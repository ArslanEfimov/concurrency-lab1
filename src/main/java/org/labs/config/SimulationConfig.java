package org.labs.config;

public record SimulationConfig(
        int programmersCount,
        int mealsCount,
        int waitersCount,
        long statisticsReportInitialDelaySeconds,
        long statisticsReportIntervalSeconds
) {
    public SimulationConfig {
        if(programmersCount < 2) throw new IllegalArgumentException("Programmers count must be >= 2");
        if(waitersCount < 1) throw new IllegalArgumentException("Waiters count must be >= 1");
        if(mealsCount < programmersCount) throw new IllegalArgumentException("Every programmer must have an initial meal");
        if(statisticsReportInitialDelaySeconds < 0) throw new IllegalArgumentException("Statistics report initial delay must be >= 0 seconds");
        if(statisticsReportIntervalSeconds <= 0) throw new IllegalArgumentException("Statistics report interval must be > 0 seconds");
    }
}
