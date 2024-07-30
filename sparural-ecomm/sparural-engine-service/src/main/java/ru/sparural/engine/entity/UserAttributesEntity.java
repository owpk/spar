package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@Data
@ToString
public class UserAttributesEntity {
    private Long id;
    private String attributeName;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAttributesEntity that = (UserAttributesEntity) o;
        return Objects.equals(attributeName, that.attributeName) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeName, name);
    }
}
