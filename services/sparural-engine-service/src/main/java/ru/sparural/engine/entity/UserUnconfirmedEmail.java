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
public class UserUnconfirmedEmail {
    private Long id;
    private Long userId;
    private String email;

    public UserUnconfirmedEmail(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
