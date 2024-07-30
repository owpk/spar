package ru.sparural.engine.loymax.rest.dto.merchant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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