package org.labs;

import org.labs.app.Simulation;
import org.labs.config.ProgrammerConfig;
import org.labs.config.SimulationConfig;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ProgrammerConfig programmerConfig = new ProgrammerConfig(0L, 1L);
        SimulationConfig simulationConfig = new SimulationConfig(7, 100000, 2, 0L, 1L);
        Simulation simulation = new Simulation(simulationConfig, programmerConfig);
        simulation.start();
    }
}