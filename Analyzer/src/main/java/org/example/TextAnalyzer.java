package org.example;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TextAnalyzer {
    private static final int TEXTS_COUNT = 10_000;
    private static final int TEXT_LENGTH = 100_000;
    private static final int QUEUE_CAPACITY = 100;
    private static final String SYMBOLS = "abc";

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {
        Thread producer = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < TEXTS_COUNT; i++) {
                StringBuilder sb = new StringBuilder(TEXT_LENGTH);
                for (int j = 0; j < TEXT_LENGTH; j++) {
                    sb.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
                }
                String text = sb.toString();
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumerA = new Thread(() -> processQueue(queueA, 'a'));
        Thread consumerB = new Thread(() -> processQueue(queueB, 'b'));
        Thread consumerC = new Thread(() -> processQueue(queueC, 'c'));

        producer.start();
        consumerA.start();
        consumerB.start();
        consumerC.start();

        try {
            producer.join();
            consumerA.join();
            consumerB.join();
            consumerC.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void processQueue(BlockingQueue<String> queue, char targetChar) {
        int maxCount = 0;
        String maxText = null;

        try {
            for (int i = 0; i < TEXTS_COUNT; i++) {
                String text = queue.take();
                int count = (int) text.chars().filter(ch -> ch == targetChar).count();
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Максимальное количество '" + targetChar + "': " + maxCount);
    }
}
