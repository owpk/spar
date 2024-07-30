package ru.sparural.engine.entity.file;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Data
public class File {
    String uuid;
    String name;
    String ext;
    Long size;
    String mime;
    String url;
}
