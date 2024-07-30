package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.OnboxBannerDto;
import ru.sparural.engine.services.exception.ValidationException;

import java.util.List;

public interface OnboxBannersService {

    OnboxBannerDto create(OnboxBannerDto data) throws ValidationException;

    OnboxBannerDto update(Long id, OnboxBannerDto data) throws ValidationException;

    List<OnboxBannerDto> list(int offset, int limit, Long city, Boolean isPublic, Long dateStart, Long dateEnd);

    OnboxBannerDto get(Long id);

    boolean delete(Long id);

}