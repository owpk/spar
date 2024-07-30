package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.enums.CitySelectValues;

import java.util.List;

@Data
@ToString
public class OnboxBanner {
    private Long id;
    private Integer order;
    private CitySelectValues citySelect;
    private List<City> cities;
    private Boolean isPublic;
    private Screen mobileNavigateTarget;
    private Boolean draft;
    private String title;
    private String description;
    private String url;
    private Long dateStart;
    private Long dateEnd;
}
