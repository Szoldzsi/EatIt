package com.example.eatit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MenuDetailsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MenuClass> menuDetailList;

    public MenuDetailsAdapter(Context context, ArrayList<MenuClass> menuDetailList) {
        this.context = context;
        this.menuDetailList = menuDetailList;
    }

    @Override
    public int getCount() {
        return menuDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_menu_details, parent, false);
        }
        MenuClass menu = (MenuClass) getItem(position);
        TextView menuNameTextView = convertView.findViewById(R.id.nameTV);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView ingredientsTextView = convertView.findViewById(R.id.ingredientsTextView);
        TextView specialTextView = convertView.findViewById(R.id.specialTextView);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = inputFormat.parse(menu.getMenuDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, yyyy.MM.dd", new Locale("hu", "HU"));
        String formattedDate = outputFormat.format(date);

        menuNameTextView.setText("Étel neve: " + menu.getMenuName());
        dateTextView.setText("Dátum: " + formattedDate);
        ingredientsTextView.setText("Hozzávalók: " + menu.getMenuSpecial());
        specialTextView.setText("Speciális: " + menu.getMenuIngredients());
        return convertView;
    }
}
