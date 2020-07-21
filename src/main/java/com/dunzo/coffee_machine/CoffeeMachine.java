package com.dunzo.coffee_machine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class to imitate an automated coffee machine
 */
public class CoffeeMachine {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeMachine.class);
    private final ExecutorService outletExecutors;
    private final InventoryManager inventoryManager;
    private final List<Future<OutletResponse>> results;

    /**
     * constructor to initialize the initial state of the coffee machine
     *
     * @param outlets
     * @param inventoryManager
     */
    public CoffeeMachine(Integer outlets, InventoryManager inventoryManager) {
        this.outletExecutors = Executors.newFixedThreadPool(outlets);
        this.inventoryManager = inventoryManager;
        this.results = new ArrayList<>();
    }

    /**
     * Method to submit and queue the orders at the given outlets
     * @param beverageOrders
     */
    public void submitAllOrders(Map<String, Map<String, Integer>> beverageOrders) {
        beverageOrders.forEach((name, recipe) -> {
            LOGGER.info("Submitting order for {}", name);
            results.add(outletExecutors.submit(new BeveragePreparationTask(name, recipe, inventoryManager)));
            LOGGER.info("Order submitted!");
        });
    }

    /**
     * method to shutdown the coffee machine and return the responses of the execution at the outlet
     * @return
     */
    public List<OutletResponse> shutDown() throws ExecutionException, InterruptedException {
        this.outletExecutors.shutdown();
        List<OutletResponse> responses = new ArrayList<>();
        for (Future<OutletResponse> result : results) {
            responses.add(result.get());
        }
        return responses ;
    }
}
