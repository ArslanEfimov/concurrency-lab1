package org.labs.config;

public record WaiterConfig(
        long mealDeliveryTime
) {

    public WaiterConfig {
        if(mealDeliveryTime < 0){
            throw new IllegalArgumentException("Meal delivery time must be >= 0 milliseconds");
        }
    }
}
