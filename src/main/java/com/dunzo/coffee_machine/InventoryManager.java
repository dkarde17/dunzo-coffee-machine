package com.dunzo.coffee_machine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to manage the inventory
 * Used by the CoffeeMachine to ration ingredients required for the beverages
 */
public class InventoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryManager.class);
    private final Map<String, Integer> itemsQuantityCache;

    public InventoryManager(Map<String, Integer> itemsQuantity) {
        this.itemsQuantityCache = itemsQuantity;
    }

    /**
     * Method to check if the required ingredients are present
     * If the ingredients are present then the ingredients are used up and the remaining quantities are updated
     * @param ingredientsRequired
     * @throws InsufficientIngredientsException
     */
    public void ration(Map<String, Integer> ingredientsRequired) throws InsufficientIngredientsException {
        LOGGER.debug("Started rationing...");

        /*
        check if all the ingredients are present so that they can be used up
        if not present then throw an exception
         */
        LOGGER.debug("Checking if all items are present...");
        checkAllItemsPresent(ingredientsRequired);
        LOGGER.debug("All ingredients are present in sufficient quantity!");

        //ration the ingredients i.e. use up the ingredients and update the remaining quantities
        updateQuantities(ingredientsRequired);
        LOGGER.debug("Rationed ingredients!");
    }

    /**
     * Method to update the ingredients quantities after using to make any beverage
     * @param ingredientsRequired
     */
    private void updateQuantities(Map<String, Integer> ingredientsRequired) {
        for (Map.Entry<String, Integer> entry : ingredientsRequired.entrySet()) {
            String ingredient = entry.getKey();
            LOGGER.debug("updating for {}", ingredient);
            Integer quantityRequired = entry.getValue();
            itemsQuantityCache.compute(ingredient, (item, availableQuantity) -> {
                LOGGER.debug("Available quantity = {}", availableQuantity);
                LOGGER.debug("Required quantity = {}", quantityRequired);
                int remainingQuantity = Math.subtractExact(availableQuantity, quantityRequired);
                LOGGER.debug("Remaining quantity = {}", remainingQuantity);
                return remainingQuantity;
            });
        }
    }

    /**
     * Method to check if all the required ingredients are present
     * If not present then throw an exception with the list of all the missing ingredients
     *
     * @param ingredientsRequired
     * @throws InsufficientIngredientsException
     */
    private void checkAllItemsPresent(Map<String, Integer> ingredientsRequired) throws InsufficientIngredientsException {
        List<String> insufficientIngredients = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ingredientsRequired.entrySet()) {
            String ingredient = entry.getKey();
            LOGGER.debug("Checking for {}", ingredient);
            Integer quantityRequired = entry.getValue();
            LOGGER.debug("Required quantity = {}", quantityRequired);
            if (itemsQuantityCache.containsKey(ingredient) && itemsQuantityCache.get(ingredient) != null &&
                    quantityRequired.compareTo(itemsQuantityCache.get(ingredient)) <= 0) {
                LOGGER.debug("Available Quantity = {}", itemsQuantityCache.get(ingredient));
            } else {
                insufficientIngredients.add(ingredient);
            }
        }
        if (insufficientIngredients.size() > 0) {
            throw new InsufficientIngredientsException(insufficientIngredients);
        }
    }

    public void refill(String ingredient, Integer quantity) {
        itemsQuantityCache.computeIfPresent(ingredient, (item, availableQuantity) -> availableQuantity + quantity);
        itemsQuantityCache.computeIfAbsent(ingredient, item -> quantity);
    }
}
