package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.MessagesTemplateUsersGroup;
import ru.sparural.triggers.entities.MessageTemplateUsersGroup;
import ru.sparural.triggers.utils.TimeHelper;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageTemplateUsersGroupRepositoryImpl implements ru.sparural.triggers.repositories.MessageTemplateUsersGroupRepository {
    private final DSLContext dslContext;
    private final MessagesTemplateUsersGroup table = MessagesTemplateUsersGroup.MESSAGES_TEMPLATE_USERS_GROUP;


    @Override
    public List<MessageTemplateUsersGroup> list(Long messageTemplate) {
        return dslContext.selectFrom(table)
                .where(table.MESSAGE_TEMPLATE.eq(messageTemplate))
                .fetchInto(MessageTemplateUsersGroup.class);
    }

    @Override
    public void save(Long userGroupId, Long messageTemplateId) {
        dslContext.insertInto(table)
                .set(table.USERS_GROUP_ID, userGroupId)
                .set(table.MESSAGE_TEMPLATE, messageTemplateId)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USERS_GROUP_ID, table.MESSAGE_TEMPLATE)
                .doUpdate()
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetch();

    }

    @Override
    public void delete(Long messageTemplateId) {
        dslContext.delete(table)
                .where(table.MESSAGE_TEMPLATE.eq(messageTemplateId))
                .execute();
    }

    @Override
    public void batchBind(Set<Long> usersGroup, Long id) {
        var insert =
                dslContext.insertInto(table, table.USERS_GROUP_ID,
                        table.MESSAGE_TEMPLATE,
                        table.CREATED_AT);

        for (var recId : usersGroup)
            insert = insert.values(recId, id, TimeHelper.currentTime());

        var insertStep = insert.onConflict(table.USERS_GROUP_ID, table.MESSAGE_TEMPLATE)
                .doUpdate()
                .set(table.UPDATED_AT, TimeHelper.currentTime());

        insertStep.execute();
    }

}
