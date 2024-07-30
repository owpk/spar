package ru.sparural.engine.utils.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.entity.AccountsLifeTimesByTime;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxLifeTimesByTime;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;

@Mapper
public interface AccountLifeTimeByTimeMapper {
    AccountLifeTimeByTimeMapper INSTANCE = Mappers.getMapper(AccountLifeTimeByTimeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(source = "date", target = "date", qualifiedByName = "mapLoymaxDate")
    AccountsLifeTimesByTime loymaxDtoToEntity(LoymaxLifeTimesByTime loymaxDto);

    AccountsLifeTimesByTimeDTO entityToDto(AccountsLifeTimesByTime accountsLifeTimesByTime);

    @Named("mapLoymaxDate")
    default Long mapLoymaxDate(String date) {
        return LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(date);
    }
}
