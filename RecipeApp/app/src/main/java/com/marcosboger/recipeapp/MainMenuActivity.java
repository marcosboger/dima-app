package com.marcosboger.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenuActivity extends AppCompatActivity implements RecipeListFragment.OnListFragmentInteractionListener {

    @Override
    public void onListFragmentInteraction(Recipe.RecipeItem item) {
        recipeNumber = item.id;
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeNumber);

        ValueEventListener textListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                difficulty = dataSnapshot.child("difficulty").getValue(String.class);
                time = dataSnapshot.child("time").getValue(String.class);
                serving = dataSnapshot.child("serving").getValue(String.class);
                ingredients = dataSnapshot.child("ingredients").getValue(String.class);
                textImage =  dataSnapshot.child("image").getValue(String.class);
                numberReviews = dataSnapshot.child("reviews_number").getValue(Integer.class);
                reviewsAvg = dataSnapshot.child("review_avg").getValue(Float.class);
                openFragment(RecipeOverviewFragment.newInstance(recipeNumber, name, difficulty, time, serving, ingredients, textImage, numberReviews, reviewsAvg));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);
    }

    private BottomNavigationView bottomNavigation;
    private FirebaseAuth mAuth;
    private String userName;
    private DatabaseReference mDatabase;
    private String difficulty, time, serving, ingredients, textImage, name;
    private int numberReviews;
    private float reviewsAvg;
    private String recipeNumber;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(WelcomeFragment.newInstance("brigadeiro", "brigadeiro"));
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
                            openFragment(RecipeListFragment.newInstance(1));
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

    public void onGoBackToRecipesClicked(View view){
        openFragment(RecipeListFragment.newInstance(1));
    }

    public void onStartRecipeClicked(View view){
        Intent intent = new Intent(MainMenuActivity.this, RecipeFlowActivity.class);
        intent.putExtra("recipe_number", recipeNumber);
        intent.putExtra("number_reviews", numberReviews);
        intent.putExtra("reviews_avg", reviewsAvg);
        startActivity(intent);
    }

    public void onRecipeClicked(View view){
        recipeNumber = "1";
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeNumber);

        ValueEventListener textListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                difficulty = dataSnapshot.child("difficulty").getValue(String.class);
                time = dataSnapshot.child("time").getValue(String.class);
                serving = dataSnapshot.child("serving").getValue(String.class);
                ingredients = dataSnapshot.child("ingredients").getValue(String.class);
                textImage =  dataSnapshot.child("image").getValue(String.class);
                numberReviews = dataSnapshot.child("reviews_number").getValue(Integer.class);
                reviewsAvg = dataSnapshot.child("review_avg").getValue(Float.class);
                openFragment(RecipeOverviewFragment.newInstance(recipeNumber, name, difficulty, time, serving, ingredients, textImage, numberReviews, reviewsAvg));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);
    }
}
