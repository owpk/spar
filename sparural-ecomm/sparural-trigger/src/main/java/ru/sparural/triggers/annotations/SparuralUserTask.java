package ru.sparural.triggers.annotations;

import org.springframework.stereotype.Component;
import ru.sparural.triggers.model.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vorobyev Vyacheslav
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface SparuralUserTask {
    EventType value();
}
