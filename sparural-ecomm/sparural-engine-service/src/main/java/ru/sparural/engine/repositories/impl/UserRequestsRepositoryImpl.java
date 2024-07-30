package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.UserRequestEntity;
import ru.sparural.engine.repositories.UserRequestsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.UserRequests;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class UserRequestsRepositoryImpl implements UserRequestsRepository {
    private final DSLContext dslContext;
    private final UserRequests table = UserRequests.USER_REQUESTS;

    @Transactional
    @Override
    public Optional<UserRequestEntity> create(Long userId, UserRequestEntity userRequest) {
        return dslContext
                .insertInto(UserRequests.USER_REQUESTS)
                .set(UserRequests.USER_REQUESTS.USER_ID, userId)
                .set(UserRequests.USER_REQUESTS.FULL_NAME, userRequest.getFullName())
                .set(UserRequests.USER_REQUESTS.EMAIL, userRequest.getEmail())
                .set(UserRequests.USER_REQUESTS.SUBJECT_ID, userRequest.getSubjectId())
                .set(UserRequests.USER_REQUESTS.MESSAGE, userRequest.getMessage())
                .set(UserRequests.USER_REQUESTS.DRAFT, userRequest.getDraft())
                .set(UserRequests.USER_REQUESTS.CREATED_AT, TimeHelper.currentTime())
                .onConflict(UserRequests.USER_REQUESTS.ID)
                .doNothing()
                .returning().fetchOptionalInto(UserRequestEntity.class);
    }

    @Override
    public Optional<UserRequestEntity> update(Long id, Long userId, UserRequestEntity userRequest) {
        return dslContext
                .update(UserRequests.USER_REQUESTS)
                .set(UserRequests.USER_REQUESTS.FULL_NAME, coalesce(val(userRequest.getFullName()), UserRequests.USER_REQUESTS.FULL_NAME))
                .set(UserRequests.USER_REQUESTS.EMAIL, coalesce(val(userRequest.getEmail()), UserRequests.USER_REQUESTS.EMAIL))
                .set(UserRequests.USER_REQUESTS.MESSAGE, coalesce(val(userRequest.getMessage()), UserRequests.USER_REQUESTS.MESSAGE))
                .set(UserRequests.USER_REQUESTS.SUBJECT_ID, coalesce(val(userRequest.getSubjectId()), UserRequests.USER_REQUESTS.SUBJECT_ID))
                .set(UserRequests.USER_REQUESTS.UPDATED_AT, TimeHelper.currentTime())
                .where(UserRequests.USER_REQUESTS.ID.eq(id)
                        .and(UserRequests.USER_REQUESTS.USER_ID.eq(userId)))
                .returning()
                .fetchOptionalInto(UserRequestEntity.class);
    }

    @Override
    public boolean findUserRequestIsNotDraft(Long id) {
        return dslContext.selectFrom(UserRequests.USER_REQUESTS)
                .where(UserRequests.USER_REQUESTS.ID.eq(id)
                        .and(UserRequests.USER_REQUESTS.DRAFT.eq(false)))
                .fetchOptionalInto(UserRequestEntity.class).isPresent();
    }

    @Override
    public Optional<UserRequestEntity> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(UserRequestEntity.class);
    }

    @Override
    public List<UserRequestEntity> index(int offset, int limit, String search, List<Long> subjectsIds) {
        return dslContext
                .selectFrom(table)
                .where(table.FULL_NAME.like(search)
                        .or(table.MESSAGE.like(search))
                        .or(table.SUBJECT_ID.in(subjectsIds)))
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(UserRequestEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }
}
