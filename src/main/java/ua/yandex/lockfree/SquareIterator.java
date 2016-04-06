package ua.yandex.lockfree;

import ua.yandex.misc.NamedThread;
import ua.yandex.utils.Logger;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lionell on 4/5/16.
 *
 * @author Ruslan Sakevych
 */
public class SquareIterator implements Iterator<BigInteger> {
    private static final Random random = new Random(17);
    private static final BigInteger BIG_TWO = BigInteger.valueOf(2);
    private static final LinkedBlockingQueue<BigInteger> generatedValues =
            new LinkedBlockingQueue<>();
    private final AtomicReference<BigInteger> value =
            new AtomicReference<>(BigInteger.ONE);

    public static void main(String[] args) {
        SquareIterator iterator = new SquareIterator();

        Thread[] threadPool = new Thread[10];
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = new DummyConsumer(iterator);
        }

        for (Thread thread : threadPool) {
            thread.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        for (Thread thread : threadPool) {
            thread.interrupt();
        }

        for (Thread thread : threadPool) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }

        assert checkConsistency() : "Some values are incorrect!";
    }

    private static boolean checkConsistency() {
        List<BigInteger> values = new LinkedList<>();
        generatedValues.drainTo(values);
        Collections.sort(values);

        ListIterator<BigInteger> listIterator = values.listIterator();
        listIterator.next();
        listIterator.previous();

        BigInteger old = listIterator.next();
        if (!old.equals(BigInteger.ONE)) {
            return false;
        }

        while (listIterator.hasNext()) {
            old = old.multiply(BIG_TWO);
            if (!old.equals(listIterator.next())) {
                Logger.log(old.toString());
                return false;
            }
        }

        return true;
    }

    private static class DummyConsumer extends NamedThread {
        private final SquareIterator iterator;

        private DummyConsumer(SquareIterator iterator) {
            super("consumer");
            this.iterator = iterator;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                BigInteger value = iterator.next();
                generatedValues.add(value);
                Logger.log(value.toString());

                try {
                    Thread.sleep(random.nextInt(100));
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public BigInteger next() {
        BigInteger oldValue;
        BigInteger newValue;

        do {
            oldValue = value.get();
            newValue = oldValue.multiply(BIG_TWO);
        } while (!value.compareAndSet(oldValue, newValue));

        return oldValue;
    }
}
