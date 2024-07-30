package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserRequestsSubject;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserRequestsSubjectsRepository {
    List<UserRequestsSubject> getList(int offset, int limit);

    Optional<UserRequestsSubject> create(UserRequestsSubject date);

    Boolean delete(Long id);

    Optional<UserRequestsSubject> get(Long id) throws ResourceNotFoundException;

    Optional<UserRequestsSubject> update(Long id, UserRequestsSubject data) throws ResourceNotFoundException;

    boolean checkIfUserRequestsSubjectsExistsWithId(Long id);

    List<Long> findIdsByName(String search);
}
