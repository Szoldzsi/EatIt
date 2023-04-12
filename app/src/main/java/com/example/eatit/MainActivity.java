package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tw;
    Button logout, newRecipe, listRecipe;
    DatabaseReference reference, referenceDisplay;
    FirebaseAuth mAuth;
    FirebaseDatabase db;


    String username;
    FirebaseListAdapter adapter;
    ArrayList<String> recipeArrayList;
    boolean recipes_bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tw = findViewById(R.id.textView3);
        logout = findViewById(R.id.logoutBtn);
        newRecipe = findViewById(R.id.recipeBtn);
        listRecipe = findViewById(R.id.recipeListBtn);
        recipeArrayList = new ArrayList<String>();



        db = FirebaseDatabase.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    username = String.valueOf(dataSnapshot.getValue());
                    tw.setText(username);
                }
                else {
                    Toast.makeText(MainActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        newRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        listRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecipeListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }
}