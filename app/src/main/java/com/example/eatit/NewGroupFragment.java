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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewGroupFragment extends Fragment {

    String username;
    EditText groupName;
    Button createBtn;

    private static final String KEY = "username";


    public NewGroupFragment() {
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
        View view = inflater.inflate(R.layout.fragment_new_group, container, false);

        groupName = view.findViewById(R.id.groupName);
        createBtn = view.findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String grpName = groupName.getText().toString();

                if (TextUtils.isEmpty(grpName)){
                    Toast.makeText(getActivity(), "Hiányzó csoport név!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a unique group ID (key) using push()
                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups").push();
                String groupID = groupRef.getKey();

                // Set the group name
                groupRef.child("group_name").setValue(grpName);

                // Set the owner of the group (in this case, it can be the current user)
                groupRef.child("owner").setValue(username);

                // Add the member (in this case, it can be the current user) to the members node
                /*groupRef.child("members").child(username).setValue(true);*/

                Toast.makeText(getActivity(), "Csoport sikeresen létrehozva!", Toast.LENGTH_SHORT).show();
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