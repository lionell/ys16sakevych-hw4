package ua.yandex.misc;

import ua.yandex.utils.Logger;

import java.util.Random;

/**
 * Created by lionell on 4/6/16.
 *
 * @author Ruslan Sakevych
 */
public class DummyRunnable implements Runnable {
    private static final Random random = new Random(17);
    private static int nextID = 0;
    private final int id;

    public DummyRunnable() {
        id = nextID++;
    }

    @Override
    public void run() {
        Logger.log(this + " started.");
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.log(this + " finished.");
    }

    @Override
    public String toString() {
        return "DummyRunnable{" + id + "}";
    }
}
