package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.entity.enums.CitySelectValues;

import java.util.List;

@Data
@ToString
public class InfoScreen {
    private Long id;
    private CitySelectValues citySelect;
    private List<City> cities;
    private boolean isPublic;
    private Boolean draft;
    private Long dateStart;
    private Long dateEnd;
}
