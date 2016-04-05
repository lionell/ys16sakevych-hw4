package ua.yandex.prodcons.threads;

import ua.yandex.prodcons.BlockingBuffer;
import ua.yandex.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lionell on 4/4/16.
 *
 * @author Ruslan Sakevych
 */
public class BlockingRingBuffer<T> implements BlockingBuffer<T> {
    private final int size;
    private final List<T> a;
    private volatile int head = 0;
    private volatile int tail = 0;
    private volatile int amount = 0;
    private volatile boolean closed = false;

    public BlockingRingBuffer(int size) {
        this.size = size;

        a = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            a.add(null);
        }
    }

    private int next(int i) {
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

    public synchronized void push(T value) throws InterruptedException {
        while (!isClosed() && isFull()) {
            Logger.log("Buffer is full!");
            wait();
        }

        if (!isClosed()) {
            a.set(head, value);
            head = next(head);
            amount++;
            Logger.log("Added " + value);

            notifyAll();
        }
    }

    public synchronized T pop() throws InterruptedException {
        while (!isClosed() && isEmpty()) {
            Logger.log("Buffer is empty!");
            wait();
        }

        T value = null;

        if (!isClosed()) {
            value = a.get(tail);
            tail = next(tail);
            amount--;
            Logger.log("Prepared " + value);

            notifyAll();
        }
        return value;
    }

    public synchronized void close() {
        closed = true;
        notifyAll();
    }
}