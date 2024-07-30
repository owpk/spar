package ru.sparural.backgrounds.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.sparural.backgrounds.SparuralKafkaTopics;
import ru.sparural.backgrounds.cache.CacheNames;
import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.user.LoymaxUserDto;
import ru.sparural.engine.api.dto.user.RoleDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class EngineUserServiceImpl implements EngineUserService {
    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final SparuralKafkaTopics kafkaTopics;

    @Override
    @Cacheable(cacheNames = CacheNames.SPAR_USERS_CACHE)
    public List<UserDto> loadAllUsers(Long roleId) {
        var search = new UserSearchFilterDto();
        search.setRole(List.of(roleId));
        return kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineTopicName())
                .withAction("users/index")
                .withRequestBody(search)
                .sendForEntity();
    }

    @Override
    @Cacheable(cacheNames = CacheNames.LOYMAX_USERS_CACHE)
    public List<LoymaxUserDto> loadAllLoymaxUsers() {
        return kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineTopicName())
                .withAction("users/index-loymax")
                .sendForEntity();
    }

    @Override
    public List<RoleDto> listRoles() {
        return kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineTopicName())
                .withAction("user/roles")
                .sendForEntity();
    }
}
