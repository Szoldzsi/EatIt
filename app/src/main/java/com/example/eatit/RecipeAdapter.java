package com.example.eatit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Recipe> recipes;

    public RecipeAdapter(Fragment context, ArrayList<Recipe> recipes) {
        this.context = context.requireContext();
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

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        }


        Recipe recipe = (Recipe) getItem(position);


        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        nameTextView.setText("Recept neve: "+recipe.getRecipeName());

        TextView ingredientsTextView = convertView.findViewById(R.id.ingredientsTextView);
        if (recipe.getRecipeIngredients() != null && !recipe.getRecipeIngredients().isEmpty()) {
            ingredientsTextView.setText("Hozzavalok: "+recipe.getRecipeSpecial());
            ingredientsTextView.setVisibility(View.VISIBLE);
        } else {
            ingredientsTextView.setVisibility(View.GONE);
        }
        //ingredientsTextView.setText("Hozzavalok: "+recipe.getRecipeIngredients());


        TextView specialTextView = convertView.findViewById(R.id.specialTextView);
        if (recipe.getRecipeSpecial() != null && !recipe.getRecipeSpecial().isEmpty()) {
            specialTextView.setText("Specialis: "+recipe.getRecipeSpecial());
            specialTextView.setVisibility(View.VISIBLE);
        } else {
            specialTextView.setVisibility(View.GONE);
        }

        return convertView;
    }
}

