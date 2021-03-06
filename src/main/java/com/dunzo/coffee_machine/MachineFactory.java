package com.dunzo.coffee_machine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Class to create and initialize a coffee machine from the given input json
 */
public class MachineFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MachineFactory.class);

    public static CoffeeMachine create(String inputJson, ObjectMapper objectMapper) {

        //reading the json input
        JsonNode input = null;
        try {
            input = objectMapper.readTree(inputJson);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to parse json!");
            throw new RuntimeException("Unable to parse json!", e);
        }
        JsonNode machine = input.get(ApplicationConstants.MACHINE);
        Integer outlets = machine.get(ApplicationConstants.OUTLETS).get(ApplicationConstants.COUNT_N).intValue();
        LOGGER.debug("Total number of outlets to be added to the coffee machine = {}", outlets);
        Map<String, Integer> itemsQuantity = objectMapper.convertValue(machine.get(ApplicationConstants.TOTAL_ITEMS_QUANTITY),
                new TypeReference<Map<String, Integer>>() {});
        itemsQuantity.forEach((item, quantity) -> LOGGER.debug("Adding {} quantity of {} to the inventory manager",
                quantity, item));
        //initializing the inventory manager
        InventoryManager inventoryManager = new InventoryManager(itemsQuantity);
        //creating the coffee machine with the given number of outlets and inventory manager

        return new CoffeeMachine(outlets, inventoryManager);
    }
}
