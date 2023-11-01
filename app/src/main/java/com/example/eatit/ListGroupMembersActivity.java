package com.example.eatit;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListGroupMembersActivity extends AppCompatActivity {

    ListView membersLV;
    String groupKey, username;
    private DatabaseReference groupsRef;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_members2);

        membersLV = findViewById(R.id.groupMembers);
        Intent intent = getIntent();

        groupKey = intent.getStringExtra("groupKey");
        username = intent.getStringExtra("username");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupKey);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        membersLV.setAdapter(adapter);

        membersLV.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                String selectedMember = adapter.getItem(info.position);
                menu.setHeaderTitle("Eltávolítás " + selectedMember);
                menu.add(Menu.NONE, 1, Menu.NONE, "Csoporttag eltávolítása");
            }
        });

        loadGroupMembers();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String selectedMember = adapter.getItem(info.position);

        switch (item.getItemId()) {
            case 1:
                removeMember(selectedMember);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void loadGroupMembers() {
        groupsRef.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    if (memberSnapshot.getValue(Boolean.class)) {
                        adapter.add(memberSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }

    private void removeMember(String memberName) {
        groupsRef.child("members").child(memberName).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ListGroupMembersActivity.this, memberName + " eltávolítva a csoportból", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ListGroupMembersActivity.this, "Nem sikerült eltávolítani a " + memberName + " nevű felhasználót", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
