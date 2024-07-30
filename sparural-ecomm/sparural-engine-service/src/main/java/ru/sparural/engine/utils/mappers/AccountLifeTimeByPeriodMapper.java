package ru.sparural.engine.utils.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;
import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxLifeTimeByPeriod;

@Mapper
public interface AccountLifeTimeByPeriodMapper {

    AccountLifeTimeByPeriodMapper INSTANCE = Mappers.getMapper(AccountLifeTimeByPeriodMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    AccountsLifeTimesByPeriod loymaxDtoToEntity(LoymaxLifeTimeByPeriod loymaxDto);
    AccountsLifeTimesByPeriodDto entityToDto(AccountsLifeTimesByPeriod entity);
}
