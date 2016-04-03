package ua.yandex.sumofseries.utilconcurrent;

import ua.yandex.sumofseries.SineCosineFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        ExecutorService threadPool = Executors.newFixedThreadPool(M);
        List<Calculator> calculatorPool = new ArrayList<>();
        final double step = 2 * N / M;
        for (int i = 0; i < M; i++) {
            calculatorPool.add(new Calculator(
                    new SineCosineFunction(),
                    -N + i * step, -N + (i + 1) * step));
        }

        List<Future<Double>> resultPool = new ArrayList<>();

        try {
            resultPool = threadPool.invokeAll(calculatorPool);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();

        double result = 0.0;
        try {
            for (Future<Double> doubleFuture : resultPool) {
                result += doubleFuture.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static class Calculator implements Callable<Double> {
        private static final double STEP = 1e-4;
        private final DoubleUnaryOperator func;
        private final double from;
        private final double to;

        private Calculator(DoubleUnaryOperator func, double from, double to) {
            this.func = func;
            this.from = from;
            this.to = to;
        }

        @Override
        public Double call() throws Exception {
            double result = 0.0;
            double x = from;
            while (x + STEP <= to) {
                result += STEP * func.applyAsDouble(x);
                x += STEP;
            }
            return result;
        }
    }
}
