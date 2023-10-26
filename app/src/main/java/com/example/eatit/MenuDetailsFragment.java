package com.example.eatit;

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
    String username;
    private ListView listView;
    private MenuDetailsAdapter adapter;
    private ArrayList<MenuClass> menuDetailList;


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

        // Find the TextViews in the layout
//        TextView menuNameTextView = view.findViewById(R.id.menuNameTextView);
//        TextView menuIngredientsTextView = view.findViewById(R.id.menuIngredientsTextView);
//        TextView menuSpecialTextView = view.findViewById(R.id.menuSpecialTextView);
//        TextView dateTV = view.findViewById(R.id.dateTV);

        listView = view.findViewById(R.id.menuDetailsList);
        menuDetailList = new ArrayList<>();
        // Retrieve the menu key from the bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String menuName = bundle.getString("menuName");
            Log.d("MenuDetailsFragment", "Menu Name: " + menuName);
            // Perform a new Firebase query to get the children nodes under the menuKey
            DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("Menus")
                    .child(username).child(menuName);

//            menuRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Assuming you have a MenuClass to hold the data
//                        MenuClass menu = dataSnapshot.getValue(MenuClass.class);
//
//                        // Set the text values for the TextViews
//                        menuNameTextView.setText("Menu Name: " + menu.getMenuName());
//                        menuIngredientsTextView.setText("Ingredients: " + menu.getMenuIngredients());
//                        menuSpecialTextView.setText("Special: " + menu.getMenuSpecial());
//                    } else {
//                        // Handle the case where the menuKey doesn't exist in the database
//                        // Display an error message or take appropriate action
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle onCancelled event if needed
//                }
//            });
            menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String dateKey = dateSnapshot.getKey();
                        String menu = dateSnapshot.child("menuName").getValue(String.class);
                        String menuIngredients = dateSnapshot.child("menuIngredients").getValue(String.class);
                        String menuSpecial = dateSnapshot.child("menuSpecial").getValue(String.class);

//                        dateTV.append(dateKey);
//                        menuNameTextView.setText("Menü neve: " + menu);
//                        menuIngredientsTextView.append("Hozzávalók: " + menuIngredients + "\n");
//                        menuSpecialTextView.append("Speciális: " + menuSpecial + "\n");
                        MenuClass menuDetailItem = new MenuClass(menu, menuIngredients, menuSpecial, dateKey);
                        menuDetailList.add(menuDetailItem);
//                        String menuName, String menuSpecial, String menuIngredients, String menuDate
                    }
                    adapter = new MenuDetailsAdapter(getContext(), menuDetailList);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event if needed
                }
            });
        }

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