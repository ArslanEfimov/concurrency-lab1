package org.labs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.labs.app.Simulation;
import org.labs.app.utils.SimulationStatisticsPrinter;
import org.labs.config.ProgrammerConfig;
import org.labs.config.SimulationConfig;
import org.labs.config.WaiterConfig;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulationTest {

    private static final Path REPORT_FILE = Path.of("build/reports/simulation/simulation-results.txt");

    @BeforeAll
    static void prepareReportFile() throws IOException{
        Files.createDirectories(REPORT_FILE.getParent());
        Files.writeString(
                REPORT_FILE,
                "SIMULATION TEST RESULTS\n\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public static Stream<Arguments> simulationConfigurations(){
        return Stream.of(
                Arguments.of("Two programmers and one waiter", 2, 1, 40, 0, 0, 0),
                Arguments.of("Seven programmers and two waiter", 7, 2, 1_000, 1, 2, 1),
                Arguments.of("Many programmers and one waiter, which creates a bottleneck", 12, 1, 30, 0, 0, 0),
                Arguments.of("More waiters than programmers", 9, 15, 50, 0, 0, 0),
                Arguments.of("Programmers slow eating and fast discussing", 7, 2, 100, 10, 0, 0),
                Arguments.of("Programmers long time discussing and fast eating", 7, 2, 100, 0, 10, 0),
                Arguments.of("Programmers wait for slow delivery", 7, 2, 100, 0, 0, 10)
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource(value = "simulationConfigurations")
    void philosophersDinnerSimulationTest(
            String scenarioName, int programmersCount, int waitersCount, int mealsCount,
            long eatingTime, long discussionTime, long mealDeliveryTime) throws ExecutionException, InterruptedException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try(PrintStream output = new PrintStream(
                buffer,
                true,
                StandardCharsets.UTF_8
        )) {
            SimulationConfig simulationConfig = new SimulationConfig(programmersCount, waitersCount,
                    mealsCount, 15L, 15L);
            ProgrammerConfig programmerConfig = new ProgrammerConfig(eatingTime, discussionTime);
            WaiterConfig waiterConfig = new WaiterConfig(mealDeliveryTime);
            SimulationStatisticsPrinter printer = new SimulationStatisticsPrinter(output);
            Simulation simulation = new Simulation(simulationConfig, programmerConfig, waiterConfig, printer);
            simulation.start();
        }
        String report = buffer.toString(StandardCharsets.UTF_8);
        appendScenarioResult(scenarioName, report);
        List<String> reportLines = report.lines().toList();
        assertAll(
                () -> assertTrue(reportLines.contains("Total eaten: " + mealsCount), "Incorrect total eaten value"),
                () -> assertTrue(reportLines.contains("Not yet eaten: 0"), "Some meals were not eaten"),
                () -> assertTrue(reportLines.contains("Remaining in kitchen: 0"), "Kitchen is not empty")
        );
    }

    private void appendScenarioResult(String scenarioName, String report) throws IOException {
        try(BufferedWriter writer = Files.newBufferedWriter(
                REPORT_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            writer.write("==================================================");
            writer.newLine();

            writer.write("Scenario: " + scenarioName);
            writer.newLine();

            writer.write("==================================================");
            writer.newLine();

            writer.write(report.stripTrailing());
            writer.newLine();
            writer.newLine();

        }
    }
}
