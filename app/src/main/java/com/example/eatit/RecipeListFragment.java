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

        recSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSpecial = recSpinner.getSelectedItem().toString();
                fillList(selectedSpecial);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        recList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

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
                Set<String> uniqueRecipeSpecials = new HashSet<>();

                String manualEntry = "Szűrés";
                recipeNames.add(manualEntry);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String recipeSpecial = snapshot.child("recipeSpecial").getValue(String.class);
                    if (recipeSpecial != null && !recipeSpecial.isEmpty() && !uniqueRecipeSpecials.contains(recipeSpecial)) {
                        recipeNames.add(recipeSpecial);
                        uniqueRecipeSpecials.add(recipeSpecial);
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
                adapter = new RecipeAdapter(requireContext(), recipeList);
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

                        Toast.makeText(getActivity(), "Recept törölve", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
