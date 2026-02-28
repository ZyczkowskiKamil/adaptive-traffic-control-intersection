package org.traffic;

import javafx.application.Application;
import org.traffic.gui.SimulationGui;
import org.traffic.model.dto.SimulationInput;
import org.traffic.model.dto.SimulationOutput;
import org.traffic.service.TrafficSimulator;
import org.traffic.utils.SimulationParser;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    static void main(String[] args) {
        boolean isGuiMode = Arrays.asList(args).contains("--gui");

        if (isGuiMode) {
            System.out.println("Gui");
            Application.launch(SimulationGui.class, args);
        } else {
            runConsoleSimulation(args);
        }
    }

    private static void runConsoleSimulation(String[] args) {
        if (args.length < 2) {
            System.err.println("App usage:");
            System.err.println("  Console mode: java --enable-native-access=ALL-UNNAMED -jar sim.jar <input.json> <output.json>");
            System.err.println("  GUI mode: App usage: java --enable-native-access=ALL-UNNAMED -jar sim.jar --gui");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        var parser = new SimulationParser();

        try {
            SimulationInput simulationInput = parser.parseInput(inputPath);
            var simulator = new TrafficSimulator();

            SimulationOutput simulationOutput = simulator.runSimulation(simulationInput);

            parser.saveOutput(outputPath, simulationOutput);
        } catch (IOException e) { // TODO - better exception handling
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
