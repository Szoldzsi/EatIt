package com.example.eatit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MenuListFragment extends Fragment {

    private static final String KEY = "username";
    String username;
    ListView menuList;
    private MenuListAdapter adapter;
    DatabaseReference reference;

    private ArrayList<MenuClass> menuListItems = new ArrayList<>();

    public MenuListFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);

        menuList = view.findViewById(R.id.menuList);
        menuListItems = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Menus").child(username);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuListItems.clear();

                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String menuDate = dateSnapshot.getKey();

                    // Create a MenuClass instance for the parent node (menu name)
                    MenuClass menu = new MenuClass();
                    menu.setName(menuDate);

                    // Add the MenuClass instance to the list
                    menuListItems.add(menu);
                }

                // Adapter setup and notifyDataSetChanged
                adapter = new MenuListAdapter(MenuListFragment.this, menuListItems);
                menuList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
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