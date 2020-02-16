package com.marcosboger.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {

    RatingBar ratingBar;
    Button rateItButton;
    private String recipeNumber;
    private int recipeNumberReviews;
    private float recipeReviewAvg;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        recipeNumber = getIntent().getStringExtra("recipe_number");
        recipeReviewAvg = getIntent().getFloatExtra("reviews_avg", 0);
        recipeNumberReviews = getIntent().getIntExtra("number_reviews", 0);
        rateItButton = findViewById(R.id.rate_it_button);
        ratingBar = findViewById(R.id.rating_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeNumber);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateItButton.setEnabled(true);
            }
        });

    }

    public void onSkipItClicked(View view){
        Intent intent = new Intent(ReviewActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void onRateItClicked(View view){
        mDatabase.child("reviews_number").setValue(recipeNumberReviews + 1);
        float avg = recipeNumberReviews * recipeReviewAvg;
        avg = avg + ratingBar.getRating();
        avg = avg/(recipeNumberReviews + 1);
        mDatabase.child("review_avg").setValue(avg);
        Intent intent = new Intent(ReviewActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }
}
