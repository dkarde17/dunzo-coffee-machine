package com.dunzo.coffee_machine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to provide refill items if available in the input
 */
public class RefillProvider {
    private static Map<String, Integer> refillStore;

    public static RefillProvider createEmpty() {
        return new RefillProvider();
    }

    /**
     * initializes the refillStore from the refill_pack of the input
     * @param inputJson
     * @param objectMapper
     * @throws JsonProcessingException
     */
    public static void initRefillProvider(String inputJson, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode refillPackNode = objectMapper.readTree(inputJson).get(ApplicationConstants.MACHINE).get(ApplicationConstants.REFILL_PACK);
        refillStore = new HashMap<>();
        if (refillPackNode != null) {
            objectMapper.convertValue(refillPackNode, new TypeReference<Map<String, Integer>>() {}).forEach(
                    (ingredient, quantity) -> {
                        refillStore.put(ingredient, quantity);
                    }
            );
        }
    }

    /**
     * Method to check whether the required ingredients are available in the refill store
     * @param ingredient
     * @return
     */
    public static boolean ingredientAvailable(String ingredient) {
        return refillStore.containsKey(ingredient) && refillStore.get(ingredient).compareTo(0) > 0;
    }

    /**
     * Method to get ingredient from the refill store
     * Used by the inventory manager to replenish the inventory
     * @param ingredient
     * @return
     */
    public static Integer getIngredient(String ingredient) {
        Integer foundQuantity = refillStore.get(ingredient);
        refillStore.compute(ingredient, (item, availableQuantity) -> 0);
        return foundQuantity;
    }
}