package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.PersonalOfferUserDto;
import ru.sparural.engine.entity.PersonalOfferUserEntity;

import java.util.List;


public interface PersonalOfferUserService {
    PersonalOfferUserDto getByUserIdAndOfferId(Long userId, Long offerId);

    PersonalOfferUserDto createDto(PersonalOfferUserEntity entity);

    PersonalOfferUserEntity createEntity(PersonalOfferUserDto dto);

    Boolean checkIfExist(Long userId, Long offerId);

    PersonalOfferUserDto create(PersonalOfferUserDto dto);

    void updateData(PersonalOfferUserDto dto);

    List<PersonalOfferUserDto> listByUserId(Long userId);

    List<PersonalOfferUserDto> createListDto(List<PersonalOfferUserEntity> list);
}
