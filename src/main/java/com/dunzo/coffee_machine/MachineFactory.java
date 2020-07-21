package com.dunzo.coffee_machine;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public static CoffeeMachine create(String inputJson) {

        //reading the json input
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode input = null;
        try {
            input = objectMapper.readTree(inputJson);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to parse json!");
            throw new RuntimeException("Unable to parse json!", e);
        }
        JsonNode machine = input.get("machine");
        Integer outlets = machine.get("outlets").get("count_n").intValue();
        Map<String, Integer> itemsQuantity = objectMapper.convertValue(machine.get("total_items_quantity"),
                new TypeReference<Map<String, Integer>>() {});

        //initializing the inventory manager
        InventoryManager inventoryManager = new InventoryManager(itemsQuantity);
        //creating the coffee machine with the given number of outlets and inventory manager
        CoffeeMachine coffeeMachine = new CoffeeMachine(outlets, inventoryManager);

        return coffeeMachine;
    }
}
