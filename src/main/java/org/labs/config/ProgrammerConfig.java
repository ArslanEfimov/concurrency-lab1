package org.labs.config;

public record ProgrammerConfig(
        long eatingTime,
        long discussionTime
) {
    public ProgrammerConfig{
        if(eatingTime < 0){
            throw new IllegalArgumentException("Eating time must be >= 0 milliseconds");
        }
        if(discussionTime < 0){
            throw new IllegalArgumentException("Discussion time must be >= 0 milliseconds");
        }
    }
}
