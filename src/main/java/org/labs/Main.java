package org.labs;

import org.labs.app.Simulation;
import org.labs.app.utils.SimulationStatisticsPrinter;
import org.labs.config.ProgrammerConfig;
import org.labs.config.SimulationConfig;
import org.labs.config.WaiterConfig;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ProgrammerConfig programmerConfig = new ProgrammerConfig(0L, 1L);
        WaiterConfig waiterConfig = new WaiterConfig(1L);
        SimulationConfig simulationConfig = new SimulationConfig(7, 2, 1000, 1L, 2L);
        Simulation simulation = new Simulation(simulationConfig, programmerConfig, waiterConfig, new SimulationStatisticsPrinter(System.out));
        simulation.start();
    }
}