package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.NotificationsEntity;
import ru.sparural.engine.entity.NotificationsFullEntity;

import java.util.List;
import java.util.Optional;

public interface NotificationsListRepository {
    List<NotificationsFullEntity> fetch(long userId, int offset, int limit, Boolean isReaded, List<String> types);

    List<NotificationsEntity> fetch(long userId, int offset, int limit, Boolean isReaded);

    List<NotificationsEntity> fetch(long userId, int offset, int limit, List<String> types);

    List<NotificationsEntity> fetch(long userId, int offset, int limit);

    Optional<NotificationsEntity> get(long id, long userId);

    Optional<NotificationsEntity> save(NotificationsEntity notificationsEntity);

    int getUnreadedMessagesCount(Long userId);
}