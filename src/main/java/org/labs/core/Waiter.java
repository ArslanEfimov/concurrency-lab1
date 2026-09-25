package org.labs.core;

import org.labs.config.WaiterConfig;
import org.labs.messaging.MealBroker;
import org.labs.messaging.MealTask;
import org.labs.messaging.MealOutcome;

public class Waiter implements Runnable{

    private final long id;
    private final MealBroker mealBroker;
    private final Kitchen kitchen;
    private final WaiterConfig waiterConfig;

    public Waiter(long id, MealBroker mealBroker, Kitchen kitchen, WaiterConfig waiterConfig) {
        this.id = id;
        this.mealBroker = mealBroker;
        this.kitchen = kitchen;
        this.waiterConfig = waiterConfig;
    }

    @Override
    public void run() {
        try {
            while(true){
                processNextRequest();
            }
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }

    public void processNextRequest() throws InterruptedException{
        MealTask mealTask = mealBroker.take();
        boolean hasMeal = kitchen.tryTakeMeal();
        if(!hasMeal){
            mealTask.complete(MealOutcome.OUT_OF_MEALS);
            return;
        }
        deliverMeal(mealTask);
    }

    private void deliverMeal(MealTask mealTask) throws InterruptedException{
        try {
            Thread.sleep(waiterConfig.mealDeliveryTime());
            mealTask.complete(MealOutcome.SERVED);
        } catch (InterruptedException exception) {
            mealTask.completeExceptionally(exception);
            throw exception;
        }
    }

    public long getId() {
        return id;
    }
}
