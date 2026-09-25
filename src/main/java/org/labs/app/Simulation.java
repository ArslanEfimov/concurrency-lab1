package org.labs.app;

import org.labs.app.utils.SimulationStatisticsPrinter;
import org.labs.config.WaiterConfig;
import org.labs.messaging.MealBroker;
import org.labs.config.ProgrammerConfig;
import org.labs.config.SimulationConfig;
import org.labs.core.Programmer;
import org.labs.core.Spoon;
import org.labs.core.Kitchen;
import org.labs.core.Waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Simulation {
    private final SimulationConfig simulationConfig;
    private final ProgrammerConfig programmerConfig;
    private final WaiterConfig waiterConfig;
    private final SimulationStatisticsPrinter printer;

    public Simulation(SimulationConfig simulationConfig,
                      ProgrammerConfig programmerConfig,
                      WaiterConfig waiterConfig,
                      SimulationStatisticsPrinter printer){
        this.simulationConfig = simulationConfig;
        this.programmerConfig = programmerConfig;
        this.waiterConfig = waiterConfig;
        this.printer = printer;

    }

    public void start() throws ExecutionException, InterruptedException {
        int programmersCount = simulationConfig.programmersCount();
        int waitersCount = simulationConfig.waitersCount();

        Kitchen kitchen = new Kitchen(simulationConfig.mealsCount() - programmersCount);
        Spoon[] spoons = createSpoons(programmersCount);
        MealBroker mealBroker = new MealBroker(programmersCount);
        ExecutorService programmersPool = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService waitersPool = Executors.newVirtualThreadPerTaskExecutor();
        ScheduledExecutorService statisticsPool = Executors.newSingleThreadScheduledExecutor();

        List<Programmer> programmers = new ArrayList<>();
        List<Future<?>> tasks = new ArrayList<>();
        boolean completed = false;
        try {
            printer.printConfiguration(simulationConfig);
            for (int i = 0; i < waitersCount; i++) {
                waitersPool.submit(new Waiter(i, mealBroker, kitchen, waiterConfig));
            }

            for (int i = 0; i < programmersCount; i++) {
                Spoon leftSpoon = spoons[i];
                Spoon rightSpoon = spoons[(i + 1) % programmersCount];
                Programmer programmer = new Programmer(i, leftSpoon, rightSpoon, mealBroker, programmerConfig);
                programmers.add(programmer);
                Future<?> task = programmersPool.submit(programmer);
                tasks.add(task);
            }

            statisticsPool.scheduleWithFixedDelay(
                    () -> printer.printProgress(programmers, kitchen, simulationConfig),
                    simulationConfig.statisticsReportInitialDelaySeconds(),
                    simulationConfig.statisticsReportIntervalSeconds(),
                    TimeUnit.SECONDS
            );

            for (Future<?> task : tasks){
                task.get();
            }
            completed = true;
        }finally {
            statisticsPool.shutdown();
            if(completed){
                programmersPool.shutdown();
            }
            else {
                programmersPool.shutdownNow();
            }
            waitersPool.shutdownNow();
        }

        if(!statisticsPool.awaitTermination(5, TimeUnit.SECONDS)){
            statisticsPool.shutdownNow();
        }

        if(!waitersPool.awaitTermination(5, TimeUnit.SECONDS)){
            throw new IllegalStateException("Waiters did not terminate within 5 seconds");
        }

        printer.printProgress(programmers, kitchen, simulationConfig);

    }

    public Spoon[] createSpoons(int programmersCount){
        Spoon[] spoons = new Spoon[programmersCount];

        for (int i = 0; i < programmersCount; i++) {
            spoons[i] = new Spoon(i);
        }
        return spoons;
    }

}
