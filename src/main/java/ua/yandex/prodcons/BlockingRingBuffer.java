package ua.yandex.prodcons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lionell on 4/5/16.
 *
 * @author Ruslan Sakevych
 */
public abstract class BlockingRingBuffer<T> implements BlockingBuffer<T> {
    private final int size;
    protected final List<T> a;
    protected volatile int head = 0;
    protected volatile int tail = 0;
    protected volatile int amount = 0;
    protected volatile boolean closed = false;

    public BlockingRingBuffer(int size) {
        this.size = size;

        a = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            a.add(null);
        }
    }

    protected int next(int i) {
        return (i + 1) % size;
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    public boolean isFull() {
        return amount == size;
    }

    public boolean isClosed() {
        return closed;
    }

    public abstract void enqueue(T value) throws InterruptedException;

    public abstract T dequeue() throws InterruptedException;

    public void close() {
        closed = true;
    }
}
