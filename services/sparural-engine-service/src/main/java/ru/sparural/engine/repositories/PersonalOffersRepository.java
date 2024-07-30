package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.PersonalOffer;

import java.util.List;
import java.util.Optional;

public interface PersonalOffersRepository {
    List<PersonalOffer> fetch(int offset, int limit);

    Optional<PersonalOffer> get(Long id);

    Optional<PersonalOffer> getByAttribute(String attribute);

    Optional<PersonalOffer> create(PersonalOffer entity);

    Optional<PersonalOffer> update(Long id, PersonalOffer entity);

    Boolean delete(Long id);
}
