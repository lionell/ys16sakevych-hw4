package ua.yandex.mergesort;

import ua.yandex.utils.Logger;
import ua.yandex.utils.RandomUtils;

import java.util.Arrays;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class MergeSort {
    private static final int THREAD_COUNT =
            2 * Runtime.getRuntime().availableProcessors();
    private static final int ELEMENT_COUNT = 1000;

    public static void main(String[] args) {
        int[] a = RandomUtils.randomIntArray(ELEMENT_COUNT);
        int[] b = Arrays.copyOf(a, a.length);

        Logger.logIntArray(a);

        long startTime = System.currentTimeMillis();
        parallelMergeSort(a, THREAD_COUNT);
        long endTime = System.currentTimeMillis();

        Logger.logFormat("Parallel: %fs.", (endTime - startTime) / 1000d);

        Logger.logIntArray(a);
        assert isSorted(a) : "Not sorted after parallelMergeSort!";

        startTime = System.currentTimeMillis();
        mergeSort(b);
        endTime = System.currentTimeMillis();

        Logger.logFormat("Simple: %fs.", (endTime - startTime) / 1000d);

        Logger.logIntArray(b);
        assert isSorted(b) : "Not sorted after mergeSort!";
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i] < a[i - 1]) {
                return false;
            }
        }
        return true;
    }

    public static void parallelMergeSort(int[] a, int nThreads) {
        if (nThreads == 1) {
            mergeSort(a);
        } else if (a.length > 1) {
            int[] left = Arrays.copyOfRange(a, 0, a.length / 2);
            int[] right = Arrays.copyOfRange(a, a.length / 2, a.length);

            Thread leftThread =
                    new Thread(new Sorter(left, (nThreads - 1) / 2));
            Thread rightThread =
                    new Thread(new Sorter(right, (nThreads - 1) / 2));
            leftThread.start();
            rightThread.start();

            try {
                leftThread.join();
                rightThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            merge(left, right, a);
        }
    }

    public static void mergeSort(int[] a) {
        if (a.length == 1) {
            return;
        }

        int[] left = Arrays.copyOfRange(a, 0, a.length / 2);
        int[] right = Arrays.copyOfRange(a, a.length / 2, a.length);

        mergeSort(left);
        mergeSort(right);

        merge(left, right, a);
    }

    public static void merge(int[] left, int[] right, int[] a) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < a.length; k++) {
            if (j >= right.length || (i < left.length && left[i] < right[j])) {
                a[k] = left[i];
                i++;
            } else {
                a[k] = right[j];
                j++;
            }
        }
    }

    private static class Sorter implements Runnable {
        private final int[] a;
        private final int nThreads;

        public Sorter(int[] a, int nThreads) {
            this.a = a;
            this.nThreads = nThreads;
        }

        @Override
        public void run() {
            parallelMergeSort(a, nThreads);
        }
    }
}
