package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewMenuActivity extends AppCompatActivity {

    String usrname;
    String name;
    private final List<MenuClass> formDataList = new ArrayList<>();
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_menu);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new MenuAdapter(formDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        usrname = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        Button addButton = findViewById(R.id.addButton);
        Button submitButton = findViewById(R.id.submitButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add a new form when the button is clicked
                formDataList.add(new MenuClass());
                adapter.notifyDataSetChanged();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Upload data to Firebase
                uploadDataToFirebase();
            }
        });
    }

    private void uploadDataToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String username = "asd1234"; // Replace with the actual username
        DatabaseReference reference = database.getReference("Menus").child(usrname);

        for (int index = 0; index < formDataList.size(); index++) {
            MenuClass formData = formDataList.get(index);
//            DatabaseReference menuRef = reference.child(name);
            // Push the data to generate a unique child node ID
            reference.child(name).push().setValue(formData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Data uploaded successfully
                        Log.d("Firebase_Upload", "Data uploaded successfully");
                    } else {
                        // Handle the error
                        Log.e("Firebase_Upload", "Error: " + task.getException().getMessage());
                    }
                }
            });
        }
    }


//    for (MenuClass mc : ){

//    }
}
