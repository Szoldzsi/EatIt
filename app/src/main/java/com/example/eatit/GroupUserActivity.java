package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupUserActivity extends AppCompatActivity {
    Button listGrpMenusUser, exitGrpBtn;
    String groupKey, groupOwner, username, groupName;
    TextView groupNameTV;
    private DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_user);

        Intent intent = getIntent();
        groupKey = intent.getStringExtra("groupKey");
        groupOwner = intent.getStringExtra("groupOwner");
        username = intent.getStringExtra("username");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupKey);

        listGrpMenusUser = findViewById(R.id.listGrpMenusUser);
        exitGrpBtn = findViewById(R.id.exitGroupBtn);
        groupNameTV = findViewById(R.id.userGrpNameTV);

        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    groupName = dataSnapshot.child("group_name").getValue(String.class);
                    groupNameTV.setText(groupName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
            }
        });

        listGrpMenusUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GroupUserActivity.this, ListGroupMenus.class);
                intent1.putExtra("groupKey", groupKey);
                intent1.putExtra("username", username);
                startActivity(intent1);
            }
        });

        exitGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupsRef.child("members").child(username).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            // Group deleted successfully
                            Toast.makeText(GroupUserActivity.this, "Sikeresen kiléptél a csoportból.", Toast.LENGTH_SHORT).show();

                            // Navigate back to MainActivity
                            Intent mainIntent = new Intent(GroupUserActivity.this, MainActivity.class);
                            mainIntent.putExtra("username", username);
                            startActivity(mainIntent);
                        } else {
                            // Handle the error if group deletion fails
                            Log.e("DatabaseError", error.toString());
                            Toast.makeText(GroupUserActivity.this, "Hiba a kilépés során.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}