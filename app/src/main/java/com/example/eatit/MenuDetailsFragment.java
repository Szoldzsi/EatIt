package com.example.eatit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuDetailsFragment extends Fragment {

    private static final String KEY = "username";
    String username, menuName;
    private ListView listView;
    private MenuDetailsAdapter adapter;
    private ArrayList<MenuClass> menuDetailList;
    TextView menuNameTV;


    public MenuDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (savedInstanceState != null) {
                username = savedInstanceState.getString(KEY);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_details, container, false);

        listView = view.findViewById(R.id.menuDetailsList);
        menuDetailList = new ArrayList<>();
        menuNameTV = view.findViewById(R.id.menuNameDetails);
        Bundle bundle = getArguments();
        if (bundle != null) {
            menuName = bundle.getString("menuName");
            DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("Menus")
                    .child(username).child(menuName);
            menuNameTV.setText(menuName);
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
                    adapter = new MenuDetailsAdapter(getContext(), menuDetailList);
                    listView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            // Get the selected menu
            MenuClass selectedMenu = menuDetailList.get(position);

            // Create an Intent to start the modify activity
            Intent modifyIntent = new Intent(requireContext(), MenuDetailModifyActivity.class);

            // Put the necessary data in the Intent
            modifyIntent.putExtra("mnName", menuName);
            modifyIntent.putExtra("menuName", selectedMenu.getMenuName());
            modifyIntent.putExtra("menuIngr", selectedMenu.getMenuIngredients());
            modifyIntent.putExtra("menuSpec", selectedMenu.getMenuSpecial());
            modifyIntent.putExtra("usrname", username);
            modifyIntent.putExtra("menuDate", selectedMenu.getMenuDate());

            // Start the ModifyMenuActivity
            startActivity(modifyIntent);

            return true; // Return true to consume the long press event
        });

        return view;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }
}