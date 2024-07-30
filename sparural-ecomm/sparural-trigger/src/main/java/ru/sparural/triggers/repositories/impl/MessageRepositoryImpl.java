package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.enums.MessageStatuses;
import ru.sparural.tables.Messages;
import ru.sparural.triggers.entities.Message;
import ru.sparural.triggers.repositories.MessageRepository;
import ru.sparural.triggers.utils.TimeHelper;

@Service
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {
    private final DSLContext dslContext;
    private final Messages table = Messages.MESSAGES;

    @Override
    public void save(Message message) {
//        dslContext.insertInto(table)
//                .set(table.STATUS, message.getMessageStatuses())
//                .set(table.MESSAGETEMPLATEID, message.getMessageTemplateId())
//                .set(table.USERID, message.getUserId())
//                .set(table.UUID, message.getUuid())
//                .set(table.SENDEDAT, message.getSendedAt())
//                .set(table.TRIGGERLOGID, message.getTriggerLogId())
//                .set(table.CREATEDAT, TimeHelper.currentTime())
//                .set(table.UPDATEDAT, TimeHelper.currentTime())
//                .set(table.DATA, message.getData())
//                .execute();
    }

    @Override
    public Long findSendedAtByMessageTemplateId(Long messageTemplateId, Long userId) {
        return dslContext.select(table.SENDED_AT).from(table)
                .where(table.MESSAGE_TEMPLATE_ID.eq(messageTemplateId)
                        .and(table.USER_ID.eq(userId)))
                .orderBy(table.SENDED_AT.desc())
                .limit(1)
                .fetchOptionalInto(Long.class).orElse(0L);
    }

    @Override
    public void updateStatus(String uuid, MessageStatuses status) {
        dslContext.update(table).set(table.STATUS, status)
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.UUID.eq(uuid))
                .execute();
    }
}
