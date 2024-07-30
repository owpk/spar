package ru.sparural.triggerapi;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserEvent extends Event {
    Long getUserId();
}