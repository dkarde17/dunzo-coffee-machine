package com.dunzo.coffee_machine;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Class to start the execution of the coffee machine simulation application
 *
 * Reads an input.json file from the resources directory
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOGGER.info("Application started...");

        LOGGER.debug("Loading input.json file...");
        String inputJson;
        try {
            inputJson = Files.readAllLines(Paths.get(Application.class.getClassLoader().getResource("input.json").toURI()))
                    .stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.error("Unable to read the file!");
            throw new RuntimeException("Unable to read the file!", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Error while getting the input.json file path!");
            throw new RuntimeException("Error while getting the input.json file path!",e);
        }
        LOGGER.debug("input.json = " + "\n" + inputJson);
        LOGGER.debug("loaded successfully!");

        LOGGER.info("Creating Coffee Machine...");
        CoffeeMachine coffeeMachine = MachineFactory.create(inputJson);
        LOGGER.info("Coffee Machine created!");


        LOGGER.info("Application finished!");
    }
}
