package ua.yandex.sumofseries;

import ua.yandex.utils.Logger;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class Experiment {
    private static final double N = 3e3;
    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS =
            2 * Runtime.getRuntime().availableProcessors() + 2;

    public static void main(String[] args) {
        for (int threadCount = MIN_THREADS; threadCount <= MAX_THREADS;
             threadCount++) {
            double threadsTime = benchmarkThreads(threadCount);
            Logger.logFormat("%d THREADS %f", threadCount, threadsTime);

            double concurrentTime = benchmarkConcurrent(threadCount);
            Logger.logFormat("%d CONCURRENT %f", threadCount, concurrentTime);
        }
    }

    private static double benchmarkConcurrent(int threadCount) {
        long startTime = System.currentTimeMillis();
        double val = ua.yandex.sumofseries.utilconcurrent.SumOfSeries.eval(N,
                threadCount);
        long endTime = System.currentTimeMillis();

        assert Math.abs(val) < 1e-4;

        return (endTime - startTime) / 1e3;
    }

    private static double benchmarkThreads(int threadCount) {
        long startTime = System.currentTimeMillis();
        double val = ua.yandex.sumofseries.threads.SumOfSeries.eval(N,
                threadCount);
        long endTime = System.currentTimeMillis();

        assert Math.abs(val) < 1e-4;

        return (endTime - startTime) / 1e3;
    }
}
