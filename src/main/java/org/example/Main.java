package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static final int NUMBER_THREAD = 100_000;
    public static final int NUMBER_CHAR = 10_000;

    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
        AtomicInteger countA = new AtomicInteger();
        AtomicInteger countB = new AtomicInteger();
        AtomicInteger countC = new AtomicInteger();

        Thread generator = new Thread(() -> {
            String letters = "abc";
            Random random = new Random();
            while (true) {
                String text = generateText(letters, NUMBER_CHAR);
                try {
                    queue.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        Thread analyzer = new Thread(() -> {
            while (true) {
                try {
                    String text = queue.take();
                    countA.addAndGet(countOccurrences(text, 'a'));
                    countB.addAndGet(countOccurrences(text, 'b'));
                    countC.addAndGet(countOccurrences(text, 'c'));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        generator.start();
        analyzer.start();

        Thread.sleep(10000);
        generator.interrupt();
        analyzer.interrupt();

        generator.join();
        analyzer.join();

        System.out.println("Символов a = " + countA.get());
        System.out.println("Символов b = " + countB.get());
        System.out.println("Символов c = " + countC.get());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static int countOccurrences(String text, char targetChar) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == targetChar) {
                count++;
            }
        }
        return count;
    }
}