package com.example.eatit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewGroupMenuBeginningActivity extends AppCompatActivity {

    String grpKey, username;
    EditText det, dat, dur;
    Button startBtn;
    CheckBox existingCB;
    Spinner listExisting;
    Button copyExisting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_menu_beginning);

        Intent intent = getIntent();
        grpKey = intent.getStringExtra("groupKey");
        username = intent.getStringExtra("username");

        det = findViewById(R.id.napokgrpTF);
        dat = findViewById(R.id.datumgrpTF);
        dur = findViewById(R.id.idotartamgrpTF);
        startBtn = findViewById(R.id.startgrpBtn);
        existingCB = findViewById(R.id.existingCB);
        listExisting = findViewById(R.id.listExistingSpinner);
        copyExisting = findViewById(R.id.copyExistingBtn);

        listExisting.setVisibility(View.GONE);
        copyExisting.setVisibility(View.GONE);

        existingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listExisting.setVisibility(View.VISIBLE);
                    copyExisting.setVisibility(View.VISIBLE);
                    populateExistingMenus();
                } else {
                    listExisting.setVisibility(View.GONE);
                    copyExisting.setVisibility(View.GONE);
                }
            }
        });


        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                try {
                    String intStr = det.getText().toString();
                    String datStr = dat.getText().toString();
                    int durat = Integer.parseInt(dur.getText().toString());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    Date startDate = dateFormat.parse(datStr);
                    Intent intent = new Intent(NewGroupMenuBeginningActivity.this, NewMenuActivity.class);

                    intent.putExtra("username",username);
                    intent.putExtra("grpKey", grpKey);
                    intent.putExtra("name", intStr);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("duration", durat);

                    startActivity(intent);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


            }
        });

        copyExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMenu = listExisting.getSelectedItem().toString();
                copyMenuToGroup(selectedMenu);
            }
        });

        dat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (input.length() == 4) {
                    input = input + ".";
                } else if (input.length() == 7) {
                    input = input + ".";
                }
                dat.removeTextChangedListener(this);
                dat.setText(input);
                dat.setSelection(input.length());
                dat.addTextChangedListener(this);
            }
        });
    }
    private void populateExistingMenus() {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("Menus").child(username);

        menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> menuNames = new ArrayList<>();

                for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                    String menuName = menuSnapshot.getKey();
                    if (menuName != null) {
                        menuNames.add(menuName);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewGroupMenuBeginningActivity.this, android.R.layout.simple_spinner_item, menuNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listExisting.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void copyMenuToGroup(String selectedMenu) {
        DatabaseReference sourceRef = FirebaseDatabase.getInstance().getReference("Menus").child(username).child(selectedMenu);
        DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference("Groups").child(grpKey).child("group_menus").child(selectedMenu);

        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    destinationRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(NewGroupMenuBeginningActivity.this, "Menü sikeresen átmásolva", Toast.LENGTH_SHORT);
                            } else {
                                Toast.makeText(NewGroupMenuBeginningActivity.this, "Hiba! A választott menü nem került átmásolásra", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}