package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CityDto;
import ru.sparural.engine.api.dto.InfoScreenDto;
import ru.sparural.engine.entity.InfoScreen;
import ru.sparural.engine.repositories.InfoScreensRepository;
import ru.sparural.engine.services.InfoScreensService;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InfoScreensServiceImpl implements InfoScreensService {

    private final InfoScreensRepository infoScreensRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public InfoScreenDto create(InfoScreenDto data) {
        var entity = dtoMapperUtils.convert(data, InfoScreen.class);
        var created = infoScreensRepository.create(entity);
        return dtoMapperUtils.convert(created, InfoScreenDto.class);
    }

    @Override
    public InfoScreenDto update(Long id, InfoScreenDto data) {
        return createDto(infoScreensRepository.update(id, dtoMapperUtils.convert(data, InfoScreen.class)));
    }

    @Override
    public List<InfoScreenDto> list(int offset, int limit, Long city, Boolean showOnlyPublic,
                                    Long dateStart, Long dateEnd) {
        List<InfoScreenDto> listDto = new ArrayList<>();
        if (city == 0)
            infoScreensRepository.list(offset, limit, showOnlyPublic, dateStart, dateEnd).forEach(a ->
                    listDto.add(createDto(a)));
        else
            infoScreensRepository.list(offset, limit, city, showOnlyPublic, dateStart, dateEnd).forEach(a ->
                    listDto.add(createDto(a)));
        return listDto;
    }

    @Override
    public InfoScreenDto get(Long id) {
        return createDto(infoScreensRepository.get(id));
    }

    @Override
    public boolean delete(Long id) {
        return infoScreensRepository.delete(id);
    }

    @Override
    public InfoScreenDto createDto(InfoScreen infoScreen) {
        InfoScreenDto dto = new InfoScreenDto();
        dto.setId(infoScreen.getId());
        if (infoScreen.getCities() != null) {
            dto.setCities(dtoMapperUtils.convertList(CityDto.class, infoScreen.getCities()));
        }
        dto.setCitySelect(infoScreen.getCitySelect().getLiteral());
        dto.setDraft(infoScreen.getDraft());
        dto.setIsPublic(infoScreen.isPublic());
        dto.setDateEnd(infoScreen.getDateEnd());
        dto.setDateStart(infoScreen.getDateStart());
        return dto;
    }


}
