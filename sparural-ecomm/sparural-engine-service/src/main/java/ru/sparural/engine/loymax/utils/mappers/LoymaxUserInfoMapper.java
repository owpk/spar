package ru.sparural.engine.loymax.utils.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfoSystem;

@Mapper
public interface LoymaxUserInfoMapper {

    LoymaxUserInfoMapper INSTANCE = Mappers.getMapper(LoymaxUserInfoMapper.class);

    @Mapping(target = "personUid", source = "uid")
    @Mapping(target = "phoneNumber", source = "phone.currentValue")
    @Mapping(target = "email", source = "email.currentValue")
    @Mapping(target = "gender", ignore = true)
    LoymaxUserInfo loymaxDtoToEntity(LoymaxUserInfoSystem loymaxDto);

}
