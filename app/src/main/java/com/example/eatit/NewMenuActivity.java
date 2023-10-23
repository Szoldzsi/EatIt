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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NewMenuActivity extends AppCompatActivity {

    String usrname;
    String name;
    Date startDate;
    int duration;
    private final List<MenuClass> formDataList = new ArrayList<>();
    private MenuAdapter adapter;
    private List<Calendar> formDates;
    String grpKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_menu);
        Intent intent = getIntent();

        usrname = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        startDate = (Date) getIntent().getSerializableExtra("startDate");
        duration = intent.getIntExtra("duration", 0);
        if (intent.hasExtra("grpKey")) {
            grpKey = intent.getStringExtra("grpKey");
        } else {
            grpKey = null;
        }

        formDates = calculateDates();
        setupFormDataList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new MenuAdapter(this, formDataList, formDates, usrname);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Button submitButton = findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirebase();
            }
        });
    }

    private void uploadDataToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        /*DatabaseReference reference = database.getReference("Menus").child(usrname);*/
        DatabaseReference reference;

        if (grpKey != null && !grpKey.isEmpty()) {
            reference = database.getReference("Groups").child(grpKey).child("group_menus");
        } else {
            reference = database.getReference("Menus").child(usrname);
        }

        for (int index = 0; index < formDataList.size(); index++) {
            MenuClass formData = formDataList.get(index);
            Calendar formDate = formDates.get(index);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = dateFormat.format(formDate.getTime());


            DatabaseReference childReference = reference.child(name).child(dateString);
            childReference.setValue(formData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Firebase_Upload", "sikeres feltöltes");
                    } else {
                        Log.e("Firebase_Upload", "hiba: " + task.getException().getMessage());
                    }
                }
            });
        }
    }


    private List<Calendar> calculateDates() {
        Calendar initDate = Calendar.getInstance();
        initDate.setTime(startDate);

        List<Calendar> formDates = new ArrayList<>();
        for (int i = 0; i < duration; i++) {
            Calendar formDate = (Calendar) initDate.clone();
            formDate.add(Calendar.DAY_OF_MONTH, i);
            formDates.add(formDate);

            // Log the calculated date for debugging
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Log.d("CalculateDates", "Date " + i + ": " + dateFormat.format(formDate.getTime()));
        }

        return formDates;
    }

    private void setupFormDataList() {
        formDataList.clear(); // Clear any existing forms
        for (int i = 0; i < duration; i++) {
            formDataList.add(new MenuClass());
        }
        Log.d("NewMenuActivity", "formDataList size: " + formDataList.size());
    }


//    for (MenuClass mc : ){

//    }
}
