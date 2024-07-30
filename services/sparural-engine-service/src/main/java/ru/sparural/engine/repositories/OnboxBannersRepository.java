package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.OnboxBanner;
import ru.sparural.engine.services.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface OnboxBannersRepository {

    Optional<OnboxBanner> create(OnboxBanner data) throws ValidationException;

    boolean delete(Long id);

    Optional<OnboxBanner> update(Long id, OnboxBanner data) throws ValidationException;

    Optional<OnboxBanner> get(Long id);

    List<OnboxBanner> list(int offset, int limit, boolean isPublic, Long dateStart, Long dateEnd);

    List<OnboxBanner> list(int offset, int limit, Long city, boolean isPublic, Long dateStart, Long dateEnd);

}
