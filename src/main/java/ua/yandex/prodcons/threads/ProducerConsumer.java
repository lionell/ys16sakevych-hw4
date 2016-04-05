package ua.yandex.prodcons.threads;

import ua.yandex.prodcons.BlockingBuffer;

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
        BlockingBuffer<Integer> buffer = new BlockingRingBuffer<>(5);
        (new Producer(buffer)).start();
        (new Producer(buffer)).start();
        (new Producer(buffer)).start();
        (new Consumer(buffer)).start();
        (new Consumer(buffer)).start();
        (new Consumer(buffer)).start();
    }

    private static class Producer extends Thread {
        private static int nextID = 0;
        private final int id;
        private final BlockingBuffer<Integer> buffer;

        public Producer(BlockingBuffer<Integer> buffer) {
            this.buffer = buffer;
            id = nextID++;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    buffer.push(id * id);
                    sleep(random.nextInt(MAX_WAIT_MILLIS));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private static class Consumer extends Thread {
        private final BlockingBuffer<Integer> buffer;

        public Consumer(BlockingBuffer<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    buffer.pop();
                    sleep(random.nextInt(MAX_WAIT_MILLIS));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
