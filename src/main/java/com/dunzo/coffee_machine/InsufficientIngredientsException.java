package com.dunzo.coffee_machine;

import java.util.List;

public class InsufficientIngredientsException extends Throwable {

    private final List<String> insufficientIngredients;

    public InsufficientIngredientsException(List<String> insufficientIngredients) {
        this.insufficientIngredients = insufficientIngredients;
    }

    public List<String> getInsufficientIngredients() {
        return insufficientIngredients;
    }
}
