package ru.sparural.engine.providers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.LoymaxFileDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.engine.api.dto.main.CounterOfferDto;
import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.LoymaxOffers;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.entity.file.FileWithStorageEntity;
import ru.sparural.engine.gateways.FileServiceGateway;
import ru.sparural.engine.loymax.LoymaxConstants;
import ru.sparural.engine.loymax.rest.dto.LoymaxImage;
import ru.sparural.engine.loymax.rest.dto.offer.LoymaxOffer;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.repositories.OffersCountersRepository;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.LoymaxFilesService;
import ru.sparural.engine.services.OfferService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.StatusException;
import ru.sparural.kafka.utils.SparuralKafkaBadKafkaResponseException;

import java.nio.file.FileSystemException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static ru.sparural.engine.api.enums.FileDocumentTypeField.OFFER_PHOTO;
import static ru.sparural.engine.api.enums.FileDocumentTypeField.OFFER_PREVIEW;

@Service
@AllArgsConstructor
@Slf4j
public class OffersProvider {
    private final LoymaxConstants loymaxConstants;
    private final LoymaxService loymaxService;
    private final OfferService offerService;
    private final FileDocumentService fileDocumentService;
    private final FileServiceGateway fileServiceGateway;
    private final OffersCountersRepository offersCounterRepository;

    private final ThreadLocal<List<FileGatewayTask>> fileGatewayTasks = new ThreadLocal<>();
    private final LoymaxFilesService loymaxFilesService;

    public OfferDto fetchAndSaveOfferByLoymaxOfferId(Long loymaxOfferId) {
        fileGatewayTasks.set(new LinkedList<>());

        List<LoymaxOffer> loymaxOffers = loymaxService.getOfferById(loymaxOfferId);
        if (loymaxOffers.isEmpty())
            throw new StatusException(String.format("No loymax offer present with id: %s, system error", loymaxOfferId), 500);
        var sparOffers = loymaxOffers.stream()
                .map(lo -> {
                            CounterOfferDto counterDto = offersCounterRepository
                                    .getCounterOfferData(lo.getId()).orElse(null);
                            var sparOffer = mapLoymaxOfferToSparOffer(lo);
                            sparOffer.setCounter(counterDto);
                            return sparOffer;
                        }
                ).collect(Collectors.toList());
        var savedOffers = saveOfferToDB(sparOffers);
        var result = setImages(savedOffers);
        return result.get(0);
    }

    public List<OfferDto> getOfferList(Integer offset, Integer limit, Long userId) throws ResourceNotFoundException {
        fileGatewayTasks.set(new LinkedList<>());

        var loymaxOffers = getOffersFromLoymax(userId);
        var sparOffers = loymaxOffers.stream()
                .map(loymaxOffer -> {
                    var counterDto = offersCounterRepository
                            .getCounterOfferData(loymaxOffer.getId())
                            .orElse(null);
                    var sparOffer = mapLoymaxOfferToSparOffer(loymaxOffer);
                    sparOffer.setCounter(counterDto);
                    return sparOffer;
                }
                ).collect(Collectors.toList());

        long currentTime = Instant.now().getEpochSecond();
        var filteredSparOffers = sparOffers.stream()
                .filter(offerDto -> offerDto.getBegin() < currentTime)
                .filter(offerDto -> offerDto.getEnd() == null || offerDto.getEnd() > -currentTime)
                .collect(Collectors.toList());

        List<OfferDto> result = saveOfferToDB(filteredSparOffers);

        result.subList(
                Math.min(result.size(), offset),
                Math.min(result.size(), offset + limit));

        return setImages(result);
    }

    private List<OfferDto> setImages(List<OfferDto> sparOffers) {
        var data = sparOffers
                .stream()
                .map(offerDto -> checkOfferImages(offerDto, OFFER_PHOTO)) //fetch images for offers
                .map(offerDto -> checkOfferImages(offerDto, OFFER_PREVIEW))
                .collect(Collectors.toList());
        try {
            //If file service have not image, need load from loymax.
            //We do it in thread pool. Need wait wile all threads done.
            List<CompletableFuture<FileInfoDto>> futureList = fileGatewayTasks.get()
                    .stream()
                    .map(FileGatewayTask::getFuture)
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new))
                    .thenApply(v -> futureList
                            .stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()))
                    .get(1, TimeUnit.MINUTES);

            fileGatewayTasks.get()
                    .forEach(task -> {
                        String loymaxId = task.getOffer().getPhoto().getUuid();

                        setImageByField(
                            task.getOffer(),
                            task.getField(),
                            mapFileInfoDtoToFileDto(task.getFuture().join())
                        );

                        loymaxFieldIdInsertData(task.getOffer(), task.getField(), loymaxId);
                    });

        } catch (ExecutionException | InterruptedException | TimeoutException | SparuralKafkaBadKafkaResponseException e ) {
            if(e.getCause() instanceof SparuralKafkaBadKafkaResponseException) {
                throw new RuntimeException(
                    new FileSystemException(
                        ((SparuralKafkaBadKafkaResponseException)e.getCause())
                                .getKafkaResponseMessage()
                                .getPayload()
                                .toString()
                        )
                );
            } else {
                throw new RuntimeException(e.getCause());
            }
        } finally {
            fileGatewayTasks.remove();
        }
        return data;
    }

    /**
     * Get all existing 'offers' from db by 'loymax offer' id, if there is no 'offer' then
     * create it and bind 'offer' id to 'loymax offer' id
     */
    private List<OfferDto> saveOfferToDB(List<OfferDto> offersDto) {
        Map<Long, OfferDto> offerDtoMap = offersDto.stream()
                .collect(Collectors.toMap(OfferDto::getLoymaxId, x -> x));

        List<LoymaxOffers> loymaxOffersFromDB = offerService
                .getAllByFilter(offerDtoMap.keySet());

        var dtosToUpdate = loymaxOffersFromDB.stream()
                .map(x -> {
                    var dto = offerDtoMap.get(x.getLoymaxOfferId());
                    dto.setId(x.getOfferId());
                    return dto;
                })
                .collect(Collectors.toList());

        loymaxOffersFromDB.stream()
                .map(LoymaxOffers::getLoymaxOfferId)
                .collect(Collectors.toList())
                .forEach(offerDtoMap.keySet()::remove);

        offerService
            .batchSaveOrUpdateOffers(
                dtosToUpdate.stream()
                    .map(offerService::createEntity)
                    .collect(Collectors.toList())
            );

        offerDtoMap.values().forEach(loymaxOffer -> {
            var entityOffer = offerService.saveOrUpdateOffer(offerService.createEntity(loymaxOffer));
            offerService.createLoymaxOffer(entityOffer.getId(), loymaxOffer.getLoymaxId());
            loymaxOffer.setId(entityOffer.getId());
        });
        return offersDto;
    }

    private void loymaxFieldIdInsertData(OfferDto offerDto, FileDocumentTypeField field, String loymaxFileId) {
        switch (field) {
            case OFFER_PHOTO:
                if (offerDto.getPhoto() != null)
                    loymaxFilesService.save(new LoymaxFileDto(null, loymaxFileId, offerDto.getPhoto().getUuid()));
                break;
            case OFFER_PREVIEW:
                if (offerDto.getPreview() != null)
                    loymaxFilesService.save(new LoymaxFileDto(null, loymaxFileId, offerDto.getPreview().getUuid()));
                break;
        }
    }

    private OfferDto checkOfferImages(OfferDto offerDto, FileDocumentTypeField field) {
        if(offerDto.getPhoto() == null || offerDto.getPreview() == null)
            return offerDto;

        fileDocumentService.getFileByDocumentId(field, offerDto.getId())
                .stream()
                .findFirst()
                .ifPresentOrElse(
                    image -> setImageByField(offerDto, field, image),
                    () -> fetchImageFromGateway(offerDto, field)
                );

        return offerDto;
    }

    private void setImageByField(OfferDto offerDto, FileDocumentTypeField field, FileDto image) {
        switch (field) {
            case OFFER_PHOTO:
                offerDto.setPhoto(image);
                break;
            case OFFER_PREVIEW:
                offerDto.setPreview(image);
                break;
        }
    }

    private FileDto mapFileInfoDtoToFileDto(FileInfoDto entity) {
        FileDto dto = new FileDto();
        dto.setExt(entity.getExt());
        dto.setMime(entity.getMime());
        dto.setSize(entity.getSize());
        dto.setUrl(loymaxConstants.getLoymaxFiles() + "/" + entity.getId().toString());
        dto.setName(entity.getName());
        dto.setUuid(entity.getId().toString());
        return dto;
    }

    private void fetchImageFromGateway(OfferDto offerDto, FileDocumentTypeField field) {
        switch (field) {
            case OFFER_PHOTO:
                var futurePhoto = fileServiceGateway.uploadFileFromUrlAsync(
                        offerDto.getPhoto().getUrl(), OFFER_PHOTO, offerDto.getId());
                var taskPhoto = new FileGatewayTask(futurePhoto, offerDto, field);
                fileGatewayTasks.get().add(taskPhoto);
                break;
            case OFFER_PREVIEW:
                var futurePreview = fileServiceGateway.uploadFileFromUrlAsync(
                        offerDto.getPreview().getUrl(), OFFER_PREVIEW, offerDto.getId());
                var taskPreview = new FileGatewayTask(futurePreview, offerDto, field);
                fileGatewayTasks.get().add(taskPreview);
                break;
        }
    }

    private List<LoymaxOffer> getOffersFromLoymax(@Nullable Long userId) {
        var filter = new String[]{
                "filter.type=Original",
                "filter.offerState=Active",
                "filter.count=" + Integer.MAX_VALUE
        };

        List<LoymaxOffer> loymaxOffers;
        var ref = new Object() {
            LoymaxUser loymaxUser = null;
        };
        if (userId != null) {
            try {
                ref.loymaxUser = loymaxService.getByLocalUserId(userId);
                loymaxOffers = loymaxService.getOffers(ref.loymaxUser.getToken(), filter);
            } catch (ResourceNotFoundException e) {
                loymaxOffers = loymaxService.getOffers(filter);
            }
        } else {
            loymaxOffers = loymaxService.getOffers(filter);
        }
        return loymaxOffers;
    }

    private OfferDto mapLoymaxOfferToSparOffer(LoymaxOffer loymaxOffer) {
        return OfferDto.builder()
                .title(loymaxOffer.getTitle())
                .description(loymaxOffer.getDescription())
                .begin((loymaxOffer.getBegin() != null) ? LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(loymaxOffer.getBegin()) : null)
                .end((loymaxOffer.getEnd() != null) ? LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(loymaxOffer.getEnd()) : null)
                .shortDescription(loymaxOffer.getShortDescription())
                .loymaxId(loymaxOffer.getId())
                .photo(this.parseLoymaxPhotoData(loymaxOffer, "Основное"))
                .preview(this.parseLoymaxPhotoData(loymaxOffer, "Превью"))
                .build();
    }

    @Nullable
    private FileDto parseLoymaxPhotoData(LoymaxOffer loymaxOffer, String type) {
        Optional<LoymaxImage> photo =
                loymaxOffer.getImages().stream().filter(entry -> entry.getDescription().equals(type)).findAny();

        if(photo.isEmpty())
            return null;

        return new FileDto(
                photo.orElse(LoymaxImage.builder().fileId("").build()).getFileId(), "loymaxImg", "", 10L, "",
                loymaxConstants.getLoymaxFiles() + "/"
                        + photo.orElse(LoymaxImage.builder().fileId("").build()).getFileId()
        );
    }

    @Data
    @AllArgsConstructor
    private static class FileGatewayTask {
        private CompletableFuture<FileInfoDto> future;
        private OfferDto offer;
        private FileDocumentTypeField field;
    }

}
