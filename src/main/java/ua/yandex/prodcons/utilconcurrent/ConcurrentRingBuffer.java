package ua.yandex.prodcons.utilconcurrent;

import ua.yandex.prodcons.BlockingRingBuffer;
import ua.yandex.utils.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lionell on 4/5/16.
 *
 * @author Ruslan Sakevych
 */
public class ConcurrentRingBuffer<T> extends BlockingRingBuffer<T> {
    private final Lock lock = new ReentrantLock();
    private final Condition isFull = lock.newCondition();
    private final Condition isEmpty = lock.newCondition();

    public ConcurrentRingBuffer(int size) {
        super(size);
    }

    @Override
    public void enqueue(T value) throws InterruptedException {
        lock.lock();
        try {
            while (!isClosed() && isFull()) {
                Logger.log("Buffer is full!");
                isFull.await();
            }

            if (!isClosed()) {
                a.set(head, value);
                head = next(head);
                amount++;
                Logger.log("Added " + value);

                isEmpty.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T dequeue() throws InterruptedException {
        lock.lock();

        T value = null;

        try {
            while (!isClosed() && isEmpty()) {
                Logger.log("Buffer is empty!");
                isEmpty.await();
            }

            if (!isClosed()) {
                value = a.get(tail);
                tail = next(tail);
                amount--;
                Logger.log("Prepared " + value);

                isFull.signal();
            }
        } finally {
            lock.unlock();
        }

        return value;
    }

    @Override
    public void close() {
        super.close();

        isFull.signalAll();
        isEmpty.signalAll();
    }
}
