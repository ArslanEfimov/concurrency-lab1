package org.labs.messaging;

public record MealRequest(
        long programmerId,
        int mealEaten
){
}