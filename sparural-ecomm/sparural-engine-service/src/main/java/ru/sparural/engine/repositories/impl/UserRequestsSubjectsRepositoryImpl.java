package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserRequestsSubject;
import ru.sparural.engine.repositories.UserRequestsSubjectsRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.UserRequestsSubjects;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRequestsSubjectsRepositoryImpl implements UserRequestsSubjectsRepository {

    private final DSLContext dslContext;

    @Override
    public List<UserRequestsSubject> getList(int offset, int limit) {
        return dslContext
                .selectFrom(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .orderBy(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.CREATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(UserRequestsSubject.class);
    }

    @Override
    public Optional<UserRequestsSubject> create(UserRequestsSubject date) {
        return dslContext
                .insertInto(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .set(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.NAME, date.getName())
                .set(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.CREATED_AT, TimeHelper.currentTime())
                .set(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(UserRequestsSubject.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext
                .delete(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .where(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public Optional<UserRequestsSubject> get(Long id) throws ResourceNotFoundException {
        return dslContext
                .selectFrom(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .where(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.ID.eq(id))
                .fetchOptionalInto(UserRequestsSubject.class);
    }

    @Override
    public Optional<UserRequestsSubject> update(Long id, UserRequestsSubject date) throws ResourceNotFoundException {
        return dslContext
                .update(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .set(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.NAME, date.getName())
                .set(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.UPDATED_AT, TimeHelper.currentTime())
                .where(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.ID.eq(id))
                .returning()
                .fetchOptionalInto(UserRequestsSubject.class);
    }

    @Override
    public boolean checkIfUserRequestsSubjectsExistsWithId(Long id) {
        return dslContext.selectFrom(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .where(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.ID.eq(id))
                .fetchOptional().isPresent();
    }

    @Override
    public List<Long> findIdsByName(String search) {
        return dslContext.select(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.ID)
                .from(UserRequestsSubjects.USER_REQUESTS_SUBJECTS)
                .where(UserRequestsSubjects.USER_REQUESTS_SUBJECTS.NAME.like(search))
                .fetchInto(Long.class);
    }
}
