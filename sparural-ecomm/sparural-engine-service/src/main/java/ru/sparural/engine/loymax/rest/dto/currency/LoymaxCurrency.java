package ru.sparural.engine.loymax.rest.dto.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxCurrency {
    Long id;
    String name;
    String externalId;
    String uid;
    String description;
    Boolean isDeleted;
    LoymaxNameCases nameCases;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoymaxCurrency that = (LoymaxCurrency) o;
        return Objects.equals(name, that.name) && Objects.equals(uid, that.uid) && Objects.equals(description, that.description) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uid, description, isDeleted);
    }
}