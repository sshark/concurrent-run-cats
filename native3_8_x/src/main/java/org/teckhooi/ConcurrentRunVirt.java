package org.teckhooi;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ConcurrentRunVirt {

    static void main(String[] args) {
        List<String> alphas = IntStream.rangeClosed('A', 'Z').mapToObj(Character::toString)
            .toList();

        var numOfTasks =
            args.length == 0
                ? alphas.size()
                : extractNumOfTaskParam(args[0])
                  .map(x -> Math.min(x, alphas.size()))
                  .orElse(alphas.size());

        var rnd = new Random();
        var startTimeMillis = System.currentTimeMillis();

        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            alphas
                .subList(0, numOfTasks)
                .forEach(s -> es.submit(
                    task(s, Duration.of(100 + rnd.nextInt(300), ChronoUnit.MILLIS))));
        }

        IO.println(
            String.format(
                "Time taken to complete all tasks, %dms",
                System.currentTimeMillis() - startTimeMillis));
    }

    private static Optional<Integer> extractNumOfTaskParam(String param) {
        try {
            return Optional.of(Integer.parseInt(param));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Callable<Void> task(String name, Duration delay) {
        return () -> {
            IO.println(
                String.format("Starting task %s on virt-%d... ", name, Thread.currentThread().threadId()));

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            IO.println(
                String.format(
                    "Task %s completed after %dms on virt-%d",
                    name, delay.toMillis(), Thread.currentThread().threadId()));

            return null;
        };
    }
}
