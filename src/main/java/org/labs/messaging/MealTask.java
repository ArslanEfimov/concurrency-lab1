package org.labs.messaging;

import java.util.concurrent.CompletableFuture;

public class MealTask {
    private final MealRequest request;
    private final CompletableFuture<MealOutcome> result;

    public MealTask(MealRequest request, CompletableFuture<MealOutcome> result) {
        this.request = request;
        this.result = result;
    }

    public MealRequest getRequest(){
        return request;
    }

    public void complete(MealOutcome mealOutcome){
        result.complete(mealOutcome);
    }
}
