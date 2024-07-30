package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Merchant;
import ru.sparural.engine.entity.NotificationsEntity;
import ru.sparural.engine.entity.NotificationsFullEntity;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.NotificationsListRepository;
import ru.sparural.tables.Merchants;
import ru.sparural.tables.Notifications;
import ru.sparural.tables.Screens;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationsListRepositoryImpl implements NotificationsListRepository {
    private final DSLContext dslContext;
    private final Notifications table = Notifications.NOTIFICATIONS;

    @Override
    public List<NotificationsFullEntity> fetch(long userId, int offset, int limit, Boolean isReaded, List<String> type) {
        var condition = table.USER_ID.eq(userId);
        if (isReaded != null)
            condition = condition.and(table.IS_READED.eq(isReaded));
        if (type != null && !type.isEmpty())
            condition = condition.and(table.MSG_TYPE.in(type));

        return dslContext
                .select()
                .from(table)
                .leftJoin(Merchants.MERCHANTS).on(Merchants.MERCHANTS.ID.eq(table.MERCHANT_ID))
                .leftJoin(Screens.SCREENS).on(Screens.SCREENS.ID.eq(table.SCREEN_ID))
                .where(condition)
                .orderBy(table.SENDED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetch(record -> {
                    var notify = new NotificationsFullEntity();
                    var merchant = record.into(Merchants.MERCHANTS.fields()).into(Merchant.class);
                    var screen = record.into(Screens.SCREENS.fields()).into(Screen.class);
                    notify.setMerchant(merchant);
                    notify.setScreen(screen);
                    notify.setId(record.get(table.ID));
                    notify.setBody(record.get(table.BODY));
                    notify.setTitle(record.get(table.TITLE));
                    notify.setIsReaded(record.get(table.IS_READED));
                    notify.setType(record.get(table.MSG_TYPE));
                    notify.setSendedAt(record.get(table.SENDED_AT));
                    notify.setUserId(record.get(table.USER_ID));
                    return notify;
                });
    }

    @Override
    public List<NotificationsEntity> fetch(long userId, int offset, int limit, Boolean isReaded) {
        return dslContext
                .selectFrom(Notifications.NOTIFICATIONS)
                .where(Notifications.NOTIFICATIONS.USER_ID.eq(userId)
                        .and(Notifications.NOTIFICATIONS.IS_READED.eq(isReaded)))
                .orderBy(Notifications.NOTIFICATIONS.SENDED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(NotificationsEntity.class);
    }

    @Override
    public List<NotificationsEntity> fetch(long userId, int offset, int limit, List<String> type) {

        return dslContext
                .selectFrom(Notifications.NOTIFICATIONS)
                .where(Notifications.NOTIFICATIONS.USER_ID.eq(userId)
                        .and(Notifications.NOTIFICATIONS.MSG_TYPE.in(type)))
                .orderBy(Notifications.NOTIFICATIONS.SENDED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(NotificationsEntity.class);
    }

    @Override
    public List<NotificationsEntity> fetch(long userId, int offset, int limit) {

        return dslContext
                .selectFrom(Notifications.NOTIFICATIONS)
                .where(Notifications.NOTIFICATIONS.USER_ID.eq(userId))
                .orderBy(Notifications.NOTIFICATIONS.SENDED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(NotificationsEntity.class);
    }

    @Override
    public Optional<NotificationsEntity> get(long id, long userId) {
        return dslContext.update(Notifications.NOTIFICATIONS)
                .set(Notifications.NOTIFICATIONS.IS_READED, true)
                .where(Notifications.NOTIFICATIONS.ID.eq(id))
                .returning()
                .fetchOptionalInto(NotificationsEntity.class);
    }

    @Override
    public Optional<NotificationsEntity> save(NotificationsEntity notificationsEntity) {
        return dslContext.insertInto(Notifications.NOTIFICATIONS)
                .set(Notifications.NOTIFICATIONS.USER_ID, notificationsEntity.getUserId())
                .set(Notifications.NOTIFICATIONS.BODY, notificationsEntity.getBody())
                .set(Notifications.NOTIFICATIONS.MSG_TYPE, notificationsEntity.getType())
                .set(Notifications.NOTIFICATIONS.TITLE, notificationsEntity.getTitle())
                .set(Notifications.NOTIFICATIONS.MERCHANT_ID, notificationsEntity.getMerchantId())
                .set(Notifications.NOTIFICATIONS.SENDED_AT, new Date().getTime())
                .set(Notifications.NOTIFICATIONS.IS_READED, false)
                .set(Notifications.NOTIFICATIONS.SCREEN_ID, notificationsEntity.getScreenId())
                .set(Notifications.NOTIFICATIONS.CREATED_AT, new Date().getTime())
                .set(Notifications.NOTIFICATIONS.UPDATED_AT, new Date().getTime())
                .returningResult()
                .fetchOptionalInto(NotificationsEntity.class);
    }

    @Override
    public int getUnreadedMessagesCount(Long userId) {
        return dslContext.selectFrom(Notifications.NOTIFICATIONS)
                .where(Notifications.NOTIFICATIONS.USER_ID.eq(userId)
                        .and(Notifications.NOTIFICATIONS.IS_READED.eq(false)))
                .fetch().size();
    }

}
