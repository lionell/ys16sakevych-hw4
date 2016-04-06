package ua.yandex.fj;

import ua.yandex.utils.Logger;
import ua.yandex.utils.RandomUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by lionell on 4/6/16.
 *
 * @author Ruslan Sakevych
 */
public class WordCounter extends RecursiveTask<Map<String, Integer>> {
    private static final int THRESHOLD = 1;
    private final String[] words;
    private final int from;
    private final int to;

    private WordCounter(String[] words, int from, int to) {
        this.words = words;
        this.from = from;
        this.to = to;
    }

    public WordCounter(String[] words) {
        this(words, 0, words.length - 1);
    }

    public static void main(String[] args) {
        String[] words = RandomUtils.randomStringArray(1000, 1, 1);

        Map<String, Integer> frequency =
                (new ForkJoinPool()).invoke(new WordCounter(words));

        for (Map.Entry<String, Integer> e : frequency.entrySet()) {
            Logger.log(e.getKey() + ": " + e.getValue());
        }
    }

    @Override
    protected Map<String, Integer> compute() {
        if (to - from + 1 <= THRESHOLD) {
            return countSequential();
        }
        return countParallel();
    }

    private Map<String, Integer> countParallel() {
        int mid = from + (to - from) / 2;

        WordCounter leftCounter = new WordCounter(words, from, mid);
        leftCounter.fork();

        WordCounter rightCounter = new WordCounter(words, mid + 1, to);
        rightCounter.fork();

        return merge(leftCounter.join(), rightCounter.join());
    }

    private Map<String, Integer> countSequential() {
        Map<String, Integer> frequency = new HashMap<>();

        for (int i = from; i <= to; i++) {
            int newValue = 1;

            if (frequency.containsKey(words[i])) {
                newValue = frequency.get(words[i]) + 1;
            }

            frequency.put(words[i], newValue);
        }

        return frequency;
    }

    private Map<String, Integer> merge(Map<String, Integer> m1,
                                           Map<String, Integer> m2) {
        Map<String, Integer> result = new HashMap<>(m1);
        m2.forEach((k, v) -> result.merge(k, v, (v1, v2) -> v1 + v2));
        return result;
    }
}