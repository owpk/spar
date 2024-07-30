package ru.sparural.rest.dto;

import lombok.*;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TokenRefreshRequest {
    private String refreshToken;
}
