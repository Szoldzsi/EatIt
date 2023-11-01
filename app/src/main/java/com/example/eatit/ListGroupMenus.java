package com.example.eatit;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

public class ListGroupMenus extends AppCompatActivity {

    String grpKey, username;
    ListView groupMenusList;

    private DatabaseReference groupsRef;
    private ArrayAdapter<String> adapter;
    private List<String> menuNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_menus);

        Intent intent = getIntent();
        grpKey = intent.getStringExtra("groupKey");
        username = intent.getStringExtra("username");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        groupMenusList = findViewById(R.id.groupMenusList);

        menuNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuNames);
        groupMenusList.setAdapter(adapter);



        fetchAndDisplayMenuNames();

        groupMenusList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMenuName = menuNames.get(position);

                Intent intent = new Intent(ListGroupMenus.this, ListGroupMenuDetails.class);
                intent.putExtra("groupKey", grpKey);
                intent.putExtra("username", username);
                intent.putExtra("menuName", selectedMenuName);
                startActivity(intent);
            }
        });

        /*groupMenusList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                String selectedMember = adapter.getItem(info.position);
                menu.setHeaderTitle("Törlés " + selectedMember);
                menu.add(android.view.Menu.NONE, 1, Menu.NONE, "Menü törlése");
            }
        });*/
        groupsRef.child(grpKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String groupOwner = dataSnapshot.child("owner").getValue(String.class);

                    if (username.equals(groupOwner)) {
                        groupMenusList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                                String selectedMember = adapter.getItem(info.position);
                                menu.setHeaderTitle("Törlés " + selectedMember);
                                menu.add(android.view.Menu.NONE, 1, Menu.NONE, "Menü törlése");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    private void fetchAndDisplayMenuNames() {
        groupsRef.child(grpKey).child("group_menus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupMenusSnapshot) {
                for (DataSnapshot menuSnapshot : groupMenusSnapshot.getChildren()) {
                    String menuName = menuSnapshot.getKey();
                    if (menuName != null) {
                        menuNames.add(menuName);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        String selectedMenuName = menuNames.get(position);

        switch (item.getItemId()) {
            case 1:
                deleteMenu(selectedMenuName);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteMenu(String menuName) {
        groupsRef.child(grpKey).child("group_menus").child(menuName).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ListGroupMenus.this, menuName + " törölve a csoportos menük közül", Toast.LENGTH_SHORT).show();

                        int position = menuNames.indexOf(menuName);
                        if (position >= 0) {
                            menuNames.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ListGroupMenus.this, "Nem sikerült törölni a " + menuName + " nevű menüt", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}