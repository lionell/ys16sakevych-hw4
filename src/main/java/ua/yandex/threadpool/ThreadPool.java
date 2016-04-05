package ua.yandex.threadpool;

import ua.yandex.prodcons.BlockingBuffer;
import ua.yandex.prodcons.threads.SynchronizedRingBuffer;
import ua.yandex.utils.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by lionell on 4/4/16.
 *
 * @author Ruslan Sakevych
 */
public class ThreadPool {
    private static final Random random = new Random(17);
    private static int nextPoolThreadId = 0;

    private final BlockingBuffer<Runnable> tasks;
    private final List<PoolThread> threads;
    private volatile boolean finished = false;

    public ThreadPool(int nThreads) {
        tasks = new SynchronizedRingBuffer<>(nThreads);
        threads = new LinkedList<>();

        for (int i = 0; i < nThreads; i++) {
            threads.add(new PoolThread(tasks));
        }

        threads.forEach(Thread::start);
    }

    public static void main(String[] args) {
        Logger.log("Creating threadPool....");
        ThreadPool threadPool = new ThreadPool(10);

        for (int i = 0; i < 20; i++) {
            threadPool.execute(new DummyRunnable());
        }

        Logger.log("Going to sleep...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.log("Waking up...");

        Logger.log("Shutting down threadPool....");
        threadPool.shutdown();
    }

    public void execute(Runnable task) {
        try {
            tasks.enqueue(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        finished = true;
        tasks.close();

        try {
            for (PoolThread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class PoolThread extends Thread {
        private final BlockingBuffer<Runnable> tasks;

        public PoolThread(BlockingBuffer<Runnable> tasks) {
            super("poolThread-" + nextPoolThreadId);
            this.tasks = tasks;
            nextPoolThreadId++;
        }

        @Override
        public void run() {
            while (!finished) {
                Runnable task;
                try {
                    task = tasks.dequeue();
                } catch (InterruptedException ignored) {
                    break;
                }

                if (task != null) {
                    task.run();
                }
            }
        }
    }

    private static class DummyRunnable implements Runnable {
        private static int nextID = 0;
        private final int id;

        private DummyRunnable() {
            id = nextID++;
        }

        @Override
        public void run() {
            Logger.log(this + " started.");
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.log(this + " finished.");
        }

        @Override
        public String toString() {
            return "DummyRunnable{" + id + "}";
        }
    }
}
