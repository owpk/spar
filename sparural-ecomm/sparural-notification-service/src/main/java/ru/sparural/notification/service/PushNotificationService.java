package ru.sparural.notification.service;

import ru.sparural.notification.model.push.PushNotification;

import java.util.Map;

/**
 * @author Vorobyev Vyacheslav
 */
public interface PushNotificationService {
    void send(PushNotification pushNotification, Map<String, String> params) throws Exception;
}