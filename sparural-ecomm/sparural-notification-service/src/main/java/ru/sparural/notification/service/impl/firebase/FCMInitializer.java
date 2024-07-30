package ru.sparural.notification.service.impl.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.sparural.notification.service.impl.firebase.settings.ClientSettingsBean;
import ru.sparural.notification.service.impl.firebase.settings.ClientSettingsUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@DependsOn(value = "FirebaseClientSettingsBean")
@Slf4j
@RequiredArgsConstructor
public class FCMInitializer {
    private final ClientSettingsBean clientSettingsBean;

    @PostConstruct
    public void initialize() {
        try {
            var cred = GoogleCredentials.fromStream(
                    ClientSettingsUtils.getSettingsJson(clientSettingsBean));
            var options = FirebaseOptions.builder()
                    .setCredentials(cred)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}