package org.labs.core;


import java.util.concurrent.atomic.AtomicInteger;

public class Kitchen {

    private final AtomicInteger mealLeft;

    public Kitchen(int mealLeft){
        this.mealLeft = new AtomicInteger(mealLeft);
    }

    public boolean tryTakeMeal(){
        return mealLeft.getAndUpdate(meal -> meal > 0 ? meal - 1 : 0) > 0;
    }

    public int getMealsLeft(){
        return mealLeft.get();
    }
}
