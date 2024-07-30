package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Notification;

public interface NotificationsRepository {

    Notification get();

    Notification update(Notification notification);
}
