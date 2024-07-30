package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.support.SupportChatCreateMessageDto;
import ru.sparural.engine.entity.SupportChatMessageEntity;
import ru.sparural.engine.entity.SupportChatMessageType;
import ru.sparural.engine.entity.SupportChatsEntity;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.repositories.SupportChatsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.SupportChatMessages;
import ru.sparural.tables.SupportChats;
import ru.sparural.tables.Users;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportChatsRepositoryImpl implements SupportChatsRepository {

    private final DSLContext dslContext;
    private final SupportChats table = SupportChats.SUPPORT_CHATS;
    private final SupportChatMessages msgTable = SupportChatMessages.SUPPORT_CHAT_MESSAGES;
    private final Users userTable = Users.USERS.as("u1");
    private final Users senderTable = Users.USERS.as("u2");

    @Override
    public Optional<SupportChatsEntity> fetchById(Long id) {
        return basicSelectSupportChatMessage()
                .where(SupportChatMessages.SUPPORT_CHAT_MESSAGES.ID.eq(id))
                .fetchOptional(this::mapRecordToEntity);
    }

    private SelectOnConditionStep<?> basicSelectSupportChatMessage() {
        return dslContext.select().from(table)
                .leftJoin(msgTable)
                .on(table.ID.eq(msgTable.CHAT_ID))
                .leftJoin(userTable)
                .on(table.USER_ID.eq(userTable.ID))
                .leftJoin(senderTable)
                .on(msgTable.SENDER_ID.eq(senderTable.ID));
    }

    private SelectOnConditionStep<?> basicSelectSupportChatMessage(Integer offset, Integer limit) {
        var tempTable = dslContext.select().from(table).offset(offset).limit(limit).asTable("limited_sup_chats");
        return dslContext.select().from(tempTable)
                .leftJoin(msgTable)
                .on(tempTable.field("id", Long.class).eq(msgTable.CHAT_ID))
                .leftJoin(userTable)
                .on(tempTable.field("user_id", Long.class).eq(userTable.ID))
                .leftJoin(senderTable)
                .on(msgTable.SENDER_ID.eq(senderTable.ID));
    }

    @Override
    public List<SupportChatsEntity> list(Integer offset, Integer limit) {
        return basicSelectSupportChatMessage(offset, limit)
                .fetch(this::mapRecordToEntity);
    }

    @Override
    public List<SupportChatsEntity> list(Long chatId, Long timeStamp, Integer limit) {
        var condition = DSL.noCondition();
        if (timeStamp != null)
            condition.and(msgTable.CREATED_AT.lessOrEqual(timeStamp));
        return basicSelectSupportChatMessage()
                .where(condition.and(msgTable.CHAT_ID.eq(chatId)))
                .orderBy(msgTable.CREATED_AT.desc())
                .limit(limit)
                .fetch(this::mapRecordToEntity);
    }

    @Override
    public Long countUnreadMessages(Long senderId, Long chatId) {
        var field = DSL.field("count(*)", SQLDataType.BIGINT);
        return dslContext.select(field)
                .from(msgTable)
                .where(msgTable.CHAT_ID.eq(chatId).and(msgTable.SENDER_ID.eq(senderId))
                        .and(msgTable.IS_READ.eq(false).or(msgTable.IS_READ.isNull())))
                .fetchOneInto(Long.class);
    }

    @Override
    public Optional<SupportChatMessageEntity> updateMessage(Long chatId, Long messageId, SupportChatCreateMessageDto data) {
        dslContext.update(msgTable)
                .set(msgTable.DRAFT, data.getDraft())
                .set(msgTable.TEXT, data.getText())
                .set(msgTable.MESSAGE_TYPE, data.getMessageType())
                .set(msgTable.UPDATED_AT, TimeHelper.currentTime())
                .where(msgTable.CHAT_ID.eq(chatId).and(msgTable.ID.eq(messageId)))
                .execute();
        return dslContext.select().from(msgTable)
                .leftJoin(senderTable).on(msgTable.SENDER_ID.eq(senderTable.ID))
                .where(msgTable.ID.eq(messageId))
                .fetchOptional(this::mapRecordToMsgEntity);
    }

    @Override
    public Optional<SupportChatMessageEntity> createMessage(Long senderId, Long chatId, SupportChatCreateMessageDto data) {
        var id = dslContext.insertInto(msgTable)
                .set(msgTable.CHAT_ID, chatId)
                .set(msgTable.DRAFT, data.getDraft())
                .set(msgTable.TEXT, data.getText())
                .set(msgTable.SENDER_ID, senderId)
                .set(msgTable.MESSAGE_TYPE, data.getMessageType())
                .set(msgTable.CREATED_AT, TimeHelper.currentTime())
                .returningResult(msgTable.ID).fetchOneInto(Long.class);
        return dslContext.select().from(msgTable)
                .leftJoin(senderTable).on(msgTable.SENDER_ID.eq(senderTable.ID))
                .where(msgTable.ID.eq(id))
                .fetchOptional(this::mapRecordToMsgEntity);
    }

    @Override
    public List<SupportChatMessageEntity> setMessagesRead(Long chatId, List<Long> messagesIds) {
        List<UpdateConditionStep<?>> records = messagesIds
                .stream()
                .map(id -> dslContext.update(msgTable)
                        .set(msgTable.IS_READ, true)
                        .set(msgTable.IS_RECEIVED, true)
                        .set(msgTable.UPDATED_AT, TimeHelper.currentTime())
                        .where(msgTable.ID.eq(id)))
                .collect(Collectors.toList());
        dslContext.batch(records).execute();
        return basicSelectSupportChatMessage()
                .where(msgTable.ID.in(messagesIds))
                .fetch(this::mapRecordToMsgEntity);
    }

    private SupportChatMessageEntity mapRecordToMsgEntity(Record record) {
        var msgEntity = new SupportChatMessageEntity();
        var chatMsgRec = record.into(msgTable.fields())
                .into(ru.sparural.tables.pojos.SupportChatMessages.class);
        var senderRec = record.into(senderTable.fields()).into(User.class);
        msgEntity.setSender(senderRec);
        msgEntity.setId(chatMsgRec.getId());
        msgEntity.setText(chatMsgRec.getText());
        msgEntity.setDraft(chatMsgRec.getDraft());
        msgEntity.setMessageType(SupportChatMessageType.of(chatMsgRec.getMessageType()));
        msgEntity.setIsReceived(chatMsgRec.getIsReceived() != null ? chatMsgRec.getIsReceived() : false);
        msgEntity.setIsRead(chatMsgRec.getIsRead() != null ? chatMsgRec.getIsRead() : false);
        msgEntity.setCreatedAt(chatMsgRec.getCreatedAt());
        msgEntity.setUpdatedAt(chatMsgRec.getUpdatedAt());
        return msgEntity;
    }

    private SupportChatsEntity mapRecordToEntity(Record record) {
        var entity = new SupportChatsEntity();
        var msgEntity = mapRecordToMsgEntity(record);
        entity.setMessage(msgEntity);
        var chatRec = record.into(table.fields())
                .into(ru.sparural.tables.pojos.SupportChats.class);
        var userRec = record.into(userTable.fields()).into(User.class);
        entity.setUser(userRec);
        entity.setId(chatRec.getId());
        return entity;
    }

}
