package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.MerchantDto;
import ru.sparural.engine.api.dto.MerchantUpdateDto;
import ru.sparural.engine.api.dto.merchant.Attribute;
import ru.sparural.engine.api.dto.merchant.Format;
import ru.sparural.engine.api.dto.merchant.Merchants;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.LoymaxMerchant;
import ru.sparural.engine.entity.Merchant;
import ru.sparural.engine.entity.MerchantAttribute;
import ru.sparural.engine.entity.MerchantFormat;
import ru.sparural.engine.entity.enums.MerchantWorkingStatuses;
import ru.sparural.engine.repositories.AttributeRepository;
import ru.sparural.engine.repositories.MerchantFormatRepository;
import ru.sparural.engine.repositories.MerchantRepository;
import ru.sparural.engine.services.*;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final DtoMapperUtils dtoMapperUtils;
    private final AttributeRepository attributeRepository;
    private final MerchantFormatRepository formatRepository;
    private final MerchantFormatService merchantFormatService;
    private final FileDocumentService fileDocumentService;
    private final UserService userService;
    private final CitiesService citiesService;
    private final RoleService roleService;

    @Override
    public Merchants saveOrUpdate(MerchantDto createMerchantRequest) {

        formatRepository.get(createMerchantRequest.getFormatId())
                .orElseThrow(() -> new ValidationException("This format not exist"));
        Merchant merchant = merchantRepository.saveOrUpdate(createEntityFromDto(createMerchantRequest))
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create merchant record"));

        Merchants merchantDto = createDto(merchant);

        Format formatDto = dtoMapperUtils.convert(formatRepository.get(merchant.getFormatId()).get(), Format.class);
        merchantDto.setFormat(formatDto);

        if (createMerchantRequest.getAttributes() != null) {
            var attributeIds = createMerchantRequest.getAttributes();
            try {
                attributeIds.forEach(attr -> attributeRepository.saveMerchantAttributesOfMerchant(attr, merchant.getId()));
            } catch (Exception ex) {
                throw new ValidationException("This attribute not exist");
            }
            merchantRepository.insertAttributesToMerchant(merchant);
            var attributeList = merchant.getAttributes()
                    .stream()
                    .map(item -> dtoMapperUtils.convert(item, Attribute.class))
                    .collect(Collectors.toList());
            merchantDto.setAttributes(attributeList);
        }
        return merchantDto;
    }

    @Override
    public Merchant createEntityFromDto(MerchantDto createMerchantRequestDto) throws ValidationException {
        Merchant merchant = new Merchant();
        try {
            merchant.setAddress(createMerchantRequestDto.getAddress());
            merchant.setFormatId(createMerchantRequestDto.getFormatId());
            merchant.setLatitude(createMerchantRequestDto.getLatitude());
            merchant.setLongitude(createMerchantRequestDto.getLongitude());
            merchant.setTitle(createMerchantRequestDto.getTitle());
            merchant.setWorkingHoursFrom(createMerchantRequestDto.getWorkingHoursFrom());
            merchant.setWorkingHoursTo(createMerchantRequestDto.getWorkingHoursTo());
            MerchantWorkingStatuses statuses = MerchantWorkingStatuses.valueOf(createMerchantRequestDto.getWorkingStatus());
            merchant.setWorkingStatus(statuses);
            merchant.setLoymaxLocationId(createMerchantRequestDto.getLoymaxLocationId());
            merchant.setIsPublic(createMerchantRequestDto.getIsPublic());
        } catch (Exception e) {
            throw new ValidationException("Wrong request format: " + e.getLocalizedMessage());
        }
        return merchant;
    }

    @Override
    public MerchantDto createDtoFromEntity(Merchant merchant) {
        MerchantDto merchantDto = new MerchantDto();
        merchantDto.setAddress(merchant.getAddress());
        merchantDto.setLatitude(merchant.getLatitude());
        merchantDto.setLongitude(merchant.getLongitude());
        merchantDto.setFormatId(merchant.getFormatId());
        merchantDto.setTitle(merchant.getTitle());
        merchantDto.setWorkingHoursFrom(merchant.getWorkingHoursFrom());
        merchantDto.setWorkingHoursTo(merchant.getWorkingHoursTo());
        merchantDto.setWorkingStatus(merchant.getWorkingStatus().getVal());
        merchantDto.setIsPublic(merchant.getIsPublic());
        return merchantDto;
    }

    @Override
    public Merchants createDto(Merchant entity) {
        return Merchants.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .title(entity.getTitle())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .workingHoursFrom(entity.getWorkingHoursFrom())
                .workingHoursTo(entity.getWorkingHoursTo())
                .workingStatus(entity.getWorkingStatus().getVal())
                .isPublic(entity.getIsPublic())
                .loymaxLocationId(entity.getLoymaxLocationId())
                .build();
    }

    @Override
    public Merchants get(Long id, Long userId) {
        var entity = merchantRepository.get(id)
                .orElse(null);

        if (entity == null) return null;
        var merchant = Merchants.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .title(entity.getTitle())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .workingHoursFrom(entity.getWorkingHoursFrom())
                .workingHoursTo(entity.getWorkingHoursTo())
                .workingStatus((entity.getWorkingStatus() != null)? entity.getWorkingStatus().getVal() : null)
                .isPublic(entity.getIsPublic())
                .loymaxLocationId(entity.getLoymaxLocationId())
                .build();


        var attributesEntities = attributeRepository.listOfMerchants(id);
        var attributesDtos = dtoMapperUtils.convertList(Attribute.class, attributesEntities);
        attributesDtos.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_ATTRIBUTE_ICON, dto.getId());
            if (!files.isEmpty()) {
                dto.setIcon(files.get(0));
            }
        });
        merchant.setAttributes(attributesDtos);
        Long formatId = entity.getFormatId();
        if (formatId != null) {
            var formatEntity = formatRepository.get(formatId).get();
            var formatDto = merchantFormatService.createDtoFromEntity(formatEntity);


            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_FORMAT_ICON, formatDto.getId());
            if (!files.isEmpty()) {
                formatDto.setIcon(files.get(0));
            }

            merchant.setFormat(formatDto);
        }


        //     if(!attributesEntities.isEmpty()) {
        if (merchant.getWorkingStatus() != null && merchant.getWorkingStatus().equals("Open")) {
            merchant.setStatus(getCurrentStatus
                    (merchant.getWorkingHoursFrom(), merchant.getWorkingHoursTo(), userId));

            if (merchant.getStatus().equals("Closed")) {
                merchant.setCloseUntilUntil(merchant.getWorkingHoursFrom());
            } else {
                merchant.setCloseUntilUntil(null);
            }
        } else if (merchant.getWorkingStatus() != null) {
            merchant.setStatus(entity.getWorkingStatus().getVal());
        }
        //       }
        return merchant;

    }

    @Override
    public Merchants getForChecks(Long id, Long userId) {
        var entity = merchantRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        var merchant = Merchants.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .title(entity.getTitle())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .workingHoursFrom(entity.getWorkingHoursFrom())
                .workingHoursTo(entity.getWorkingHoursTo())
                .workingStatus(entity.getWorkingStatus().getVal())
                .isPublic(entity.getIsPublic())
                .loymaxLocationId(entity.getLoymaxLocationId())
                .build();


        var attributesEntities = attributeRepository.listOfMerchants(id);
        var attributesDtos = dtoMapperUtils.convertList(Attribute.class, attributesEntities);
        attributesDtos.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_ATTRIBUTE_ICON, dto.getId());
            if (!files.isEmpty()) {
                dto.setIcon(files.get(0));
            }
        });
        merchant.setAttributes(attributesDtos);
        Long formatId = entity.getFormatId();
        if (formatId != null) {
            var formatEntity = formatRepository.get(formatId).get();
            var formatDto = merchantFormatService.createDtoFromEntity(formatEntity);


            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_FORMAT_ICON, formatDto.getId());
            if (!files.isEmpty()) {
                formatDto.setIcon(files.get(0));
            }

            merchant.setFormat(formatDto);
        }


        if (!attributesEntities.isEmpty()) {
            if (merchant.getWorkingStatus() != null && merchant.getWorkingStatus().equals("Open")) {
                merchant.setStatus(getCurrentStatus
                        (merchant.getWorkingHoursFrom(), merchant.getWorkingHoursTo(), userId));

                if (merchant.getStatus().equals("Closed")) {
                    merchant.setCloseUntilUntil(merchant.getWorkingHoursFrom());
                } else {
                    merchant.setCloseUntilUntil(null);
                }
            } else if (merchant.getWorkingStatus() != null) {
                merchant.setStatus(entity.getWorkingStatus().getVal());
            }
        }
        return merchant;
    }

    @Override
    public Boolean delete(Long id) {
        merchantRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return merchantRepository.delete(id);
    }

    @Override
    public Merchants update(Long id, MerchantUpdateDto updateDto) {
        var entity = dtoMapperUtils.convert(Merchant.class, () -> updateDto);

        var attributeList = updateDto.getAttributes();

        if (attributeList != null) {
            attributeRepository.deleteAllForMerchant(id);
            try {
                attributeList.forEach(attr -> attributeRepository.saveMerchantAttributesOfMerchant(attr, id));
            } catch (Exception ex) {
                throw new ValidationException("This attribute not exist");
            }

            merchantRepository.insertAttributesToMerchant(entity);

            updateDto.getAttributes().forEach(a -> attributeRepository.get(a)
                    .orElseThrow(() -> new ValidationException("This attribute not exists")));
            var attributes = dtoMapperUtils.convertList(MerchantAttribute.class, updateDto::getAttributes);
            entity.setAttributes(attributes);
        }

        var formatId = updateDto.getFormatId();
        if (formatId != null) {
            formatRepository.get(updateDto.getFormatId())
                    .orElseThrow(() -> new ValidationException("This format not exists"));
        }
        var responseEntity = merchantRepository.update(id, entity).orElseThrow(
                () -> new ResourceNotFoundException("Resourse not found"));

        return get(responseEntity.getId(), 0L);
    }

    @Override
    public List<Merchants> list(Integer offset,
                                Integer limit,
                                Double topLeftLongitude,
                                Double topLeftLatitude,
                                Double bottomRightLongitude,
                                Double bottomRightLatitude,
                                Double userLongitude,
                                Double userLatitude,
                                String status,
                                Long[] format,
                                Long[] attributes,
                                Long userId,
                                List<String> roles) {

        Boolean isAdmin = false;
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MANAGER")) {
            isAdmin = true;
        }
        List<Merchants> list = convertListDto(merchantRepository.list(offset,
                limit,
                topLeftLongitude,
                topLeftLatitude,
                bottomRightLongitude,
                bottomRightLatitude,
                status,
                format,
                attributes,
                isAdmin));


        list.forEach(a -> {
            if (a.getLatitude() != null && a.getLongitude() != null
                    && userLatitude != null && userLongitude != null)
                a.setDistance(calculateDistance(userLongitude,
                        userLatitude,
                        a.getLongitude(),
                        a.getLatitude()));
        });


        if (userLatitude != null && userLongitude != null) {
            list = list.stream()
                    .sorted(Comparator.comparingInt(Merchants::getDistance))
                    .collect(Collectors.toList());
        }


        list.forEach(a -> {
            var currentStatus = get(a.getId(), userId).getStatus();
            if (currentStatus != null)
                a.setStatus(currentStatus);
        });

        list.forEach(a -> {
            if (a.getStatus() != null)
                a.setCloseUntilUntil(a.getStatus()
                        .equals("Closed") ? a.getWorkingHoursFrom() : null);
        });


        list.forEach(a -> {
            if (a.getFormat() != null) {
                var formatDto = a.getFormat();
                List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_FORMAT_ICON, formatDto.getId());
                if (!files.isEmpty()) {
                    formatDto.setIcon(files.get(0));
                }
                a.setFormat(formatDto);
            }
        });

        if (attributes != null) {
            list = filterMerchantByAttribute(list, attributes);
        }

        list.forEach(a -> {
            var attributesDtos = a.getAttributes();
            attributesDtos.forEach(dto -> {
                List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_ATTRIBUTE_ICON, dto.getId());
                if (!files.isEmpty()) {
                    dto.setIcon(files.get(0));
                }
            });
            a.setAttributes(attributesDtos);
        });

        if (status != null) {
            return list.stream().filter(merchants -> merchants.getStatus().equals(status)).collect(Collectors.toList());
        }
        return list;
    }


    @Override
    public List<Merchants> convertListDto(List<Merchant> list) {
        List<Merchants> merchantsList = new ArrayList<>();
        list.forEach(entity -> {
            var merchant = Merchants.builder()
                    .id(entity.getId())
                    .address(entity.getAddress())
                    .title(entity.getTitle())
                    .latitude(entity.getLatitude())
                    .longitude(entity.getLongitude())
                    .workingHoursFrom(entity.getWorkingHoursFrom())
                    .workingHoursTo(entity.getWorkingHoursTo())
                    .format(merchantFormatService.get(entity.getFormatId()))
                    .attributes(dtoMapperUtils.convertList(Attribute.class, attributeRepository.listOfMerchants(entity.getId())))
                    .workingStatus((entity.getWorkingStatus() != null)? entity.getWorkingStatus().getVal() : null)
                    .isPublic(entity.getIsPublic())
                    .loymaxLocationId(entity.getLoymaxLocationId())
                    .build();
            merchantsList.add(merchant);
        });
        return merchantsList;
    }

    @Override
    public Merchants getFromLoymaxMerchant(String locationId) {
        LoymaxMerchant entity = merchantRepository.getLoymaxMerchant(locationId)
                .orElse(null);
        if (entity != null) {
            return getByIdWithoutEx(entity.getId());
        }
        return null;
    }

    @Override
    public Merchants getByIdWithoutEx(Long id) {
        var entity = merchantRepository.get(id)
                .orElse(null);
        if (entity != null) {
            entity.setAttributes(attributeRepository.listOfMerchants(id));
            var dto = Merchants.builder()
                    .id(entity.getId())
                    .address(entity.getAddress())
                    .title(entity.getTitle())
                    .latitude(entity.getLatitude())
                    .longitude(entity.getLongitude())
                    .workingHoursFrom(entity.getWorkingHoursFrom())
                    .workingHoursTo(entity.getWorkingHoursTo())
                    .workingStatus(entity.getWorkingStatus().getVal())
                    .isPublic(entity.getIsPublic())
                    .loymaxLocationId(entity.getLoymaxLocationId())
                    .build();

            dto.setFormat(merchantFormatService.get(entity.getFormatId()));
            return dto;
        }
        return null;
    }

    @Override
    public List<MerchantFormat> batchSaveMerchantFormat(List<MerchantFormat> merchantFormats) {
        return merchantFormatService.batchSave(merchantFormats);
    }

    @Override
    public List<Merchant> batchSave(List<Merchant> merchants) {
        return merchantRepository.batchSave(merchants);
    }

    protected Integer calculateDistance(Double userLongitude,
                                        Double userLatitude,
                                        Double merchantLongitude,
                                        Double merchantLatitude) {

        var userLatRadian = userLatitude * Math.PI / 180;
        var merchLatRadian = merchantLatitude * Math.PI / 180;
        var userLongRadian = userLongitude * Math.PI / 180;
        var merchLongRadian = merchantLongitude * Math.PI / 180;

        var cosUserLat = Math.cos(userLatRadian);
        var cosMerchLat = Math.cos(merchLatRadian);
        var sinUserLat = Math.sin(userLatRadian);
        var sinMerchLat = Math.sin(merchLatRadian);

        var delta = merchLongRadian - userLongRadian;
        var cosDelta = Math.cos(delta);
        var sinDelta = Math.sin(delta);

        var y = Math.sqrt(Math.pow(cosMerchLat * sinDelta, 2)
                + Math.pow(cosUserLat * sinMerchLat
                - sinUserLat * cosMerchLat * cosDelta, 2));
        var x = sinUserLat * sinMerchLat + cosUserLat * cosMerchLat * cosDelta;

        var antipod = Math.atan2(y, x);

        return (int) (antipod * 6372795);

    }

    protected String getCurrentStatus(String workingHouseFrom, String workingHoursTo, Long userId) {
        var formatter = DateTimeFormatter.ofPattern("HH:mm");
        var hoursFrom = LocalTime.parse(workingHouseFrom, formatter);
        var hoursTo = LocalTime.parse(workingHoursTo, formatter);
        LocalTime currentTime;
        Long cityId = 0L;
        try {
            cityId = userService.getCityIdByUserId(userId);
        } catch (Exception e) {
        }
        if (userId == null || cityId == 0L) {
            currentTime = LocalTime.now(ZoneId.of("Asia/Yekaterinburg"));
        } else {
            currentTime = LocalTime.now(ZoneId.of(citiesService.getTimezoneById(userService.getCityIdByUserId(userId))));
        }
        if (hoursFrom.isAfter(hoursTo)) {
            return currentTime.isAfter(hoursTo)
                    && currentTime.isBefore(hoursFrom) ? "Closed" : "Open";
        }
        return currentTime.isAfter(hoursFrom)
                && currentTime.isBefore(hoursTo) ? "Open" : "Closed";
    }

    protected List<Merchants> filterMerchantByAttribute(List<Merchants> list,
                                                        Long[] attributes) {
        List<Merchants> filterList = new ArrayList<>();
        for (Merchants merchants : list) {
            List<Long> attributesIdList = attributeRepository.listIdOfMerchants(merchants.getId());

            if (attributesIdList.containsAll(List.of(attributes)))
                filterList.add(merchants);
        }
        return filterList;
    }

}

