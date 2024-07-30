package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Notification;
import ru.sparural.engine.repositories.NotificationsRepository;

//TODO: The logic of this repository better for service. Need move it to services
@Service
@RequiredArgsConstructor
public class NotificationsRepositoryImpl implements NotificationsRepository {

    private final SmsSettingsRepositoryImpl smsSettingsRepository;
    private final EmailSettingsRepositoryImpl emailSettingsRepository;
    private final WhatsAppSettingsRepositoryImpl whatsAppSettingsRepository;
    private final ViberSettingsRepositoryImpl viberSettingsRepository;
    private final PushSettingsRepositoryImpl pushSettingsRepository;

    @Override
    public Notification get() {
        Notification notification = new Notification();
        notification.setEmail(emailSettingsRepository.get());
        notification.setSms(smsSettingsRepository.get());
        notification.setWhatsapp(whatsAppSettingsRepository.get());
        notification.setViber(viberSettingsRepository.get());
        notification.setPush(pushSettingsRepository.get());
        return notification;
    }

    @Override
    public Notification update(Notification notification) {
        smsSettingsRepository.update(notification.getSms());
        emailSettingsRepository.update(notification.getEmail());
        whatsAppSettingsRepository.update(notification.getWhatsapp());
        viberSettingsRepository.update(notification.getViber());
        pushSettingsRepository.update(notification.getPush());
        return notification;
    }

}
