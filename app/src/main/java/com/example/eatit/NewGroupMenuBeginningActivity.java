package com.example.eatit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewGroupMenuBeginningActivity extends AppCompatActivity {

    String grpKey, username;
    EditText det, dat, dur;
    Button startBtn;

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
}