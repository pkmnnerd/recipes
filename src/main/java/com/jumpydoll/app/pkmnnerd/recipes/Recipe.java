package com.jumpydoll.app.pkmnnerd.recipes;

import java.util.List;

public class Recipe {

    private final String id;
    private final String name;
    private final String category;
    private final String region;
    private final List<String> ingredients;
    private final String instructions;

    public Recipe(String id, String name, String category, String region, List<String> ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.region = region;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getRegion() {
        return region;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}