package org.labs.app.utils;

import org.labs.config.SimulationConfig;
import org.labs.core.Programmer;
import org.labs.core.Kitchen;

import java.io.PrintStream;
import java.util.List;

public class SimulationStatisticsPrinter {
    private final PrintStream out;

    public SimulationStatisticsPrinter(PrintStream out) {
        this.out = out;
    }

    public void printConfiguration(SimulationConfig config) {
        out.printf(
                "Programmers: %d%nWaiters: %d%nTotal meals: %d%n",
                config.programmersCount(),
                config.waitersCount(),
                config.mealsCount()
        );
    }

    public void printProgress(List<Programmer> programmers, Kitchen kitchen, SimulationConfig config){
        StringBuilder report = new StringBuilder("\nCurrent status:\n");
        int totalEaten = 0;
        for (Programmer programmer : programmers){
            int eaten = programmer.getEaten();
            report.append("Programmer ")
                    .append(programmer.getId())
                    .append(": meals eaten ")
                    .append(eaten)
                    .append('\n');
            totalEaten += eaten;
        }
        report.append("Total eaten: ").append(totalEaten).append('\n');
        report.append("Not yet eaten: ")
                .append(config.mealsCount() - totalEaten)
                .append('\n');
        report.append("Remaining in kitchen: ")
                .append(kitchen.getMealsLeft())
                .append('\n');

        out.print(report);
        out.flush();
    }
}
