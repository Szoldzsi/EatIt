package com.example.eatit;

public class Menu {

    public String menuName, recipeName, ingrName, key;

    public Menu() {
    }

    public Menu(String name, String rec, String ingr){
        this.menuName = name;
        this.recipeName = rec;
        this.ingrName = ingr;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeIngredients() {
        return ingrName;
    }

    public void setMenuName(String recipeName) {
        this.menuName = menuName;
    }

    public void setRecipeName(String recipeSpecial) {
        this.recipeName = recipeName;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.ingrName = ingrName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
