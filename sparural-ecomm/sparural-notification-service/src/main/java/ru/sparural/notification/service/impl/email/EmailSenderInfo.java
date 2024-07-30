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
public class EmailSenderInfo {
    private String name;
    private String address;
}
