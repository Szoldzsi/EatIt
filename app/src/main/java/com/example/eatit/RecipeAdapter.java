package com.example.eatit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Recipe> recipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout for each list item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        }

        // Get the Recipe object for this list item
        Recipe recipe = (Recipe) getItem(position);

        // Set the recipe name and ingredients text views
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        nameTextView.setText(recipe.getRecipeName());

        TextView ingredientsTextView = convertView.findViewById(R.id.ingredientsTextView);
        ingredientsTextView.setText(recipe.getRecipeIngredients());

        // Set the recipe special text view if it is not empty
        TextView specialTextView = convertView.findViewById(R.id.specialTextView);
        if (recipe.getRecipeSpecial() != null && !recipe.getRecipeSpecial().isEmpty()) {
            specialTextView.setText(recipe.getRecipeSpecial());
            specialTextView.setVisibility(View.VISIBLE);
        } else {
            specialTextView.setVisibility(View.GONE);
        }

        return convertView;
    }
}

