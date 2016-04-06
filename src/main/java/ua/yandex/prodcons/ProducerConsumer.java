package ua.yandex.prodcons;

import ua.yandex.misc.NamedThread;
import ua.yandex.prodcons.utilconcurrent.ConcurrentRingBuffer;

import java.util.Random;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class ProducerConsumer {
    private static final Random random = new Random(17);
    private static final int MAX_WAIT_MILLIS = 1000;

    public static void main(String[] args) {
//        BlockingBuffer<Integer> buffer = new SynchronizedRingBuffer<>(5);
        BlockingBuffer<Integer> buffer = new ConcurrentRingBuffer<>(5);
        (new Producer(buffer)).start();
        (new Producer(buffer)).start();
        (new Producer(buffer)).start();
        (new Consumer(buffer)).start();
        (new Consumer(buffer)).start();
        (new Consumer(buffer)).start();
    }

    private static class Producer extends NamedThread {
        private final BlockingBuffer<Integer> buffer;

        private Producer(BlockingBuffer<Integer> buffer) {
            super("producer");
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    buffer.enqueue(id * id);
                    sleep(random.nextInt(MAX_WAIT_MILLIS));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private static class Consumer extends NamedThread {
        private final BlockingBuffer<Integer> buffer;

        private Consumer(BlockingBuffer<Integer> buffer) {
            super("consumer");
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    buffer.dequeue();
                    sleep(random.nextInt(MAX_WAIT_MILLIS));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
