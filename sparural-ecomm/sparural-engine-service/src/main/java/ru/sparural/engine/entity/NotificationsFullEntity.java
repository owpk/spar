package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class NotificationsFullEntity {
    private Long id;
    private Long userId;
    private String title;
    private String body;
    private Long sendedAt;
    private Boolean isReaded;
    private String type;
    private Screen screen;
    private Merchant merchant;
}
