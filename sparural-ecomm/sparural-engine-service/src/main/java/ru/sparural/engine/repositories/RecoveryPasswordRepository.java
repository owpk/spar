package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.RecoveryPassword;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecoveryPasswordRepository {
    Optional<RecoveryPassword> create(RecoveryPassword data);

    Optional<RecoveryPassword> getByToken(String token);
}
