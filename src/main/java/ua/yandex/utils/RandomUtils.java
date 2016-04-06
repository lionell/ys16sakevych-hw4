package ua.yandex.utils;

import java.util.Random;

/**
 * Created by lionell on 4/6/16.
 *
 * @author Ruslan Sakevych
 */
public class RandomUtils {
    private static final Random random = new Random(17);
    private static final String alphabet =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static int[] randomIntArray(int length) {
        int[] a = new int[length];

        for (int i = 0; i < length; ++i) {
            a[i] = random.nextInt(1000);
        }

        return a;
    }

    public static String[] randomStringArray(int length, int minLength,
                                             int maxLength) {
        String[] words = new String[length];

        for (int i = 0; i < length; i++) {
            words[i] = randomWord(random.nextInt(maxLength - minLength + 1)
                    + minLength);
        }

        return words;
    }

    private static String randomWord(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(randomChar());
        }

        return builder.toString();
    }

    private static char randomChar() {
        return alphabet.charAt(random.nextInt(alphabet.length()));
    }
}
