package org.labs.messaging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class MealBroker {

    private final BlockingQueue<MealTask> tasks;

    public MealBroker(int capacity){
        this.tasks = new ArrayBlockingQueue<>(capacity);
    }

    public CompletableFuture<MealOutcome> submit(MealRequest request) throws InterruptedException {
        CompletableFuture<MealOutcome> result = new CompletableFuture<>();
        tasks.put(new MealTask(request, result));
        return result;
    }

    public MealTask take() throws InterruptedException {
        return tasks.take();
    }
}
