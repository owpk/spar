package ru.sparural.engine.loymax.utils.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.loymax.rest.dto.TokenExchangeResponse;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;

import java.util.concurrent.TimeUnit;

@Mapper
public interface LoymaxUserMapper {

    LoymaxUserMapper INSTANCE = Mappers.getMapper(LoymaxUserMapper.class);

    @Mapping(target = "loymaxUserId", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "setMobileApplicationInstalled", ignore = true)
    LoymaxUser mapLoymaxUser(LoymaxUserInfo loymaxUserInfo);

    default LoymaxUser mapLoymaxUserWithId(Long userId, LoymaxUserInfo loymaxUserInfo) {
       var mapped = mapLoymaxUser(loymaxUserInfo);
       mapped.setUserId(userId);
       return mapped;
    }

    default LoymaxUser mapLoymaxUserWithIdAndToken(Long userId, LoymaxUserInfo loymaxUserInfo,
                                           TokenExchangeResponse tokenExchangeResponse) {
        var mapped = mapLoymaxUserWithId(userId, loymaxUserInfo);
        mapped.setExpiresAt(generateExpires(tokenExchangeResponse.getExpiresIn()));
        mapped.setToken(tokenExchangeResponse.getAccessToken());
        mapped.setRefreshToken(tokenExchangeResponse.getRefreshToken());
        return mapped;
    }

    @Named("generate_expiration")
    default Long generateExpires(Long expiresIn) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                + (expiresIn != null ? expiresIn : 36000);
    }
}
