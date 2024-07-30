package ru.sparural.notification.utils;

/**
 * @author Vorobyev Vyacheslav
 */
@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;

}
