package com.example.eatit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RecipeFragment extends Fragment {


    TextView tw;
    EditText ingredients, special, recipeName;
    Button saveBtn;

    String username;
    private String specialString;
    private String recipeNameString;
    private String ingredientsString;
    private String recipeKey;

    private static final String KEY = "username";

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            username = savedInstanceState.getString(KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        //tw = view.findViewById(R.id.usernameTw);
        ingredients = view.findViewById(R.id.ingredientsText);
        special = view.findViewById(R.id.specialText);
        recipeName = view.findViewById(R.id.recipeText);
        saveBtn = view.findViewById(R.id.saveBtn);

        Bundle args = getArguments();
        if (args != null) {
            specialString = args.getString("special");
            recipeNameString = args.getString("recipeName");
            ingredientsString = args.getString("ingredients");
            recipeKey = args.getString("recipeKey");

            special.setText(specialString);
            recipeName.setText(recipeNameString);
            ingredients.setText(ingredientsString);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipe_Name = recipeName.getText().toString();
                String special_mark = special.getText().toString();
                String ingredients_list = ingredients.getText().toString();

                if (TextUtils.isEmpty(recipe_Name)) {
                    Toast.makeText(getActivity(), "Hiányzó recept név!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Recipe recipe = new Recipe(recipe_Name, special_mark, ingredients_list);

                try {
                    if (recipeKey != null) {
                        updateRecipe(recipeKey, recipe);
                    } else {
                        createNewRecipe(recipe);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OthersFragment othersFragment = new OthersFragment();
                othersFragment.setUsername(username);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, othersFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void updateRecipe(String recipeKey, Recipe recipe) {
        if (recipeKey != null && !recipeKey.isEmpty()) {
            DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username).child(recipeKey);
            recipeRef.setValue(recipe)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Recept sikeresen frissítve!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Hiba a frissítés során!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Hiba!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewRecipe(Recipe recipe) {
        DatabaseReference recipesRef;
        recipesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipesRef.push().setValue(recipe)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Recept sikeresen elmentve!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Hiba a mentés során!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }
}