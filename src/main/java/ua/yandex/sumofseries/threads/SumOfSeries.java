package ua.yandex.sumofseries.threads;

import ua.yandex.sumofseries.SineCosineFunction;

import java.util.function.DoubleUnaryOperator;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class SumOfSeries {
    public static void main(String[] args) {
        System.out.println(SumOfSeries.eval(20, 2));
    }

    public static double eval(double N, int M) {
        Calculator calculatorPool[] = new Calculator[M - 1];
        Thread threadPool[] = new Thread[M - 1];
        final double step = 2 * N / M;
        for (int i = 0; i < M - 1; i++) {
            calculatorPool[i] = new Calculator(
                    new SineCosineFunction(),
                    -N + (i + 1) * step, -N + (i + 2) * step);
            threadPool[i] = new Thread(calculatorPool[i]);
        }

        for (Thread thread : threadPool) {
            thread.start();
        }

        Calculator ownCalculator = new Calculator(new SineCosineFunction(),
                -N, -N + step);
        ownCalculator.run();
        double result = ownCalculator.getResult();

        try {
            for (Thread thread : threadPool) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Calculator calculator : calculatorPool) {
            result += calculator.getResult();
        }

        return result;
    }

    private static class Calculator implements Runnable {
        private static final double STEP = 1e-4;
        private final DoubleUnaryOperator func;
        private final double from;
        private final double to;
        private double result;

        private Calculator(DoubleUnaryOperator func, double from, double to) {
            this.func = func;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            double x = from;
            while (x + STEP <= to) {
                result += STEP * func.applyAsDouble(x);
                x += STEP;
            }
        }

        private double getResult() {
            return result;
        }
    }
}
