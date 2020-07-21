package com.dunzo.coffee_machine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to imitate an automated coffee machine
 */
public class CoffeeMachine {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeMachine.class);
    private final ExecutorService outletExecutors;
    private final InventoryManager inventoryManager;

    /**
     * constructor to initialize the coffee machine
     *
     * @param outlets
     * @param inventoryManager
     */
    public CoffeeMachine(Integer outlets, InventoryManager inventoryManager) {
        this.outletExecutors = Executors.newFixedThreadPool(outlets);
        this.inventoryManager = inventoryManager;
    }

    public void submitAllOrders(Map<String, Map<String, Integer>> beverageOrders) {
        beverageOrders.forEach((name, recipe) -> {
            LOGGER.info("Submitting order for {}", name);
            outletExecutors.submit(new Task(name, recipe, inventoryManager));
            LOGGER.info("Order submitted!");
        });
    }

    public void shutDown() {
        this.outletExecutors.shutdown();
    }

    private static class Task implements Runnable {

        private final ThreadLocal<String> beverageName;
        private final ThreadLocal<Map<String, Integer>> ingredientsRequired;
        private final InventoryManager inventoryManager;

        public Task(String beverageName, Map<String, Integer> ingredientsRequired, InventoryManager inventoryManager) {
            this.beverageName = ThreadLocal.withInitial(() -> beverageName);
            this.ingredientsRequired = ThreadLocal.withInitial(() -> ingredientsRequired);
            this.inventoryManager = inventoryManager;
        }

        /**
         * Method to ask Refill Provider to provide a refill and then attempt the rationing again
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

        @Override
        public void run() {
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
                    }
                }
            }
        }

        private void refill(String ingredient, Integer quantity) {
            inventoryManager.refill(ingredient, quantity);
        }
    }
}
