package ua.yandex.prodcons.threads;

import ua.yandex.prodcons.BlockingRingBuffer;
import ua.yandex.utils.Logger;

/**
 * Created by lionell on 4/4/16.
 *
 * @author Ruslan Sakevych
 */
public class SynchronizedRingBuffer<T> extends BlockingRingBuffer<T> {
    public SynchronizedRingBuffer(int size) {
        super(size);
    }

    @Override
    public synchronized void enqueue(T value) throws InterruptedException {
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

    @Override
    public synchronized T dequeue() throws InterruptedException {
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

    @Override
    public synchronized void close() {
        super.close();

        notifyAll();
    }
}