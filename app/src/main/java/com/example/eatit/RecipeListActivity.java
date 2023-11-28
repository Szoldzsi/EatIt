package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    String username;
    TextView tw2;

    ListView recList;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList;


    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);


        //Deklarálás
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        tw2 = findViewById(R.id.textView2);
        recList = findViewById(R.id.recipeList);
        tw2.setText(username);
        recipeList = new ArrayList<>();


        //Ellenőrzés
        reference = FirebaseDatabase.getInstance().getReference("Recipes");
        reference.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    fillList();
                }
                else {
                    Toast.makeText(RecipeListActivity.this, "Hiba", Toast.LENGTH_SHORT).show();

                }
            }
        });

        recList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedRecipe = recipeList.get(position);
                String recipeKey = selectedRecipe.getKey();
                Log.d("kulcs",  recipeKey);
                deleteRecipe(recipeKey);
                return true;
            }
        });

    }

    public void fillList(){



        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipe.setKey(recipeSnapshot.getKey());
                    recipeList.add(recipe);
                    keys.add(dataSnapshot.getKey());
                }


                //Adapter beállítás
                //adapter = new RecipeAdapter(RecipeListActivity.this, recipeList);
                recList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteRecipe(String recipeKey){
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username).child(recipeKey);
        recipeRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(RecipeListActivity.this, "Recept törölve", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete recipe
                        Toast.makeText(RecipeListActivity.this, "Hiba a recept törlése közben", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}