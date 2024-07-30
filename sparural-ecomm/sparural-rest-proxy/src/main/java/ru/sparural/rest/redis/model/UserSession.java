package ru.sparural.rest.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.sparural.rest.exception.InvalidRefreshSession;

import java.util.Date;

/**
 * @author Vorobyev Vyacheslav
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@RedisHash("user_session")
public class UserSession {
    @Id
    private String refreshTokenId;
    private Long userId;
    private String accessToken;
    private String fingerPrint;
    private Long expiresIn;
    private Long createdAt;
    private Integer count;

    public UserSession() {
        count = 0;
        createdAt = new Date().getTime();
    }

    public void incrementCount() {
        if (count == 5)
            throw new InvalidRefreshSession("too many sessions count!");
        count++;
    }
}