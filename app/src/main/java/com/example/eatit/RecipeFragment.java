package com.example.eatit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.FirebaseDatabase;


public class RecipeFragment extends Fragment {


    TextView tw;
    EditText ingredients, special, recipeName;
    Button saveBtn;

    String username;

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

        //tw.setText(username);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipe_Name = recipeName.getText().toString();
                String special_mark = special.getText().toString();
                String ingredients_list = ingredients.getText().toString();

                if (TextUtils.isEmpty(recipe_Name)){
                    Toast.makeText(getActivity(), "Hiányzó recept név!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Recipe recipe = new Recipe(recipe_Name, special_mark, ingredients_list);

                try {
                    FirebaseDatabase.getInstance().getReference("Recipes").child(username).push().setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Recept sikeresen elmentve!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Hiba!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    System.out.println("Hiba!");
                }

            }
        });

        return view;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }
}