package com.example.eatit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListGroupsFragment extends Fragment {

    private static final String KEY = "username";
    String username;
    private DatabaseReference groupsRef;
    private ListView listView, listViewMember;
    private ArrayAdapter<String> adapter, adapter2;
    private List<GroupData> groupDataList;
    private Map<String, GroupData> groupNameToGroupDataMap = new HashMap<>();
    public ListGroupsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            username = savedInstanceState.getString(KEY);
        }
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupDataList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_groups, container, false);

        listView = view.findViewById(R.id.groupsListView);
        listViewMember = view.findViewById(R.id.groupsListViewMember);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        adapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        listViewMember.setAdapter(adapter2);

        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupDataList.clear();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    String groupName = groupSnapshot.child("group_name").getValue(String.class);
                    String groupOwner = groupSnapshot.child("owner").getValue(String.class);

                    GroupData groupData = new GroupData(groupName, groupSnapshot.getKey(), groupOwner);

                    if (isCurrentUserOwner(groupSnapshot, username)) {
                        groupDataList.add(0, groupData); // Add to the beginning of the list (owned groups)
                    } else if (groupSnapshot.child("members").hasChild(username)
                            && groupSnapshot.child("members").child(username).getValue(Boolean.class)) {
                        groupDataList.add(groupData); // Add to the end of the list (member groups)
                    }
                }

                adapter.clear();

                for (GroupData group : groupDataList) {
                    adapter.add(group.getGroupName());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupData selectedGroup = groupDataList.get(position);
                if (selectedGroup != null) {
                    Intent intent = new Intent(requireContext(), GroupAdminActivity.class);
                    intent.putExtra("groupKey", selectedGroup.getGroupKey());
                    intent.putExtra("username", username);
                    intent.putExtra("groupOwner", selectedGroup.getGroupOwner());
                    startActivity(intent);
                }
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupData selectedGroup = groupDataList.get(position);
                if (selectedGroup != null) {
                    // Check if the current user is the owner of the group
                    String currentUsername = username; // Implement this to get the current user's username
                    Log.d("Group_teszt", selectedGroup.getGroupName());

                    if (username.equals(selectedGroup.getGroupOwner())) {
                        // Current user is the owner, start GroupAdminActivity
                        Intent intent = new Intent(requireContext(), GroupAdminActivity.class);
                        intent.putExtra("groupKey", selectedGroup.getGroupKey());
                        intent.putExtra("username", username);
                        intent.putExtra("groupOwner", selectedGroup.getGroupOwner());
                        startActivity(intent);
                    } else {
                        // Current user is not the owner, start GroupUserActivity
                        Intent intent = new Intent(requireContext(), GroupUserActivity.class);
                        intent.putExtra("groupKey", selectedGroup.getGroupKey());
                        intent.putExtra("username", username);
                        intent.putExtra("groupOwner", selectedGroup.getGroupOwner());
                        startActivity(intent);
                    }
                }
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
    private String groupOwner;

    public GroupData(String groupName, String groupKey, String groupOwner) {
        this.groupName = groupName;
        this.groupKey = groupKey;
        this.groupOwner = groupOwner;
    }

    public String getGroupOwner() {
        return groupOwner;
    }
    public String getGroupName() {
        return groupName;
    }

    public String getGroupKey() {
        return groupKey;
    }
}