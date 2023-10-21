package com.example.eatit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewMenuFragment extends Fragment {

    EditText det, dat, dur;
    Button startBtn;
    TextView tw;


    String username;
    public NewMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_menu, container, false);

        det = view.findViewById(R.id.napokTF);
        dat = view.findViewById(R.id.datumTF);
        dur = view.findViewById(R.id.idotartamTF);
        startBtn = view.findViewById(R.id.startBtn);
        tw = view.findViewById(R.id.textView11);

        tw.setText(username);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                try {
                    String intStr = det.getText().toString();
                    String datStr = dat.getText().toString();
                    int durat = Integer.parseInt(dur.getText().toString());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    Date startDate = dateFormat.parse(datStr);
                    Intent intent = new Intent(getContext(), NewMenuActivity.class);

                    intent.putExtra("username",tw.getText().toString());
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
                    // Add a dot after the year
                    input = input + ".";
                } else if (input.length() == 7) {
                    // Add a dot after the month
                    input = input + ".";
                }
                // Set the text to the formatted input
                dat.removeTextChangedListener(this);
                dat.setText(input);
                dat.setSelection(input.length());
                dat.addTextChangedListener(this);
            }
        });

        return view;
    }


    public void setUsername(String username){
        this.username = username;
    }
}