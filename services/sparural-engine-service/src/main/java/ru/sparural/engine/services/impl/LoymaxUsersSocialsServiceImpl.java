package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.enums.SocialName;
import ru.sparural.engine.entity.LoymaxUsersSocialsEntity;
import ru.sparural.engine.repositories.LoymaxUsersSocialsRepository;
import ru.sparural.engine.services.LoymaxUsersSocialsService;

@Service
@RequiredArgsConstructor
public class LoymaxUsersSocialsServiceImpl implements LoymaxUsersSocialsService {
    private final LoymaxUsersSocialsRepository repository;
    private final SocialsServiceImpl socialsService;

    @Override
    public void bindLoymaxSocialToUser(Long userId, String loymaxSocialUserId, SocialName socialName) {
        var entity = new LoymaxUsersSocialsEntity();
        entity.setLoymaxSocialUserId(loymaxSocialUserId);
        entity.setUserId(userId);
        entity.setSocialId(socialsService.getBySocialName(socialName.getName()).getId());
        repository.saveOrUpdate(entity);
    }
}

