package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ConfirmCodeSetting {
    private int lifetime;
    private int maxUnsuccessfulAttempts;
    private int maxInHourCount;
    private int maxDaylyCount;
}
