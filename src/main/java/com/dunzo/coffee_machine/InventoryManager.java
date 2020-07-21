package com.dunzo.coffee_machine;

import java.util.Map;

public class InventoryManager {

    private final Map<String, Integer> itemsQuantity;

    public InventoryManager(Map<String, Integer> itemsQuantity) {
        this.itemsQuantity = itemsQuantity;
    }
}
