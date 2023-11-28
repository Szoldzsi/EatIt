package com.example.eatit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuDetailModifyActivity extends AppCompatActivity {

    Button saveBtn;
    EditText name, special, ingr;
    TextView date;
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail_modify);

        saveBtn = findViewById(R.id.saveModifyBtn);
        name = findViewById(R.id.foodNameET);
        special = findViewById(R.id.specialTagET);
        ingr = findViewById(R.id.ingrET);
        date = findViewById(R.id.DateTV);

        Intent intent = getIntent();

        String iname = intent.getStringExtra("menuName");
        //String ikey = intent.getStringExtra("menuKey");
        String idate = intent.getStringExtra("menuDate");
        String iIngr = intent.getStringExtra("menuIngr");
        String iSpec = intent.getStringExtra("menuSpec");
        String grpKey = intent.getStringExtra("grpKey");
        String username = intent.getStringExtra("usrname");
        String owner = intent.getStringExtra("owner");
        String menuName = intent.getStringExtra("mnName");

        name.setText(iname);
        special.setText(iSpec);
        ingr.setText(iIngr);
        date.setText(idate);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(grpKey != null){
                    menuRef = FirebaseDatabase.getInstance().getReference("Groups").child(grpKey).child("group_menus").child(menuName).child(idate);

                    String updatedName = name.getText().toString();
                    String updatedSpecial = special.getText().toString();
                    String updatedIngredients = ingr.getText().toString();

                    menuRef.child("menuName").setValue(updatedName);
                    menuRef.child("menuSpecial").setValue(updatedSpecial);
                    menuRef.child("menuIngredients").setValue(updatedIngredients);
                    Intent intent = new Intent(MenuDetailModifyActivity.this, GroupAdminActivity.class);
                    intent.putExtra("groupKey", grpKey);
                    intent.putExtra("username", username);
                    intent.putExtra("groupOwner", owner);
                    startActivity(intent);
                    finish();
                }else{
                    menuRef = FirebaseDatabase.getInstance().getReference("Menus").child(username).child(menuName).child(idate);

                    String updatedName = name.getText().toString();
                    String updatedSpecial = special.getText().toString();
                    String updatedIngredients = ingr.getText().toString();

                    menuRef.child("menuName").setValue(updatedName);
                    menuRef.child("menuSpecial").setValue(updatedSpecial);
                    menuRef.child("menuIngredients").setValue(updatedIngredients);

                    Intent intent = new Intent(MenuDetailModifyActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}