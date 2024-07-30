package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.PersonalOfferCreateDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.PersonalOfferUpdateDto;
import ru.sparural.engine.entity.PersonalOffer;
import ru.sparural.engine.repositories.PersonalOffersRepository;
import ru.sparural.engine.services.PersonalOffersService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalOffersServiceImpl implements PersonalOffersService {
    private final PersonalOffersRepository personalOffersRepository;
    private final DtoMapperUtils mapperUtils;

    @Override
    public List<PersonalOfferDto> list(Integer offset, Integer limit) {
        return createListDto(personalOffersRepository.fetch(offset, limit));
    }

    @Override
    public PersonalOfferDto get(Long id) {
        return createDto(personalOffersRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public PersonalOfferDto create(PersonalOfferCreateDto personalOfferDto) {
        var entity = mapperUtils.convert(PersonalOffer.class, () -> personalOfferDto);
        return createDto(personalOffersRepository.create(entity)
                .orElseThrow(() -> new ServiceException("Failed to create personal offers")));
    }

    @Override
    public PersonalOfferDto update(Long id, PersonalOfferUpdateDto personalOfferDto) {
        var entity = mapperUtils.convert(PersonalOffer.class, () -> personalOfferDto);
        return createDto(personalOffersRepository.update(id, entity)
                .orElseThrow(() -> new ServiceException("Failed to create personal offers")));
    }

    @Override
    public Boolean delete(Long id) {
        personalOffersRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return personalOffersRepository.delete(id);
    }

    @Override
    public PersonalOfferDto createDto(PersonalOffer entity) {
        return mapperUtils.convert(PersonalOfferDto.class, () -> entity);
    }

    @Override
    public List<PersonalOfferDto> createListDto(List<PersonalOffer> list) {
        return mapperUtils.convertList(PersonalOfferDto.class, () -> list);
    }

    @Override
    public PersonalOffer createEntity(PersonalOfferDto dto) {
        return mapperUtils.convert(PersonalOffer.class, () -> dto);
    }

    @Override
    public PersonalOfferDto getByAttribute(String attribute) {
        PersonalOffer personalOffer = personalOffersRepository.getByAttribute(attribute).orElse(null);
        if (personalOffer == null) {
            return null;
        }
        return createDto(personalOffer);
    }

}
