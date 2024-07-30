package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MainBlock {
    private String code;
    private String name;
    private int order;
    private boolean showCounter;
    private boolean showEndDate;
    private boolean showPercents;
    private boolean showBillet;
}
