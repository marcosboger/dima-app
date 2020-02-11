package com.marcosboger.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookActivity;
import com.facebook.ProfileManager;
import com.facebook.ProfileTracker;
import com.facebook.applinks.FacebookAppLinkResolver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FirebaseAuth mAuth;
    private String userName;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(RecipesFragment.newInstance("", ""));
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());

        ValueEventListener nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(nameListener);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_recipes:
                            openFragment(RecipesFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_favorites:
                            openFragment(FavoritesFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_account:
                            openFragment(AccountFragment.newInstance(userName));
                            return true;
                    }
                    return false;
                }
            };

    public void onLogOutClicked(View view){
        mAuth.signOut();
        Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onStartRecipeClicked(View view){
        Intent intent = new Intent(MainMenuActivity.this, RecipeFlowActivity.class);
        intent.putExtra("recipe_name", "brigadeiro");
        startActivity(intent);
    }


}
