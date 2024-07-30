package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NotificationsEntity {
    private Long id;
    private Long userId;
    private String title;
    private String body;
    private Long sendedAt;
    private Boolean isReaded;
    private String type;
    private Long screenId;
    private Long merchantId;
}
