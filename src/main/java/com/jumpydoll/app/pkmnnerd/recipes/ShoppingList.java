package com.jumpydoll.app.pkmnnerd.recipes;

import java.util.List;

public class ShoppingList {
    List<Ingredient> shoppingList;

    public ShoppingList(List<Ingredient> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public List<Ingredient> getShoppingList() {
        return shoppingList;
    }

    public static class Ingredient {
        String ingredientName;
        List<String> quantities;

        public Ingredient(String ingredientName, List<String> quantities) {
            this.ingredientName = ingredientName;
            this.quantities = quantities;
        }

        public String getIngredientName() {
            return ingredientName;
        }

        public List<String> getQuantities() {
            return quantities;
        }
    }
}
