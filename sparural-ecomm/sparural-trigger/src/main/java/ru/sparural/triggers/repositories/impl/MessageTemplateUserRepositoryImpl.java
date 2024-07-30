package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.MessagesTemplateUser;
import ru.sparural.triggers.entities.MessageTemplateUser;
import ru.sparural.triggers.utils.TimeHelper;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageTemplateUserRepositoryImpl implements ru.sparural.triggers.repositories.MessageTemplateUserRepository {
    private final DSLContext dslContext;
    private final MessagesTemplateUser table = MessagesTemplateUser.MESSAGES_TEMPLATE_USER;

    @Override
    public List<MessageTemplateUser> findByMessageTemplateId(Long messageTemplateId) {
        return dslContext.selectFrom(table)
                .where(table.MESSAGE_TEMPLATE_ID.eq(messageTemplateId))
                .fetchInto(MessageTemplateUser.class);
    }

    @Override
    public void save(Long userId, Long messageTemplateId) {
        dslContext.insertInto(table)
                .set(table.USER_ID, userId)
                .set(table.MESSAGE_TEMPLATE_ID, messageTemplateId)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.MESSAGE_TEMPLATE_ID)
                .doUpdate()
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetch();
    }

    @Override
    public void delete(Long messageTemplateId) {
        dslContext.delete(table)
                .where(table.MESSAGE_TEMPLATE_ID.eq(messageTemplateId))
                .execute();
    }

    @Override
    public void batchBind(Set<Long> users, Long id) {
        var insert =
                dslContext.insertInto(table, table.USER_ID,
                        table.MESSAGE_TEMPLATE_ID,
                        table.CREATED_AT);

        for (var recId : users)
            insert = insert.values(recId, id, TimeHelper.currentTime());

        var insertStep = insert.onConflict(table.USER_ID, table.MESSAGE_TEMPLATE_ID)
                .doUpdate()
                .set(table.UPDATED_AT, TimeHelper.currentTime());

        insertStep.execute();
    }
}
