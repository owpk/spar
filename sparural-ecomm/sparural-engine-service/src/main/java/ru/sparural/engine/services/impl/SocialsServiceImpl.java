package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.socials.SocialDto;
import ru.sparural.engine.api.dto.socials.SocialSettingReqDto;
import ru.sparural.engine.entity.Social;
import ru.sparural.engine.repositories.SocialModelRepository;
import ru.sparural.engine.services.SocialsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialsServiceImpl implements SocialsService {
    private final SocialModelRepository socialModelRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public SocialDto update(Long id, SocialSettingReqDto data) {
        Social dataEntity = dtoMapperUtils.convert(data, Social.class);
        Social result = socialModelRepository.updateById(id, dataEntity);

        return dtoMapperUtils.convert(result, SocialDto.class);
    }

    @Override
    public List<SocialDto> get() {
        return dtoMapperUtils.convertList(SocialDto.class, socialModelRepository.getSocialList());
    }

    @Override
    public Social getBySocialName(String name) {
        return socialModelRepository.getSocialName(name).orElseThrow(
                () -> new ResourceNotFoundException("Social with name '" + name + "' not found"));
    }
}