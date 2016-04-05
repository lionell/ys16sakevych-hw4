package ua.yandex.prodcons;

/**
 * Created by lionell on 4/4/16.
 *
 * @author Ruslan Sakevych
 */
public interface BlockingBuffer<T> {
    void enqueue(T value) throws InterruptedException;

    T dequeue() throws InterruptedException;

    void close();
}
