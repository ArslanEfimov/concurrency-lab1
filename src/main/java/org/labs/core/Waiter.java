package org.labs.core;

import org.labs.messaging.MealBroker;
import org.labs.messaging.MealTask;
import org.labs.messaging.MealOutcome;

public class Waiter implements Runnable{

    private final long id;
    private final MealBroker mealBroker;
    private final Kitchen kitchen;

    public Waiter(long id, MealBroker mealBroker, Kitchen kitchen) {
        this.id = id;
        this.mealBroker = mealBroker;
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        try {
            while(true){
                MealTask mealTask = mealBroker.take();
                boolean hasMeal = kitchen.tryTakeMeal();
                if(!hasMeal){
                    mealTask.complete(MealOutcome.OUT_OF_MEALS);
                    continue;
                }
                mealTask.complete(MealOutcome.SERVED);

            }
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }

    public long getId() {
        return id;
    }
}
