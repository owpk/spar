package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.socials.SocialDto;
import ru.sparural.engine.api.dto.socials.SocialSettingReqDto;
import ru.sparural.engine.entity.Social;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface SocialsService {
    SocialDto update(Long id, SocialSettingReqDto data);

    List<SocialDto> get();

    Social getBySocialName(String name);
}
