package org.labs.core;


import org.labs.messaging.MealBroker;
import org.labs.config.ProgrammerConfig;
import org.labs.messaging.MealOutcome;
import org.labs.messaging.MealRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Programmer implements Runnable {

    private final long id;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final MealBroker mealBroker;
    private volatile int eaten;
    private final ProgrammerConfig programmerConfig;

    public Programmer(long id, Spoon leftSpoon, Spoon rightSpoon, MealBroker mealBroker, ProgrammerConfig programmerConfig){
        this.id = id;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.mealBroker = mealBroker;
        this.eaten = 0;
        this.programmerConfig = programmerConfig;
    }

    @Override
    public void run() {
        try {
            boolean hasMeal = true;
            while (hasMeal){
                eat();
                CompletableFuture<MealOutcome> delivery = requestMeal();
                discuss();
                MealOutcome deliveryOutcome = delivery.get();
                hasMeal = deliveryOutcome == MealOutcome.SERVED;
            }
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }

    }

    private void eat() throws InterruptedException{
        Spoon first;
        Spoon second;

        if(leftSpoon.getId() < rightSpoon.getId()){
            first = leftSpoon;
            second = rightSpoon;
        }
        else {
            first = rightSpoon;
            second = leftSpoon;
        }

        try {
            first.take();
            second.take();
            Thread.sleep(programmerConfig.eatingTime());
            eaten++;
        }
        finally {
            second.put();
            first.put();
        }

    }

    private void discuss() throws InterruptedException {
        Thread.sleep(programmerConfig.discussionTime());
    }

    private CompletableFuture<MealOutcome> requestMeal() throws InterruptedException {
        return mealBroker.submit(new MealRequest(id));
    }

    public long getId() {
        return id;
    }

    public int getEaten() {
        return eaten;
    }
}
