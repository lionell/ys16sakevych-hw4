package ua.yandex.utils;

/**
 * Created by lionell on 4/5/16.
 *
 * @author Ruslan Sakevych
 */
public class Logger {
    public static void log(String message) {
        String currentThread = Thread.currentThread().getName();
        System.out.printf("[%s]: %s\n", currentThread, message);
    }

    public static void logIntArray(int[] a) {
        for (int i : a) {
            System.out.println(i + " ");
        }
        System.out.println();
    }

    public static void logFormat(String format, Object... args) {
        String currentThread = Thread.currentThread().getName();
        Object[] fullArgs = new Object[args.length + 1];
        fullArgs[0] = currentThread;
        System.arraycopy(args, 0, fullArgs, 1, args.length);
        String fullFormat = "[%s]: " + format + "\n";
        System.out.printf(fullFormat, fullArgs);
    }
}
