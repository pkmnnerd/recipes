package com.jumpydoll.app.pkmnnerd.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.google.gson.JsonParser;

@RestController
public class RecipesController {

    final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/api/cereal")
    public List<Recipe> searchRecipes() {
        List<Recipe> response = new ArrayList<>();
        List<String> cerealIngredients = Arrays.asList("1 cup Cereal", "1/2 cup Milk");
        Recipe cereal = new Recipe(
                "1",
                "Cereal",
                "Breakfast",
                "American",
                cerealIngredients,
                "Step 1: Pour the cereal into a bowl\n Step 2: Pour the milk into the bowl\nStep 3: Enjoy!"
        );
        response.add(cereal);
        return response;
    }

    @GetMapping("/api/recipes")
    public List<Recipe> searchRecipes(@RequestParam String term) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format("https://www.themealdb.com/api/json/v1/1/search.php?s=%s", term)))
                .build();
        HttpResponse<String> searchResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject mealDbResponse = JsonParser.parseString(searchResponse.body()).getAsJsonObject();
        JsonArray mealDbRecipes = mealDbResponse.getAsJsonArray("meals");

        List<Recipe> recipes = new ArrayList<>();
        for(JsonElement mealDbRecipe: mealDbRecipes) {
            JsonObject mealDbRecipeObj = mealDbRecipe.getAsJsonObject();
            String id = mealDbRecipeObj.get("idMeal").getAsString();
            String name = mealDbRecipeObj.get("strMeal").getAsString();
            String category = mealDbRecipeObj.get("strCategory").getAsString();
            String region = mealDbRecipeObj.get("strArea").getAsString();
            String instructions =mealDbRecipeObj.get("strInstructions").getAsString();
            List<String> ingredients = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                String ingredientPropertyName = String.format("strIngredient%d", i);
                String measurePropertyName = String.format("strMeasure%d", i);
                if (!mealDbRecipeObj.get(ingredientPropertyName).isJsonNull() && !mealDbRecipeObj.get(measurePropertyName).isJsonNull()) {
                    String ingredient = mealDbRecipeObj.get(ingredientPropertyName).getAsString();
                    String measure = mealDbRecipeObj.get(measurePropertyName).getAsString();
                    if (!ingredient.isEmpty()) {
                        ingredients.add(String.format("%s %s", measure, ingredient));
                    }
                }
            }
            Recipe recipe = new Recipe(id, name, category, region, ingredients, instructions);
            recipes.add(recipe);
        }
        return recipes;
    }

    @GetMapping("/api/shoppinglist")
    public ShoppingList generateShoppingList(@RequestParam String recipeIds) throws IOException, InterruptedException {
        String[] recipeIdArray = recipeIds.split(",");
        Map<String, List<String>> ingredientQuantities = new HashMap<>();
        for(String recipeId : recipeIdArray) {
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create(String.format("https://www.themealdb.com/api/json/v1/1/lookup.php?i=%s", recipeId)))
                    .build();

            HttpResponse<String> searchResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject mealDbResponse = JsonParser.parseString(searchResponse.body()).getAsJsonObject();
            JsonArray mealDbRecipes = mealDbResponse.getAsJsonArray("meals");
            JsonObject mealDbRecipeObj = mealDbRecipes.get(0).getAsJsonObject();
            for (int i = 1; i <= 20; i++) {
                String ingredientPropertyName = String.format("strIngredient%d", i);
                String measurePropertyName = String.format("strMeasure%d", i);
                if (!mealDbRecipeObj.get(ingredientPropertyName).isJsonNull() && !mealDbRecipeObj.get(measurePropertyName).isJsonNull()) {
                    String ingredient = mealDbRecipeObj.get(ingredientPropertyName).getAsString();
                    String measure = mealDbRecipeObj.get(measurePropertyName).getAsString();
                    if (!ingredient.isEmpty()) {
                        ingredientQuantities.putIfAbsent(ingredient, new ArrayList<>());
                        ingredientQuantities.get(ingredient).add(measure);
                    }
                }
            }
        }
        List<ShoppingList.Ingredient> shoppingList = ingredientQuantities.entrySet().stream()
                .map((entry) -> new ShoppingList.Ingredient(entry.getKey(), entry.getValue()))
                .toList();
        return new ShoppingList(shoppingList);
    }
}
