package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.PersonalOfferCreateDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.PersonalOfferUpdateDto;
import ru.sparural.engine.entity.PersonalOffer;

import java.util.List;

public interface PersonalOffersService {
    List<PersonalOfferDto> list(Integer offset, Integer limit);

    PersonalOfferDto get(Long id);

    PersonalOfferDto create(PersonalOfferCreateDto personalOfferDto);

    PersonalOfferDto update(Long id, PersonalOfferUpdateDto personalOfferDto);

    Boolean delete(Long id);

    PersonalOfferDto createDto(PersonalOffer entity);

    List<PersonalOfferDto> createListDto(List<PersonalOffer> list);

    PersonalOffer createEntity(PersonalOfferDto dto);

    PersonalOfferDto getByAttribute(String attribute);
}
