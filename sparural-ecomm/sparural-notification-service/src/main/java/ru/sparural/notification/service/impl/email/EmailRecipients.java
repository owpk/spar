package ru.sparural.notification.service.impl.email;

import lombok.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRecipients {
    private String address;
    private String name;
}
