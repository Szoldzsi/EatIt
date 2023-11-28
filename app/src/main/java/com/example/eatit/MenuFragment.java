package com.example.eatit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class MenuFragment extends Fragment {

    EditText menuName, recipeName, ingredientsName;
    Button saveBtn;

    String username;

    private static final String KEY = "username";

    public MenuFragment() {
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
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        menuName = view.findViewById(R.id.menuText);
        recipeName = view.findViewById(R.id.recipeTextM);
        ingredientsName = view.findViewById(R.id.ingrText);
        saveBtn = view.findViewById(R.id.saveBtn2);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String menu_Name = menuName.getText().toString();
                String rec_name = recipeName.getText().toString();
                String ingredients_list = ingredientsName.getText().toString();

                if (TextUtils.isEmpty(menu_Name)){
                    Toast.makeText(getActivity(), "Hiányzó étel név!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Menu menu = new Menu(menu_Name, rec_name, ingredients_list);

                try {
                    FirebaseDatabase.getInstance().getReference("Menus").child(username).push().setValue(menu).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Menü sikeresen elmentve!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Hiba!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    System.out.println("Hiba!");
                }

            }
        });

        return view;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }
}