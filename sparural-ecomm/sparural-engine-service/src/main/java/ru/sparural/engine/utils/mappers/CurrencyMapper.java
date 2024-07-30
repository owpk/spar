package ru.sparural.engine.utils.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.sparural.engine.api.dto.NameCases;
import ru.sparural.engine.entity.CurrencyEntity;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;

@Mapper
public interface CurrencyMapper {
    ObjectMapper objMapper = new ObjectMapper();

    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);
    CurrencyEntity dtoToEntity(ru.sparural.engine.api.dto.Currency dto);

    @Mapping(target = "nameCases", source = "nameCases", qualifiedByName = "mapNameCases")
    ru.sparural.engine.api.dto.Currency entityToDto(CurrencyEntity currency);

    @Mapping(target = "externalId", source = "uid")
    CurrencyEntity loymaxEntityToModel(LoymaxCurrency loymaxCurrency);

    @Named("mapNameCases")
    default NameCases map(Object value) {
        String val = (String) value;
        try {
            return objMapper.readValue(val, NameCases.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
