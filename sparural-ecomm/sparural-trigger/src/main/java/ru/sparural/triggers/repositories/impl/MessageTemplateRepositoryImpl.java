package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.tables.MessagesTemplateUser;
import ru.sparural.tables.MessagesTemplateUsersGroup;
import ru.sparural.tables.MessagesTemplates;
import ru.sparural.tables.MessagesTypes;
import ru.sparural.triggers.entities.MessageTemplate;
import ru.sparural.triggers.repositories.MessageTemplateUserRepository;
import ru.sparural.triggers.repositories.MessageTemplateUsersGroupRepository;

import java.util.*;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class MessageTemplateRepositoryImpl implements ru.sparural.triggers.repositories.MessageTemplateRepository {
    private static final String LIM_TABLE_NAME = "msg_template_limited";

    private final DSLContext dslContext;
    private final MessagesTemplates table = MessagesTemplates.MESSAGES_TEMPLATES;
    private final MessagesTemplates limTable = table.as(LIM_TABLE_NAME);
    private final MessagesTemplateUsersGroup userGroupTable = MessagesTemplateUsersGroup.MESSAGES_TEMPLATE_USERS_GROUP;
    private final MessagesTemplateUser userTable = MessagesTemplateUser.MESSAGES_TEMPLATE_USER;
    private final MessageTemplateUserRepository messageTemplateUserRepository;
    private final MessageTemplateUsersGroupRepository messageTemplateUsersGroupRepository;

    @Override
    public List<MessageTemplate> list(Integer offset, Integer limit, String messageType) {
        var typedTableName = "msg_template_typed_table";
        var msgTypeTable = MessagesTypes.MESSAGES_TYPES;
        var resultMap = new LinkedHashMap<Long, MessageTemplate>();

        var typedTable = DSL.selectFrom(table)
                .where(table.MESSAGE_TYPE_ID.eq(DSL.select(msgTypeTable.ID)
                        .from(msgTypeTable)
                        .where(msgTypeTable.NAME.eq(messageType))))
                .offset(offset)
                .limit(limit)
                .asTable(typedTableName);

        var typedTableCasted = table.as(typedTableName);

        var fetchedResult = dslContext.select().from(typedTable)
                .leftJoin(userTable).on(typedTableCasted.ID.eq(userTable.MESSAGE_TEMPLATE_ID))
                .leftJoin(userGroupTable).on(typedTableCasted.ID.eq(userGroupTable.MESSAGE_TEMPLATE))
                .orderBy(typedTableCasted.CREATED_AT.desc())
                .fetch();

        fetchedResult.forEach(record -> computeRecordToMessageTemplateEntity(record, resultMap));
        return new ArrayList<>(resultMap.values());
    }

    private void computeRecordToMessageTemplateEntity(Record record, Map<Long, MessageTemplate> resultMap) {
        var id = record.get(table.as(LIM_TABLE_NAME).ID);
        var msgTemplate = resultMap.computeIfAbsent(id, val -> record.into(table.fields()).into(MessageTemplate.class));

        if (record.get(userTable.ID) != null)
            msgTemplate.getUsers().add(record.into(userTable.USER_ID).into(Long.class));

        if (record.get(userGroupTable.ID) != null)
            msgTemplate.getUsersGroup().add(record.into(userGroupTable.USERS_GROUP_ID).into(Long.class));
    }

    // Returns LIM_TABLE_NAME as base table !!!
    private SelectJoinStep<?> basicSelect(Integer offset, Integer limit) {
        var limitedTable = dslContext.selectFrom(table).limit(limit).offset(offset).asTable(LIM_TABLE_NAME);
        return dslContext.select().from(limitedTable)
                .leftJoin(userTable).on(limTable.ID.eq(userTable.MESSAGE_TEMPLATE_ID))
                .leftJoin(userGroupTable).on(limTable.ID.eq(userGroupTable.MESSAGE_TEMPLATE));
    }

    @Override
    public Optional<MessageTemplate> get(Long id) {
        var result = new HashMap<Long, MessageTemplate>();
        basicSelect(null, null)
                .where(limTable.ID.eq(id))
                .fetch()
                .forEach(record -> computeRecordToMessageTemplateEntity(record, result));
        return Optional.ofNullable(result.get(id));
    }

    @Override
    public Optional<MessageTemplate> create(MessageTemplate entity) {
        var opt = dslContext.insertInto(table)
                .set(table.NAME, entity.getName())
                .set(table.MESSAGE_TYPE_ID, entity.getMessageTypeId())
                .set(table.MESSAGE, entity.getMessage())
                .set(table.MESSAGE_HTML, entity.getMessageHtml())
                .set(table.SUBJECT, entity.getSubject())
                .set(table.SCREEN_ID, entity.getScreenId())
                .set(table.NOTIFICATION_TYPE_ID, entity.getNotificationTypeId())
                .set(table.SEND_TO_EVERYONE, entity.getSendToEveryone())
                .set(table.IS_SYSTEM, entity.getIsSystem())
                .set(table.REQUIRED, entity.getRequired())
                .set(table.LIFETIME, entity.getLifetime())
                .set(table.CURRENCY_ID, entity.getCurrencyId())
                .set(table.CURRENCY_DAYS_BEFORE_BURNING, entity.getCurrencyDaysBeforeBurning())
                .set(table.DAYS_WITHOUT_PURCHASING, entity.getDaysWithoutPurchasing())
                .set(table.CREATED_AT, Calendar.getInstance().getTimeInMillis())
                .returning().fetchOptionalInto(MessageTemplate.class);

        if (opt.isPresent()) {
            var savedEntity = opt.get();

            if (entity.getUsers() != null && !entity.getUsers().isEmpty()) {
                messageTemplateUserRepository.batchBind(entity.getUsers(), savedEntity.getId());
                savedEntity.setUsers(entity.getUsers());
            }

            if (entity.getUsersGroup() != null && !entity.getUsersGroup().isEmpty()) {
                messageTemplateUsersGroupRepository.batchBind(entity.getUsersGroup(), savedEntity.getId());
                savedEntity.setUsersGroup(entity.getUsersGroup());
            }
        }

        return opt;
    }

    @Override
    @Transactional
    public Optional<MessageTemplate> update(Long id, MessageTemplate entity) {
        var opt = dslContext.update(table)
                .set(table.NAME, coalesce(val(entity.getName()), table.NAME))
                .set(table.MESSAGE_TYPE_ID, coalesce(val(entity.getMessageTypeId()), table.MESSAGE_TYPE_ID))
                .set(table.MESSAGE, coalesce(val(entity.getMessage()), table.MESSAGE))
                .set(table.MESSAGE_HTML, coalesce(val(entity.getMessageHtml()), table.MESSAGE_HTML))
                .set(table.SUBJECT, coalesce(val(entity.getSubject()), table.SUBJECT))
                .set(table.SCREEN_ID, coalesce(val(entity.getScreenId()), table.SCREEN_ID))
                .set(table.NOTIFICATION_TYPE_ID, coalesce(val(entity.getNotificationTypeId()), table.NOTIFICATION_TYPE_ID))
                .set(table.SEND_TO_EVERYONE, coalesce(val(entity.getSendToEveryone()), table.SEND_TO_EVERYONE))
                .set(table.IS_SYSTEM, coalesce(val(entity.getIsSystem()), table.IS_SYSTEM))
                .set(table.REQUIRED, coalesce(val(entity.getRequired()), table.REQUIRED))
                .set(table.LIFETIME, coalesce(val(entity.getLifetime()), table.LIFETIME))
                .set(table.CURRENCY_ID, coalesce(val(entity.getCurrencyId()), table.CURRENCY_ID))
                .set(table.CURRENCY_DAYS_BEFORE_BURNING, coalesce(val(entity.getCurrencyDaysBeforeBurning()), table.CURRENCY_DAYS_BEFORE_BURNING))
                .set(table.UPDATED_AT, Calendar.getInstance().getTimeInMillis())
                .where(table.ID.eq(id))
                .returning().fetchOptionalInto(MessageTemplate.class);

        if (opt.isPresent()) {

            if (entity.getUsers() != null && !entity.getUsers().isEmpty()) {
                messageTemplateUserRepository.delete(opt.get().getId());
                messageTemplateUserRepository.batchBind(entity.getUsers(), opt.get().getId());
                opt.get().setUsers(entity.getUsers());
            }

            if (entity.getUsersGroup() != null && !entity.getUsersGroup().isEmpty()) {
                messageTemplateUsersGroupRepository.delete(opt.get().getId());
                messageTemplateUsersGroupRepository.batchBind(entity.getUsersGroup(), opt.get().getId());
                opt.get().setUsersGroup(entity.getUsersGroup());
            }
        }
        return opt;
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext
                .delete(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public Optional<MessageTemplate> getByUserId(long userId) {
        var result = new HashMap<Long, MessageTemplate>();
        basicSelect(null, null)
                .where(userTable.USER_ID.eq(userId))
                .fetch()
                .forEach(record -> computeRecordToMessageTemplateEntity(record, result));
        return result.values().stream().findAny();
    }
}
