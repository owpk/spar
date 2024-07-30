package ru.sparural.rest.redis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sparural.rest.redis.model.UserSession;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserSessionRepository extends CrudRepository<UserSession, String> {
}