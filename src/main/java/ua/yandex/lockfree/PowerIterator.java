package ua.yandex.lockfree;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lionell on 4/5/16.
 *
 * @author Ruslan Sakevych
 */
public class PowerIterator implements Iterator<BigInteger> {
    private final AtomicReference<BigInteger> value =
            new AtomicReference<>(BigInteger.ONE);
    public static final BigInteger MULTIPLIER = BigInteger.valueOf(2);

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
            newValue = oldValue.multiply(MULTIPLIER);
        } while (!value.compareAndSet(oldValue, newValue));

        return oldValue;
    }
}
