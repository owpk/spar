package ru.sparural.engine.loymax.rest.dto.merchant;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Merchant {
    Long id;
    String title;
    String address;
    String longitude;
    String latitude;
    String status;
    String closeUntilUntil;
    Integer distance;
    List<Attribute> attributes;
}