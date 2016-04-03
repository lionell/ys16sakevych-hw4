package ua.yandex.prodcons.threads;

import java.util.Random;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class ProducerConsumer {
    private static final Random random = new Random(17);

    public static void main(String[] args) {
        Buffer buffer = new Buffer(100);
        Producer p1 = new Producer(buffer);
        Producer p2 = new Producer(buffer);
        Consumer c1 = new Consumer(buffer);
        Consumer c2 = new Consumer(buffer);
        p1.start();
        p2.start();
        c1.start();
        c2.start();
        try {
            Thread.sleep(30);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p1.interrupt();
        p2.interrupt();
        c1.interrupt();
        c2.interrupt();
        for (int i : buffer.a) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println(buffer.tail);
        System.out.println(buffer.head);
        System.out.println(buffer.amount);
    }

    private static class Producer extends Thread {
        private static int nextID = 0;
        private final int id;
        private final Buffer buffer;

        public Producer(Buffer buffer) {
            this.buffer = buffer;
            id = nextID++;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    buffer.push(id * id);
                    sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private static class Consumer extends Thread {
        private final Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    buffer.pop();
                    sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private static class Buffer {
        private final int size;
        private final int[] a;
        private volatile int head = 0;
        private volatile int tail = 0;
        private volatile int amount = 0;

        public Buffer(int size) {
            this.size = size;
            a = new int[size];
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

        public synchronized void push(int x) throws InterruptedException {
            while (isFull()) {
                System.out.println("Buffer is full!");
                wait();
            }

            a[head] = x;
            head = next(head);
            amount++;
            System.out.println("Added " + x);

            notifyAll();
        }

        public synchronized int pop() throws InterruptedException {
            while (isEmpty()) {
                System.out.println("Buffer is empty!");
                wait();
            }

            int value = a[tail];
            tail = next(tail);
            amount--;
            System.out.println("Prepared " + value);

            notifyAll();
            return value;
        }
    }
}
