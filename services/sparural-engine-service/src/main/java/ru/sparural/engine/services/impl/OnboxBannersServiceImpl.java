package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CityDto;
import ru.sparural.engine.api.dto.MobileNavigateTargetDto;
import ru.sparural.engine.api.dto.OnboxBannerDto;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.entity.OnboxBanner;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.CitiesRepository;
import ru.sparural.engine.repositories.OnboxBannersRepository;
import ru.sparural.engine.repositories.ScreensRepository;
import ru.sparural.engine.services.OnboxBannersService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OnboxBannersServiceImpl implements OnboxBannersService {

    private final OnboxBannersRepository onboxBannersRepository;
    private final CitiesRepository citiesRepository;
    private final ScreensRepository screensRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public OnboxBannerDto create(OnboxBannerDto data) throws ValidationException {
        return dtoMapperUtils.convert(
                onboxBannersRepository.create(dtoMapperUtils.convert(data, OnboxBanner.class))
                        .orElseThrow(() -> new ResourceNotFoundException("Cannot create onBoxBanner entity")),
                OnboxBannerDto.class);
    }

    @Override
    public OnboxBannerDto update(Long id, OnboxBannerDto data) throws ValidationException {
        var en = dtoMapperUtils.convert(data, OnboxBanner.class);
        var entity = onboxBannersRepository.update(id, en).orElseThrow(
                () -> new ResourceNotFoundException("Cannot update onBoxBannerEntity"));
        var dto = createDto(entity);
        setCityAndScreens(dto);
        return dto;
    }

    @Override
    public List<OnboxBannerDto> list(int offset, int limit, Long city, Boolean isPublic, Long dateStart, Long dateEnd) {
        List<OnboxBanner> result;
        if (city == 0)
            result = onboxBannersRepository.list(offset, limit, isPublic, dateStart, dateEnd);
        else
            result = onboxBannersRepository.list(offset, limit, city, isPublic, dateStart, dateEnd);

        return result.stream().map(e -> {
            var dto = createDto(e);
            setCityAndScreens(dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public OnboxBannerDto get(Long id) {
        OnboxBanner entity;
        entity = onboxBannersRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("OnBoxBanner not found with given id: " + id));
        var dto = createDto(entity);
        List<City> cities = citiesRepository.listCitiesByOnboxBannerId(dto.getId());
        dto.setCities(dtoMapperUtils.convertList(CityDto.class, cities));
        if (dto.getMobileNavigateTarget().getId() != null) {
            var screen = screensRepository.get(dto.getMobileNavigateTarget().getId())
                    .orElse(new Screen());
            dto.setMobileNavigateTarget(dtoMapperUtils.convert(screen, MobileNavigateTargetDto.class));
        }
        return dto;
    }

    @Override
    public boolean delete(Long id) {
        return onboxBannersRepository.delete(id);
    }

    private void setCityAndScreens(OnboxBannerDto bannerDto) {
        if (bannerDto.getMobileNavigateTarget().getId() != null) {
            var screen = screensRepository.get(bannerDto.getMobileNavigateTarget().getId())
                    .orElse(new Screen());
            bannerDto.setMobileNavigateTarget(dtoMapperUtils.convert(screen, MobileNavigateTargetDto.class));
        }
        List<City> cities = citiesRepository.listCitiesByOnboxBannerId(bannerDto.getId());
        bannerDto.setCities(dtoMapperUtils.convertList(CityDto.class, cities));
    }

    private OnboxBannerDto createDto(OnboxBanner entity) {
        OnboxBannerDto dto = new OnboxBannerDto();
        if (entity.getCities() != null) {
            List<CityDto> cities = entity.getCities().stream()
                    .map(x -> dtoMapperUtils.convert(x, CityDto.class))
                    .collect(Collectors.toList());
            dto.setCities(cities);
        }
        dto.setDescription(entity.getDescription());
        dto.setMobileNavigateTarget(dtoMapperUtils.convert(
                entity.getMobileNavigateTarget(), MobileNavigateTargetDto.class));

        dto.setDraft(entity.getDraft());
        dto.setIsPublic(entity.getIsPublic());
        dto.setId(entity.getId());
        dto.setCitySelect(entity.getCitySelect().getLiteral());
        dto.setOrder(entity.getOrder());
        dto.setUrl(entity.getUrl());
        dto.setTitle(entity.getTitle());
        dto.setDateStart(entity.getDateStart());
        dto.setDateEnd(entity.getDateEnd());
        return dto;
    }
}
