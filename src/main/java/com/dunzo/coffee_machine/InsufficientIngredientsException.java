package com.dunzo.coffee_machine;

import java.util.List;

/**
 * Exception to be thrown when the required quantities of ingredients are not present
 */
public class InsufficientIngredientsException extends Throwable {

    private final List<String> insufficientIngredients; //list of ingredients that were not present in the desired quantity

    public InsufficientIngredientsException(List<String> insufficientIngredients) {
        this.insufficientIngredients = insufficientIngredients;
    }

    public List<String> getInsufficientIngredients() {
        return insufficientIngredients;
    }
}
