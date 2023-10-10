package com.example.eatit;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private final List<MenuClass> data;
    private List<Calendar> formDates;

    public MenuAdapter(List<MenuClass> data, List<Calendar> formDates) {
        this.data = data;
        this.formDates = formDates;
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

    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position; // Return a unique view type for each position
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etSpecial, etIngredients;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.etName);
            etSpecial = itemView.findViewById(R.id.etSpecial);
            etIngredients = itemView.findViewById(R.id.etIngredients);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
