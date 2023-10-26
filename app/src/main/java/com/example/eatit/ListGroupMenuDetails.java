package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListGroupMenuDetails extends AppCompatActivity {

    ListView detailsLV;
    private DatabaseReference menuRef;
    private ArrayList<MenuClass> menuDetailList;
    private MenuDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_menu_details);

        detailsLV = findViewById(R.id.menuDetailsListView);
        menuDetailList = new ArrayList<>();
        adapter = new MenuDetailsAdapter(this, menuDetailList);
        detailsLV.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            String groupKey = intent.getStringExtra("groupKey");
            String username = intent.getStringExtra("username");
            String menuName = intent.getStringExtra("menuName");
            menuRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupKey).child("group_menus").child(menuName);
            menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String dateKey = dateSnapshot.getKey();
                        String menu = dateSnapshot.child("menuName").getValue(String.class);
                        String menuIngredients = dateSnapshot.child("menuIngredients").getValue(String.class);
                        String menuSpecial = dateSnapshot.child("menuSpecial").getValue(String.class);

                        MenuClass menuDetailItem = new MenuClass(menu, menuIngredients, menuSpecial, dateKey);
                        menuDetailList.add(menuDetailItem);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event if needed
                }
            });
        }
    }
}