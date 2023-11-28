package com.example.eatit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class GroupAdminActivity extends AppCompatActivity {

    Button listMembers, inviteMembers, groupMenu, listGrpMenus, deleteGroup;
    String groupKey, groupOwner, username, groupName;
    TextView groupNameTV;
    EditText usernameInput;
    private DatabaseReference groupsRef;
    private DatabaseReference usersRef;
    private AlertDialog inviteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_admin);

        Intent intent = getIntent();
        groupKey = intent.getStringExtra("groupKey");
        groupOwner = intent.getStringExtra("groupOwner");
        username = intent.getStringExtra("username");

        listMembers = findViewById(R.id.listGrpMembers);
        inviteMembers = findViewById(R.id.inviteGrpBtn);
        groupMenu = findViewById(R.id.createGrpMenuBtn);
        listGrpMenus = findViewById(R.id.listGroupMenuBtn);
        deleteGroup = findViewById(R.id.removeGroupBtn);
        groupNameTV = findViewById(R.id.groupNameTV);
        usernameInput = new EditText(this);

        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        groupsRef.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
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

        inviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInviteDialog();
            }
        });

        listMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GroupAdminActivity.this, ListGroupMembersActivity.class);
                intent1.putExtra("groupKey", groupKey);
                intent1.putExtra("username", username);
                intent1.putExtra("groupOwner", groupOwner);
                startActivity(intent1);
            }
        });
        groupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GroupAdminActivity.this, NewGroupMenuBeginningActivity.class);
                intent1.putExtra("groupKey", groupKey);
                intent1.putExtra("username", username);
                intent1.putExtra("groupOwner", groupOwner);
                startActivity(intent1);
                finish();
            }
        });
        listGrpMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GroupAdminActivity.this, ListGroupMenus.class);
                intent1.putExtra("groupKey", groupKey);
                intent1.putExtra("username", username);
                intent1.putExtra("owner", groupOwner);
                startActivity(intent1);
            }
        });
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the group from the database
                groupsRef.child(groupKey).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(GroupAdminActivity.this, "Csoport sikeresen törölve.", Toast.LENGTH_SHORT).show();

                            Intent mainIntent = new Intent(GroupAdminActivity.this, MainActivity.class);
                            mainIntent.putExtra("username", username);
                            startActivity(mainIntent);
                        } else {
                            Log.e("DatabaseError", databaseError.toString());
                            Toast.makeText(GroupAdminActivity.this, "Csoport törlése sikertelen.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(GroupAdminActivity.this, MainActivity.class);
        mainIntent.putExtra("username", username);
        startActivity(mainIntent);
        finish(); // Optional: Call finish to close the current activity if you don't want to keep it in the back stack
    }

    private void showInviteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Csoporttag meghívása");
        builder.setMessage("Meghívandó felhasználónév megadása:");

        final EditText inputField = new EditText(this);
        builder.setView(inputField);

        builder.setPositiveButton("Meghívás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String invitedUsername = inputField.getText().toString().trim();
                if (!TextUtils.isEmpty(invitedUsername)) {
                    checkIfUsernameExists(invitedUsername, groupName);
                } else {
                    Toast.makeText(GroupAdminActivity.this, "Felhasználó mező nem lehet üres", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        inviteDialog = builder.create();
        inviteDialog.show();
    }

    private void checkIfUsernameExists(final String invitedUsername, final String groupName) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean usernameExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String storedUsername = userSnapshot.getValue(String.class);
                    if (storedUsername != null && storedUsername.equals(invitedUsername)) {
                        // Username exists
                        usernameExists = true;
                        break;
                    }
                }

                if (usernameExists) {
                    groupsRef.child(groupKey).child("members").child(invitedUsername).setValue(false);


                    Toast.makeText(GroupAdminActivity.this, "Meghívó elküldve: " + invitedUsername + " felhasználónak", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupAdminActivity.this, "Ilyen felhasználó nem létezik", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
                Toast.makeText(GroupAdminActivity.this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            }
        });
    }

/*    private void sendNotification(String invitedUsername, String groupName) {
        DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("Tokens");
        tokensRef.child(invitedUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fcmToken = dataSnapshot.getValue(String.class);
                if (fcmToken != null) {
                    String notificationTitle = "Meghívó a " + groupName + " csoportba";
                    String notificationBody = "Meghívót kaptál, hogy csatlakozz a " + groupName + " nevű csoportba.";

                    sendFCMNotification(fcmToken, notificationTitle, notificationBody);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
            }
        });
    }

    private void sendFCMNotification(String fcmToken, String title, String message) {
        RemoteMessage notification = new RemoteMessage.Builder(fcmToken + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(0))
                .addData("title", title)
                .addData("body", message)
                .build();

        FirebaseMessaging.getInstance().send(notification);
    }*/


}
