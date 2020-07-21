package com.dunzo.coffee_machine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class CoffeeMachine {

    private final ExecutorService outletExecutors;
    private final InventoryManager inventoryManager;

    /**
     * constructor to initialize the coffee machine
     * @param outlets
     * @param inventoryManager
     */
    public CoffeeMachine(Integer outlets, InventoryManager inventoryManager) {
        this.outletExecutors = Executors.newFixedThreadPool(outlets);
        this.inventoryManager = inventoryManager;
    }
}
