package org.traffic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.traffic.model.SimulationInput;
import org.traffic.model.SimulationOutput;
import org.traffic.utils.SimulationParser;

import java.io.IOException;

public class Main {
    static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("App usage: java -jar sim.jar <input.json> <output.json>");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        var parser = new SimulationParser();

        try {
            SimulationInput simulationInput = parser.parseInput(inputPath);

            var simulator = new TrafficSimulator();

            SimulationOutput simulationOutput = simulator.runSimulation(simulationInput);

            parser.saveOutput(outputPath ,simulationOutput);

        } catch (IOException e) { // TODO - better exception handling
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
