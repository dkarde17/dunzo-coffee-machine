package com.dunzo.coffee_machine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * An instance of BeveragePreparationTask represents the beverage to be created and ingredients required
 * it interacts with the inventory manager to get the ration
 */
public class BeveragePreparationTask implements Callable<OutletResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeveragePreparationTask.class);
    private final ThreadLocal<String> beverageName;
    private final ThreadLocal<Map<String, Integer>> ingredientsRequired;
    private final InventoryManager inventoryManager;

    public BeveragePreparationTask(String beverageName, Map<String, Integer> ingredientsRequired, InventoryManager inventoryManager) {
        this.beverageName = ThreadLocal.withInitial(() -> beverageName);
        this.ingredientsRequired = ThreadLocal.withInitial(() -> ingredientsRequired);
        this.inventoryManager = inventoryManager;
    }

    @Override
    public OutletResponse call() throws Exception {
        //get ration from inventoryManager to make the beverage
        String beverageName = this.beverageName.get();
        LOGGER.debug("Getting ration to make {}", beverageName);
        synchronized (inventoryManager) {
            try {
                inventoryManager.ration(ingredientsRequired.get());
                LOGGER.info("{} is prepared", beverageName);
            } catch (InsufficientIngredientsException e) {
                LOGGER.error("Insufficient ingredients for making {}!", beverageName);
                try {
                    attemptAfterRefill(e.getInsufficientIngredients(), ingredientsRequired.get());
                    LOGGER.info("{} is prepared", beverageName);
                } catch (InsufficientIngredientsException insufficientIngredientsException) {
                    LOGGER.error("{} cannot be prepared because following are not available: {}", beverageName,
                            insufficientIngredientsException.getInsufficientIngredients());
                    return new OutletResponse(beverageName, false);
                }
            }
        }
        return new OutletResponse(beverageName, true);
    }

    /**
     * Method to request the Refill Provider to provide a refill and then attempt the rationing again
     *
     * @param insufficientIngredients
     * @param ingredientsRequired
     */
    private void attemptAfterRefill(List<String> insufficientIngredients, Map<String, Integer> ingredientsRequired) throws InsufficientIngredientsException {
        LOGGER.debug("Checking if refill provider has all the missing ingredients...");
        if (insufficientIngredients == null || insufficientIngredients.size() == 0)
            throw new IllegalStateException("Insufficient Ingredients can't be null or 0 at this point!");
        List<String> missingIngredients = new ArrayList<>();
        for (String ingredient : insufficientIngredients) {
            if (RefillProvider.ingredientAvailable(ingredient)) {
                refill(ingredient, RefillProvider.getIngredient(ingredient));
                LOGGER.debug("{} is available, added it to the inventory!", ingredient);
            } else {
                missingIngredients.add(ingredient);
                LOGGER.debug("{} is unavailable, added to missing ingredients!", ingredient);
            }
        }
        if (missingIngredients.size() > 0)
            throw new InsufficientIngredientsException(missingIngredients);
        else inventoryManager.ration(ingredientsRequired);
    }

    /**
     * Method to replenish the given ingredient in the inventory manager
     * @param ingredient
     * @param quantity
     */
    private void refill(String ingredient, Integer quantity) {
        inventoryManager.refill(ingredient, quantity);
    }
}
