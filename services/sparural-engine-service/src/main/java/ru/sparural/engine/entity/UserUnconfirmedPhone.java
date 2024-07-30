package ru.sparural.engine.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
@NoArgsConstructor
public class UserUnconfirmedPhone {
    private Long id;
    private Long userId;
    private String phoneNumber;

    public UserUnconfirmedPhone(Long userId, String phoneNumber) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }
}
