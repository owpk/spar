package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Setting {
    private int timezone;
    private int notificationsFrequency;
}
