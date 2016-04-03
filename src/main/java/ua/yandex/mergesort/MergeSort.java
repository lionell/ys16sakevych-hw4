package ua.yandex.mergesort;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by lionell on 4/3/16.
 *
 * @author Ruslan Sakevych
 */
public class MergeSort {
    private static final Random random = new Random();
    private static final int THREAD_COUNT =
            2 * Runtime.getRuntime().availableProcessors();
    private static final int ELEMENT_COUNT = 1000;

    public static void main(String[] args) {
        int[] a = generateArray(ELEMENT_COUNT);
        int[] b = Arrays.copyOf(a, a.length);
        printArray(a);
        long startTime = System.currentTimeMillis();
        parallelMergeSort(a, THREAD_COUNT);
        long endTime = System.currentTimeMillis();
        System.out.println("Parallel: " + ((endTime - startTime) / 1000.0)
                + "s.");
        printArray(a);
        startTime = System.currentTimeMillis();
        mergeSort(b);
        endTime = System.currentTimeMillis();
        System.out.println("Simple: " + ((endTime - startTime) / 1000.0)
                + "s.");
        printArray(b);
    }

    private static int[] generateArray(int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; ++i) {
            array[i] = random.nextInt(1000);
        }
        return array;
    }

    private static void printArray(int[] array) {
        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.println();
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
        private final int[] array;
        private final int nThreads;

        public Sorter(int[] array, int nThreads) {
            this.array = array;
            this.nThreads = nThreads;
        }

        @Override
        public void run() {
            parallelMergeSort(array, nThreads);
        }
    }
}
