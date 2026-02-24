package org.traffic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.traffic.model.SimulationInput;
import org.traffic.model.SimulationOutput;

import java.io.File;
import java.io.IOException;

public class SimulationParser {

    private final ObjectMapper mapper;

    public SimulationParser() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public SimulationInput parseInput(String inputFilePath) throws IOException {
        File file = new File(inputFilePath);
        if (!file.exists())
            throw new IOException("Input file doesn't exist: " + inputFilePath);
        return mapper.readValue(file, SimulationInput.class);
    }

    public void saveOutput(String filePath, SimulationOutput output) throws IOException {
        mapper.writeValue(new File(filePath), output);
    }
}
