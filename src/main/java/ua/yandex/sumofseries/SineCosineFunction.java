package ua.yandex.sumofseries;

import java.util.function.DoubleUnaryOperator;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class SineCosineFunction implements DoubleUnaryOperator {
    @Override
    public double applyAsDouble(double operand) {
        return Math.sin(operand) * Math.cos(operand);
    }
}
