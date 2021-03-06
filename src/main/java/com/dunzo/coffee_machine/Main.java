package com.dunzo.coffee_machine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Application started...");

        String inputFileName = ApplicationConstants.DEFAULT_INPUT_FILE_NAME;
        if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty())
            inputFileName = args[0];
        LOGGER.debug("Input File name = {}", inputFileName);

        CoffeeMachineSimulation.run(inputFileName);

        LOGGER.info("Application finished!");
    }
}
