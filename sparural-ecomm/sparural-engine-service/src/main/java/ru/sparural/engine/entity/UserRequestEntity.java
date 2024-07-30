package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRequestEntity {
    private Long id;
    private String fullName;
    private String email;
    private Long subjectId;
    private String message;
    private Boolean draft = false;
    private Long userId;
}
