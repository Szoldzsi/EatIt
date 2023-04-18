package com.example.eatit;

public class Recipe {
    public String recipeName, recipeSpecial, recipeIngredients, key;

    public Recipe() {
    }

    public Recipe(String name, String special, String ingredients){
        this.recipeName = name;
        this.recipeSpecial = special;
        this.recipeIngredients = ingredients;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeSpecial() {
        return recipeSpecial;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setRecipeSpecial(String recipeSpecial) {
        this.recipeSpecial = recipeSpecial;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
