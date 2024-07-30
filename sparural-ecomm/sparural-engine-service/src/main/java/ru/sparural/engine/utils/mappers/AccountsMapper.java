package ru.sparural.engine.utils.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.entity.AccountFull;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;

@Mapper(uses = {AccountLifeTimeByPeriodMapper.class, AccountLifeTimeByTimeMapper.class, CurrencyMapper.class})
public interface AccountsMapper {
    AccountsMapper INSTANCE = Mappers.getMapper(AccountsMapper.class);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currencyId", ignore = true)
    Account loymaxDtoToPojoEntity(LoymaxUserBalanceInfoDto loymaxDto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountLifeTimeByTime", ignore = true)
    @Mapping(target = "accountLifeTimeByPeriod", ignore = true)
    AccountFull loymaxDtoToFullEntity(LoymaxUserBalanceInfoDto loymaxDto);

    @Mapping(target = "accountsLifeTimesByTime", ignore = true)
    @Mapping(target = "accountsLifeTimesByPeriod", ignore = true)
    AccountsDto entityToDto(AccountFull entity);

    @Mapping(target = "accountLifeTimeByTime", ignore = true)
    @Mapping(target = "accountLifeTimeByPeriod", ignore = true)
    AccountFull dtoToEntity(AccountsDto dto);
}
