package com.example.eatit;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    TextView tw;
    Button logout, newRecipe, listRecipe;
    DatabaseReference reference;
    FirebaseDatabase db;
     String username;
     ProgressBar progressBar;
     View loadingView;
     FragmentTransaction transaction;
     BottomNavigationView bottomNavigationView;
     private static final String SELECTED_KEY = "selectedItemId";
     private int selectedItemId;

    ArrayList<String> recipeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tw = findViewById(R.id.textView3);
        logout = findViewById(R.id.logoutBtn);
        newRecipe = findViewById(R.id.recipeBtn);
        listRecipe = findViewById(R.id.recipeListBtn);
        recipeArrayList = new ArrayList<String>();
        progressBar = findViewById(R.id.progressBar);
        loadingView = findViewById(R.id.loading_view);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        loadingView.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        newRecipe.setVisibility(View.GONE);
        listRecipe.setVisibility(View.GONE);
        logout.setActivated(false);
        newRecipe.setActivated(false);
        listRecipe.setActivated(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = FirebaseDatabase.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    username = String.valueOf(dataSnapshot.getValue());
                    tw.setText("Eat It!");
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.setIndeterminate(false);
                    loadingView.setVisibility(View.GONE);
                    if (savedInstanceState != null) {
                        switch (selectedItemId){
                            case R.id.navigation_list:
                                MenuListFragment recipeListFragment = new MenuListFragment();
                                recipeListFragment.setUsername(username);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recipeListFragment).commit();
                            case R.id.navigation_nrec:
                                NewMenuFragment recipeFragment = new NewMenuFragment();
                                recipeFragment.setUsername(username);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recipeFragment).commit();
                        }
                    }else{
                        MenuListFragment recipeListFragment = new MenuListFragment();
                        recipeListFragment.setUsername(username);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recipeListFragment).commit();
                    }


                }
                else {
                    Toast.makeText(MainActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectedItemId = item.getItemId();
                        switch (selectedItemId) {
                            case R.id.navigation_list:
                                transaction = getSupportFragmentManager().beginTransaction();
                                MenuListFragment recipeListFragment = new MenuListFragment();
                                transaction.replace(R.id.fragment_container, recipeListFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                recipeListFragment.setUsername(username);

                                return true;
                            case R.id.navigation_nrec:
                                transaction = getSupportFragmentManager().beginTransaction();
                                NewMenuFragment recipeFragment = new NewMenuFragment();
                                transaction.replace(R.id.fragment_container, recipeFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                recipeFragment.setUsername(username);
                                return true;
                            case R.id.navigation_logout:
//                                FirebaseAuth.getInstance().signOut();
//                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivity(intent);
//                                finish();
                                transaction = getSupportFragmentManager().beginTransaction();
                                OthersFragment othersFragment =  new OthersFragment();
                                transaction.replace(R.id.fragment_container, othersFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                othersFragment.setUsername(username);
                                return true;
                        }
                        return true;
                    }
                });

        if (savedInstanceState != null) {
            selectedItemId = savedInstanceState.getInt(SELECTED_KEY);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }

    }
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_KEY, selectedItemId);
    }
}