package com.jumpydoll.app.pkmnnerd.recipes;

import java.util.List;

public class Recipe {

    private final String id;
    private final String name;
    private final String category;
    private final String region;
    private final List<String> ingredients;
    private final String instructions;
    private final String imageLink;
    private final String videoLink;

    public Recipe(String id, String name, String category, String region, List<String> ingredients,
                  String instructions, String imageLink, String videoLink) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.region = region;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageLink = imageLink;
        this.videoLink = videoLink;
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

    public String getImageLink() {
        return imageLink;
    }

    public String getVideoLink() {
        return videoLink;
    }
}