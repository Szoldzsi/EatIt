package com.example.eatit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InvitesFragment extends Fragment {
    private static final String KEY = "username";
    String username;
    ListView invitesList;
    List<String> invites;
    ArrayAdapter<String> invitesAdapter;

    public InvitesFragment() {
        // Required empty public constructor
    }

    public static InvitesFragment newInstance(String param1, String param2) {
        InvitesFragment fragment = new InvitesFragment();
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_invites, container, false);

        invitesList = view.findViewById(R.id.invitesList);
        invites = new ArrayList<>();
        invitesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, invites);
        invitesList.setAdapter(invitesAdapter);
        registerForContextMenu(invitesList);

        checkForInvites();

        return view;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Choose an action");
        menu.add(0, 1, 0, "Elfogad");
        menu.add(0, 2, 0, "Elutasít");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String inviteMessage = invites.get(info.position);

        // Get the group name from the inviteMessage
        String groupName = inviteMessage.substring(inviteMessage.indexOf("a ") + 2, inviteMessage.indexOf(" csoportba"));

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        switch (item.getItemId()) {
            case 1: // Elfogad
                groupRef.orderByChild("group_name").equalTo(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot membersSnapshot = groupSnapshot.child("members");
                            membersSnapshot.child(username).getRef().setValue(true);
                        }
                        invites.remove(info.position);
                        invitesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
                break;

            case 2: // Elutasít
                groupRef.orderByChild("group_name").equalTo(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot membersSnapshot = groupSnapshot.child("members");
                            membersSnapshot.child(username).getRef().removeValue();
                        }
                        invites.remove(info.position);
                        invitesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
                break;
        }
        return true;
    }
    public void checkForInvites() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    // Get the group name using the groupKey
                    String groupKey = groupSnapshot.getKey();
                    if (groupKey != null && groupSnapshot.child("group_name").exists()) {
                        String groupName = groupSnapshot.child("group_name").getValue(String.class);

                        // Check for invites
                        if (groupName != null) {
                            DataSnapshot membersSnapshot = groupSnapshot.child("members");
                            for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                                String memberName = memberSnapshot.getKey();
                                Boolean isInvited = memberSnapshot.getValue(Boolean.class);
                                if (memberName != null && memberName.equals(username) && isInvited != null && !isInvited) {
                                    String inviteMessage = "Meghívó a " + groupName + " csoportba";
                                    invites.add(inviteMessage);
                                    invitesAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }
}