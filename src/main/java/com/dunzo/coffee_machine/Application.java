package com.dunzo.coffee_machine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to start the execution of the coffee machine simulation application
 * <p>
 * Reads an input json file from the resources directory
 * <p>
 * input json needs to be of the following schema:
 * {
 * "machine": {
 * "outlets": {
 * "count_n": Integer
 * },
 * "total_items_quantity": JsonData, //String, Integer Pairs of the initial ingredients and quantity
 * "beverages": JsonArray, //each jsondata will contain name and String, Integer pair of the required ingredients and quantities
 * "refill_pack": JsonData (optional) ////String, Integer Pairs of the ingredients and quantity available for refill
 * }
 * }
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOGGER.info("Application started...");

        String inputFileName = "input.json";
        if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty())
            inputFileName = args[0];
        LOGGER.debug("Input File name = {}", inputFileName);

        //read the input json file to initialize parameters like number of outlets, initial ingredients, etc.
        LOGGER.debug("Loading {} file...", inputFileName);
        String inputJson;
        try {
            URL resource = Application.class.getClassLoader().getResource(inputFileName);
            if (resource == null) {
                LOGGER.error("Resource {} not found!", inputFileName);
                throw new RuntimeException("Resource " + inputFileName + " not found!");
            }
            inputJson = Files.readAllLines(Paths.get(resource.toURI()))
                    .stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.error("Unable to read the file!");
            throw new RuntimeException("Unable to read the file!", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Error while getting the {} file path!", inputFileName);
            throw new RuntimeException("Error while getting the " + inputFileName + " file path!", e);
        }
        LOGGER.debug("{} = " + "\n" + inputJson, inputFileName);
        LOGGER.debug("loaded successfully!");

        ObjectMapper objectMapper = new ObjectMapper();

        //initialize coffee machine with the given number of outlets and ingredients parameters
        LOGGER.info("Starting Coffee Machine...");
        CoffeeMachine coffeeMachine = MachineFactory.create(inputJson, objectMapper);
        LOGGER.info("Coffee Machine started!");

        //fetch all the beverage orders to be served
        LOGGER.info("Getting all the beverage orders");
        Map<String, Map<String, Integer>> beverageOrders = null;
        try {
            beverageOrders = readBeverageTasks(inputJson, objectMapper);
        } catch (JsonProcessingException e) {
            LOGGER.info("Error reading beverage tasks!");
            throw new RuntimeException("Error reading beverage tasks!");
        }
        if (beverageOrders == null)
            throw new IllegalStateException("beverages can't be null!");
        LOGGER.info("Total beverage orders found = {}", beverageOrders.size());

        //fetch refill-pack if present
        LOGGER.info("Initializing Refill Provider...");
        try {
            RefillProvider.initRefillProvider(inputJson, objectMapper);
        } catch (JsonProcessingException e) {
            LOGGER.info("Error reading the refill-pack!");
            throw new RuntimeException("Error reading the refill-pack!", e);
        }
        LOGGER.info("Refill Provider Initialized!");

        //execute all beverage orders
        LOGGER.info("submitting beverage orders...");
        coffeeMachine.submitAllOrders(beverageOrders);
        LOGGER.info("{} beverage orders submitted!", beverageOrders.size());

        //shutdown coffee machine
        LOGGER.info("Shutting down coffee machine...");
        coffeeMachine.shutDown();
        LOGGER.info("Coffee Machine shut down!");

        LOGGER.info("Application finished!");
    }

    /**
     * Method to read the beverage orders (beverage name and ingredients to be included) from the input
     *
     * @param inputJson
     * @param objectMapper
     * @return
     * @throws JsonProcessingException
     */
    private static Map<String, Map<String, Integer>> readBeverageTasks(String inputJson, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode beveragesNode = objectMapper.readTree(inputJson).get("machine").get("beverages");
        return objectMapper.convertValue(beveragesNode,
                new TypeReference<Map<String, Map<String, Integer>>>() {
                });
    }
}
