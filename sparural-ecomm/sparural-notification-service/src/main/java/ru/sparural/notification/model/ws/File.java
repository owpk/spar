package ru.sparural.notification.model.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File {
    String uuid;
    String name;
    String ext;
    Long size;
    String mime;
    String url;
}
