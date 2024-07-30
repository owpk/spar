package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.PersonalOfferUserEntity;

import java.util.List;
import java.util.Optional;

public interface PersonalOfferUserRepository {
    Optional<PersonalOfferUserEntity> getByUserIdAndOfferId(Long userId, Long offerId);

    Optional<PersonalOfferUserEntity> create(PersonalOfferUserEntity personalOfferUserEntity);

    Optional<PersonalOfferUserEntity> updateData(PersonalOfferUserEntity personalOfferUserEntity);

    List<PersonalOfferUserEntity> listByUserId(Long userId);
}
