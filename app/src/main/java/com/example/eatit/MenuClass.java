package com.example.eatit;

public class MenuClass {
    private String menuName, key;
    private String menuSpecial;
    private String menuIngredients;

    public MenuClass(){

    }
    public MenuClass(String menuName, String menuSpecial, String menuIngredients){
        this.menuName = menuName;
        this.menuSpecial = menuSpecial;
        this.menuIngredients = menuIngredients;
    }


    public String getMenuName() {
        return menuName;
    }

    public String getMenuSpecial() {
        return menuSpecial;
    }

    public String getMenuIngredients() {
        return menuIngredients;
    }

    public void setName(String menuName) {
        this.menuName = menuName;
    }

    public void setSpecial(String menuSpecial) {
        this.menuSpecial = menuSpecial;
    }

    public void setIngredients(String menuIngredients) {
        this.menuIngredients = menuIngredients;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
