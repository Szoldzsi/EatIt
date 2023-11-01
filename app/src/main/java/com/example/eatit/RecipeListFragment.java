package com.example.eatit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RecipeListFragment extends Fragment{

    String username;
    Spinner recSpinner;
    TextView tw2;
    private DatabaseReference recipeNamesRef;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    private Context context;
    ListView recList;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList;
    DatabaseReference reference;
    private static final String KEY = "username";

    public RecipeListFragment() {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeNamesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        context = requireContext();
        // Now you can safely access the context here
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        //Deklarálás
        recList = view.findViewById(R.id.recipeList);
        recipeList = new ArrayList<>();
        recSpinner = view.findViewById(R.id.recipeFilter);
        populateFilterSpinner(recSpinner);

        registerForContextMenu(recList);

/*
        //Ellenőrzés
        reference = FirebaseDatabase.getInstance().getReference("Recipes");
        reference.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    String selectedFilter = recSpinner.getSelectedItem().toString();
                    DataSnapshot dataSnapshot = task.getResult();
                    fillList(recSpinner, selectedFilter);
                }
                else {
                    Toast.makeText(getActivity(), "Hiba", Toast.LENGTH_SHORT).show();

                }
            }
        });
*/

        recSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSpecial = recSpinner.getSelectedItem().toString();
                fillList(selectedSpecial);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected, if needed
            }
        });

        recList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Return false to let the context menu be displayed
                return false;
            }
        });

/*        recList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {

                    Recipe selectedRecipe = recipeList.get(position);
                    String recipeKey = selectedRecipe.getKey();
                    String special = selectedRecipe.getRecipeSpecial();
                    String recipeName = selectedRecipe.getRecipeName();
                    String ingredients = selectedRecipe.getRecipeIngredients();

                    // Start RecipeFragment and pass the details in a Bundle
                    startRecipeFragment(special, recipeName, ingredients, recipeKey);
                }
                lastClickTime = clickTime;
            }
        });*/

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.recipeList) {
            menu.setHeaderTitle("Lehetőségek");
            menu.add(0, v.getId(), 0, "Szerkesztés");
            menu.add(0, v.getId(), 0, "Törlés");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Törlés")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            Recipe selectedRecipe = recipeList.get(index);
            String recipeKey = selectedRecipe.getKey();
            deleteRecipe(recipeKey);
            return true;
        }
        else if (item.getTitle().equals("Szerkesztés")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            Recipe selectedRecipe = recipeList.get(index);
            String recipeKey = selectedRecipe.getKey();
            String special = selectedRecipe.getRecipeSpecial();
            String recipeName = selectedRecipe.getRecipeName();
            String ingredients = selectedRecipe.getRecipeIngredients();
            startRecipeFragment(special, recipeName, ingredients, recipeKey);
            return true;}

        return super.onContextItemSelected(item);
    }

    private void populateFilterSpinner(Spinner spinner) {
        recipeNamesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipeNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> recipeNames = new ArrayList<>();
                Set<String> uniqueRecipeSpecials = new HashSet<>(); // Use a set to track unique values

                String manualEntry = "Szűrés";
                recipeNames.add(manualEntry);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String recipeSpecial = snapshot.child("recipeSpecial").getValue(String.class);
                    if (recipeSpecial != null && !recipeSpecial.isEmpty() && !uniqueRecipeSpecials.contains(recipeSpecial)) {
                        recipeNames.add(recipeSpecial);
                        uniqueRecipeSpecials.add(recipeSpecial); // Add the special to the set
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, recipeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }
    private void startRecipeFragment(String special, String recipeName, String ingredients, String recipeKey) {
        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString("special", special);
        args.putString("recipeName", recipeName);
        args.putString("ingredients", ingredients);
        args.putString("recipeKey", recipeKey);
        recipeFragment.setArguments(args);
        recipeFragment.setUsername(username);


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, recipeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /*public void fillList(Spinner spinner, String selectedSpecial){

        if("Szűrés".equals(recSpinner.getSelectedItem().toString())){
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
                    adapter = new RecipeAdapter(RecipeListFragment.this, recipeList);
                    recList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            recipeNamesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
            recipeNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> recipeNames = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        if (recipe != null) {
                            // Check if the recipe's special matches the selectedSpecial
                            if (selectedSpecial.equals(recipe.getRecipeSpecial())) {
                                recipeNames.add(recipe.getRecipeName());
                            }
                        }
                    }
                    adapter = new RecipeAdapter(RecipeListFragment.this, recipeList);
                    recList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }
    }*/

    public void fillList(String selectedSpecial) {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipe.setKey(recipeSnapshot.getKey());

                    if ("Szűrés".equals(selectedSpecial) || selectedSpecial.equals(recipe.getRecipeSpecial())) {
                        recipeList.add(recipe);
                        keys.add(dataSnapshot.getKey());
                    }
                }

                // Adapter beállítás
                adapter = new RecipeAdapter(requireContext(), recipeList);
                recList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }


    public void deleteRecipe(String recipeKey){
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username).child(recipeKey);
        recipeRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getActivity(), "Recept törölve", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete recipe
                        Toast.makeText(getActivity(), "Hiba a recept törlése közben", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }

}
