package ru.sparural.notification.service;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserPushTokenService {
    void deleteByToken(String token);
}
