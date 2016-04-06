package ua.yandex.misc;

/**
 * Created by lionell on 4/6/16.
 *
 * @author Ruslan Sakevych
 */
public abstract class NamedThread extends Thread {
    private static int nextId = 0;
    protected final int id;
    protected final String threadName;

    protected NamedThread(String threadName) {
        super(threadName + "-" + nextId);
        this.threadName = threadName;
        id = nextId++;
    }
}
