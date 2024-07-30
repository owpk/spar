package ru.sparural.engine.loymax.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserAction;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Actions {
    List<LoymaxUserAction> actions;
}
