package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
    String groupKey, owner, username, menuName;
    TextView menuNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_menu_details);

        detailsLV = findViewById(R.id.menuDetailsListView);
        menuNameTV = findViewById(R.id.menuDetailsGrp);
        menuDetailList = new ArrayList<>();
        adapter = new MenuDetailsAdapter(this, menuDetailList);
        detailsLV.setAdapter(adapter);
        Intent intent = getIntent();
        if (intent != null) {
            groupKey = intent.getStringExtra("groupKey");
            username = intent.getStringExtra("username");
            menuName = intent.getStringExtra("menuName");
            owner = intent.getStringExtra("owner");
            menuNameTV.setText(menuName);
            menuRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupKey).child("group_menus").child(menuName);
            menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String dateKey = dateSnapshot.getKey();
                        String menu = dateSnapshot.child("menuName").getValue(String.class);
                        String menuIngredients = dateSnapshot.child("menuSpecial").getValue(String.class);
                        String menuSpecial = dateSnapshot.child("menuIngredients").getValue(String.class);

                        MenuClass menuDetailItem = new MenuClass(menu, menuIngredients, menuSpecial, dateKey);
                        menuDetailList.add(menuDetailItem);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        detailsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected menu
                MenuClass selectedMenu = menuDetailList.get(position);

                Intent modifyIntent = new Intent(ListGroupMenuDetails.this, MenuDetailModifyActivity.class);

                modifyIntent.putExtra("grpKey", groupKey);
                modifyIntent.putExtra("owner", owner);
                modifyIntent.putExtra("usrname", username);
                modifyIntent.putExtra("mnName", menuName);
                modifyIntent.putExtra("menuName", selectedMenu.getMenuName());
                modifyIntent.putExtra("menuIngr", selectedMenu.getMenuIngredients());
                modifyIntent.putExtra("menuSpec", selectedMenu.getMenuSpecial());
                modifyIntent.putExtra("menuDate", selectedMenu.getMenuDate());


                startActivity(modifyIntent);

                return true;
            }
        });
    }
}