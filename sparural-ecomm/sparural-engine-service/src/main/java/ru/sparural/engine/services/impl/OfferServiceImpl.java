package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.engine.entity.LoymaxOffers;
import ru.sparural.engine.entity.Offer;
import ru.sparural.engine.repositories.OffersRepository;
import ru.sparural.engine.services.OfferService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final DtoMapperUtils dtoMapperUtils;
    private final OffersRepository offersRepository;

    @Override
    public OfferDto createDto(Offer entity) {
        return dtoMapperUtils.convert(entity, OfferDto.class);
    }

    @Override
    public Offer createEntity(OfferDto dto) {
        return dtoMapperUtils.convert(dto, Offer.class);
    }

    @Override
    public Offer saveOrUpdateOffer(Offer entity) {
        return offersRepository.saveOrUpdate(entity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot save entity: " + entity));
    }

    @Override
    public List<Offer> batchSaveOrUpdateOffers(List<Offer> entity) {
        return offersRepository.batchSaveOrUpdateOffers(entity);
    }

    @Override
    public void createLoymaxOffer(Long id, Long loymaxId) {
        offersRepository.saveLoymaxOffer(id, loymaxId);
    }

    @Override
    public LoymaxOffers existLoymaxOffer(Long loymaxId) {
        return offersRepository.existOffer(loymaxId).orElse(null);
    }

    @Override
    public List<LoymaxOffers> getAllByFilter(Set<Long> keySet) {
        return offersRepository.getAllByLoymaxIds(keySet);
    }
}
