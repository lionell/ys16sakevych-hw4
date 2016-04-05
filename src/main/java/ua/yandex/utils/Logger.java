package ua.yandex.utils;

/**
 * Created by lionell on 4/5/16.
 *
 * @author Ruslan Sakevych
 */
public class Logger {
    public static void log(String message) {
        String currentThread = Thread.currentThread().getName();
        System.out.println("[" + currentThread + "]: " + message);

    }
}
