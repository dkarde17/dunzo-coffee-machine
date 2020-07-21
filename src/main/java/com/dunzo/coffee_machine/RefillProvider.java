package com.dunzo.coffee_machine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to provide refill items
 */
public class RefillProvider {
    private static Map<String, Integer> ingredients;

    public static RefillProvider createEmpty() {
        return new RefillProvider();
    }

    public static void initRefillProvider(String inputJson, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode refillPackNode = objectMapper.readTree(inputJson).get("machine").get("refill_pack");
        ingredients = new HashMap<>();
        if (refillPackNode != null) {
            objectMapper.convertValue(refillPackNode, new TypeReference<Map<String, Integer>>() {}).forEach(
                    (ingredient, quantity) -> {
                        ingredients.put(ingredient, quantity);
                    }
            );
        }
    }

    public static boolean ingredientAvailable(String ingredient) {
        return ingredients.containsKey(ingredient) && ingredients.get(ingredient).compareTo(0) > 0;
    }

    public static Integer getIngredient(String ingredient) {
        Integer foundQuantity = ingredients.get(ingredient);
        ingredients.compute(ingredient, (item, availableQuantity) -> 0);
        return foundQuantity;
    }
}