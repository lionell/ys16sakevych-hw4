package ua.yandex.prodcons;

/**
 * Created by lionell on 4/4/16.
 *
 * @author Ruslan Sakevych
 */
public interface BlockingBuffer<T> {
    void push(T value) throws InterruptedException;

    T pop() throws InterruptedException;

    void close();
}
