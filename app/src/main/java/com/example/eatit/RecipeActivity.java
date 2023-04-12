package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeActivity extends AppCompatActivity {

    String username;
    TextView tw;
    EditText ingredients, special, recipeName;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        tw = findViewById(R.id.usernameTw);
        ingredients = findViewById(R.id.ingredientsText);
        special = findViewById(R.id.specialText);
        recipeName = findViewById(R.id.recipeText);
        saveBtn = findViewById(R.id.saveBtn);



        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        tw.setText(username);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipe_Name = recipeName.getText().toString();
                String special_mark = special.getText().toString();
                String ingredients_list = ingredients.getText().toString();

                if (TextUtils.isEmpty(recipe_Name)){
                    Toast.makeText(RecipeActivity.this, "Hiányzó recept név!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(ingredients_list)){
                    Toast.makeText(RecipeActivity.this, "Hiányzó hozzávalók!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Recipe recipe = new Recipe(recipe_Name, special_mark, ingredients_list);

                try {
                    FirebaseDatabase.getInstance().getReference("Recipes").child(username).push().setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RecipeActivity.this, "Recept sikeresen elmentve!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(RecipeActivity.this, "Hiba!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    System.out.println("Hiba!");
                }

            }
        });

    }
}