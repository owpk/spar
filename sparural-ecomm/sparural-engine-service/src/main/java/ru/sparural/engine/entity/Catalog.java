package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.entity.enums.CitySelectValues;
import ru.sparural.engine.entity.file.File;

import java.util.List;

@Data
@ToString
public class Catalog {
    private Long id;
    private String name;
    private CitySelectValues citySelect;
    private String url;
    private boolean draft;
    private File photo;
    private List<City> cities;
}
