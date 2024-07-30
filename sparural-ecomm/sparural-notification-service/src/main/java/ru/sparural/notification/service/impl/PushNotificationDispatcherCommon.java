package ru.sparural.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.sparural.notification.api.constants.DeviceType;
import ru.sparural.notification.service.impl.firebase.FirebaseNotificationService;
import ru.sparural.notification.service.impl.huawei.HuaweiNotificationService;
import ru.sparural.notification.service.PushNotificationService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class PushNotificationDispatcherCommon {

    private final ApplicationContext applicationContext;

    private Map<DeviceType, PushNotificationService> deviceTypeNotificationServiceMap;

    @PostConstruct
    public void init() {
        deviceTypeNotificationServiceMap = new HashMap<>();
        deviceTypeNotificationServiceMap.put(
                DeviceType.ANDROID, applicationContext.getBean(FirebaseNotificationService.class));
        deviceTypeNotificationServiceMap.put(
                DeviceType.HUAWEI, applicationContext.getBean(HuaweiNotificationService.class));
        deviceTypeNotificationServiceMap.put(
                DeviceType.IOS, applicationContext.getBean(FirebaseNotificationService.class));
    }

    public PushNotificationService getServiceByDeviceType(DeviceType deviceType) {
        return deviceTypeNotificationServiceMap.get(deviceType);
    }
}