package com.example.eatit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ListGroupsFragment extends Fragment {

    private static final String KEY = "username";
    String username;
    private DatabaseReference groupsRef;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    public ListGroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            username = savedInstanceState.getString(KEY);
        }
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_groups, container, false);

        listView = view.findViewById(R.id.groupsListView);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                List<GroupData> ownedGroups = new ArrayList<>();
                List<GroupData> memberGroups = new ArrayList<>();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    String groupName = groupSnapshot.child("group_name").getValue(String.class);

                    // Check if the current user is the owner of the group
                    if (isCurrentUserOwner(groupSnapshot, username)) {
                        ownedGroups.add(new GroupData(groupName, groupSnapshot.getKey()));
                    }

                    // Check if the current user is a member of the group
                    if (groupSnapshot.child("members").hasChild(username)) {
                        memberGroups.add(new GroupData(groupName, groupSnapshot.getKey()));
                    }
                }

                // Display the owned groups first
                for (GroupData group : ownedGroups) {
                    adapter.add(group.getGroupName());
                }

                // Then display the member groups
                for (GroupData group : memberGroups) {
                    adapter.add(group.getGroupName());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
            }
        });

        return view;
    }

    private boolean isCurrentUserOwner(DataSnapshot groupSnapshot, String currentUsername) {
        String owner = groupSnapshot.child("owner").getValue(String.class);
        return owner != null && owner.equals(currentUsername);
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY, username);
    }
}

class GroupData {
    private String groupName;
    private String groupKey;

    public GroupData(String groupName, String groupKey) {
        this.groupName = groupName;
        this.groupKey = groupKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupKey() {
        return groupKey;
    }
}