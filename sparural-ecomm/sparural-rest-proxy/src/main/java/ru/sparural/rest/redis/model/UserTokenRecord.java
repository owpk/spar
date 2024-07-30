package ru.sparural.rest.redis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author Vorobyev Vyacheslav
 */
@ToString
@RedisHash("token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenRecord {
    @Id
    private String userId;
    private String jwtToken;
}
