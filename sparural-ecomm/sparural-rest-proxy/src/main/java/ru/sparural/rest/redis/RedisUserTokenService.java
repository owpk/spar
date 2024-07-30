package ru.sparural.rest.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.rest.exception.InvalidRefreshSession;
import ru.sparural.rest.redis.model.UserSession;
import ru.sparural.rest.redis.repository.UserSessionRepository;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RedisUserTokenService {
    private final UserSessionRepository userSessionRepository;

    public void saveOrUpdate(UserSession userSessionEntity) {
        userSessionRepository.save(userSessionEntity);
    }

    public Optional<UserSession> getByRefreshToken(String refreshToken) {
        return userSessionRepository.findById(refreshToken);
    }

    public void validateSessionCount(String refreshToken) {
        var existingSession = userSessionRepository.findById(refreshToken)
                .orElseThrow(() -> new InvalidRefreshSession("Session does not exists"));
        validateSessionCount(existingSession);
    }

    public void validateSessionCount(UserSession userSession) {
        if (userSession.getCount() > 5)
            throw new InvalidRefreshSession("too much sessions detected! try to re login");
    }

}