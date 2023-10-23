package com.example.eatit;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private final List<MenuClass> data;
    private List<Calendar> formDates;
    private String username;
    private DatabaseReference recipeNamesRef;
    private Context context;

    public MenuAdapter(Context context,List<MenuClass> data, List<Calendar> formDates,String username) {
        this.context = context;
        this.data = data;
        this.formDates = formDates;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuClass formData = data.get(position);
        Log.d("DataObjectHash", "Data object hash code: " + System.identityHashCode(formData));

        // Attach TextWatchers to capture user input and update formData
        holder.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                formData.setName(s.toString());

                Log.d("FormUpdate", "Form at position " + position + " updated: Name = " + s.toString());
                Log.d("UserInput", "User input: " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.existingRecipeCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.recipeSpinner.setVisibility(View.VISIBLE);
                populateRecipeSpinner(holder.recipeSpinner);
                holder.filterCB.setVisibility(View.VISIBLE);
            } else {
                holder.recipeSpinner.setVisibility(View.GONE);
                holder.filterCB.setChecked(false);
                holder.filterCB.setVisibility(View.GONE);
                holder.filterSpinner.setVisibility(View.GONE);
                holder.recipeSpinner.setAdapter(null);
                holder.etName.setText("");
                holder.etSpecial.setText("");
                holder.etIngredients.setText("");
            }
        });

        holder.filterCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.filterSpinner.setVisibility(View.VISIBLE);
                populateFilterSpinner(holder.filterSpinner);

            } else {
                holder.filterSpinner.setVisibility(View.GONE);
                holder.filterSpinner.setAdapter(null);
                populateRecipeSpinner(holder.recipeSpinner); 
            }
        });

        holder.filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpecial = (String) parent.getItemAtPosition(position);
                populateRecipeSpinnerFilteres(holder.recipeSpinner, selectedSpecial);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        holder.recipeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRecipeName = (String) parent.getItemAtPosition(position);
                retrieveRecipeData(selectedRecipeName, holder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.etSpecial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                formData.setSpecial(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.etIngredients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                formData.setIngredients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        Calendar dateCalendar = formDates.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String formattedDate = dateFormat.format(dateCalendar.getTime());
        holder.dateTextView.setText(formattedDate);
    }

    private void populateRecipeSpinner(Spinner spinner) {
        recipeNamesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipeNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> recipeNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String recipeName = snapshot.child("recipeName").getValue(String.class);
                    if (recipeName != null) {
                        recipeNames.add(recipeName);
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

    private void populateRecipeSpinnerFilteres(Spinner spinner, String selectedSpecial) {
        recipeNamesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipeNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> recipeNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String recipeName = snapshot.child("recipeName").getValue(String.class);
                    String recipeSpecial = snapshot.child("recipeSpecial").getValue(String.class);

                    if (recipeName != null && recipeSpecial != null && recipeSpecial.equals(selectedSpecial)) {
                        recipeNames.add(recipeName);
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


    private void populateFilterSpinner(Spinner spinner) {
        recipeNamesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);
        recipeNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> recipeNames = new ArrayList<>();
                Set<String> uniqueRecipeSpecials = new HashSet<>(); // Use a set to track unique values

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
    private void retrieveRecipeData(String selectedRecipeName, ViewHolder holder) {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Recipes").child(username);

        // Add a listener to search for the recipe with the matching name
        recipesRef.orderByChild("recipeName").equalTo(selectedRecipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve the special and ingredients data
                    String special = snapshot.child("recipeSpecial").getValue(String.class);
                    String ingredients = snapshot.child("recipeIngredients").getValue(String.class);

                    // Update the EditText fields in the ViewHolder with the retrieved data
                    holder.etName.setText(selectedRecipeName);
                    holder.etSpecial.setText(special);
                    holder.etIngredients.setText(ingredients);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etSpecial, etIngredients;
        CheckBox existingRecipeCB, filterCB;
        Spinner recipeSpinner, filterSpinner;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.etName);
            etSpecial = itemView.findViewById(R.id.etSpecial);
            etIngredients = itemView.findViewById(R.id.etIngredients);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            existingRecipeCB = itemView.findViewById(R.id.existingMenuCB);
            recipeSpinner = itemView.findViewById(R.id.recipeSpinner);
            filterCB = itemView.findViewById(R.id.filterCB);
            filterSpinner = itemView.findViewById(R.id.filterSpinner);
        }
    }
}
