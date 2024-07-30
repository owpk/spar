package ru.sparural.notification.service.impl.firebase.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component(value = "FirebaseClientSettingsBean")
public class ClientSettingsBean {
    @Value("${firebase.type}")
    String type;
    @Value("${firebase.project_id}")
    String project_id;
    @Value("${firebase.private_key_id}")
    String private_key_id;
    @Value("${firebase.private_key}")
    String private_key;
    @Value("${firebase.client_email}")
    String client_email;
    @Value("${firebase.client_id}")
    String client_id;
    @Value("${firebase.auth_uri}")
    String auth_uri;
    @Value("${firebase.token_uri}")
    String token_uri;
    @Value("${firebase.auth_provider_x509_cert_url}")
    String auth_provider_x509_cert_url;
    @Value("${firebase.client_x509_cert_url}")
    String client_x509_cert_url;
}