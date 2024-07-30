package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Deregistration {
    private Long id;
    private Long userId;
    private String message;
    private String reason;
}
