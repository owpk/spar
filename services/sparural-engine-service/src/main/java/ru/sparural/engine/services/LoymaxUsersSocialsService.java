package ru.sparural.engine.services;

import ru.sparural.engine.api.enums.SocialName;

public interface LoymaxUsersSocialsService {
    void bindLoymaxSocialToUser(Long userId, String loymaxSocialUserId, SocialName socialId);
}

