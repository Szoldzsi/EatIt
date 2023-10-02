package com.example.eatit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewMenuFragment extends Fragment {

    EditText det;
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
        startBtn = view.findViewById(R.id.startBtn);
        tw = view.findViewById(R.id.textView11);

        tw.setText(username);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String intStr = det.getText().toString();

                Intent intent = new Intent(getContext(), NewMenuActivity.class);

                intent.putExtra("username",tw.getText().toString());
                intent.putExtra("name", intStr);
                startActivity(intent);
            }
        });

        return view;
    }


    public void setUsername(String username){
        this.username = username;
    }
}