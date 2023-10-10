package com.example.eatit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MenuListAdapter extends BaseAdapter {
    private Context context;

    private ArrayList<MenuClass> recipes;

    public MenuListAdapter(Fragment context, ArrayList<MenuClass> recipes) {
        this.context = context.requireContext();
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_menu, parent, false);
        }


        MenuClass recipe = (MenuClass) getItem(position);


        TextView nameTextView = convertView.findViewById(R.id.menuName);
        nameTextView.setText("Menü neve: "+recipe.getMenuName());



        return convertView;
    }
}
