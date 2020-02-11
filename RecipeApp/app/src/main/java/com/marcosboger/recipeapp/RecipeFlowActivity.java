package com.marcosboger.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RecipeFlowActivity extends AppCompatActivity {

    private String recipeName;
    private int step = 1;
    private String recipeText, recipeImage, recipeVideo, recipeIsFinal;
    TextView text;
    Button lastStep, nextStep;
    ImageView image;
    VideoView video;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String vidAddress = "http://example.com/examplevideo.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_flow);
        recipeName = getIntent().getStringExtra("recipe_name");
        Log.d("RecipeFlowActivity", recipeName);
        lastStep = findViewById(R.id.last_step_button);
        lastStep.setEnabled(false);
        text = findViewById(R.id.recipe_text);
        nextStep = findViewById(R.id.next_step_button);
        image = findViewById(R.id.recipe_image);
        video = findViewById(R.id.recipe_video);
        Uri vidUri = Uri.parse(vidAddress);
        video.setVideoURI(vidUri);
        video.requestFocus();
        video.start();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeName).child("steps").child("1");

        ValueEventListener textListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    recipeText = dataSnapshot.child("text").getValue(String.class);
                    recipeImage = dataSnapshot.child("image").getValue(String.class);
                    recipeVideo = dataSnapshot.child("video").getValue(String.class);
                    recipeIsFinal = dataSnapshot.child("final").getValue(String.class);
                    text.setText(recipeText);
                    if(!recipeImage.equals("none"))
                        new RetrieveFeedTask().execute(recipeImage);
                    else
                        image.setImageBitmap(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);
    }

    public void onNextStepClicked(View view){
        nextStep.setEnabled(false);
        if(recipeIsFinal.equals("true"))
            return;
        if(step == 1)
            lastStep.setEnabled(true);
        step++;
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeName).child("steps").child(Integer.toString(step));

        ValueEventListener textListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeText = dataSnapshot.child("text").getValue(String.class);
                recipeImage = dataSnapshot.child("image").getValue(String.class);
                recipeVideo = dataSnapshot.child("video").getValue(String.class);
                recipeIsFinal = dataSnapshot.child("final").getValue(String.class);
                text.setText(recipeText);
                if(recipeIsFinal != null)
                    if(recipeIsFinal.equals("true"))
                        return;
                if(!recipeImage.equals("none"))
                    new RetrieveFeedTask().execute(recipeImage);
                else
                    image.setImageBitmap(null);
                nextStep.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);
    }

    public void onLastStepClicked(View view){
        if(step == 2)
           lastStep.setEnabled(false);
        step--;
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeName).child("steps").child(Integer.toString(step));

        ValueEventListener textListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeText = dataSnapshot.child("text").getValue(String.class);
                recipeImage = dataSnapshot.child("image").getValue(String.class);
                recipeVideo = dataSnapshot.child("video").getValue(String.class);
                recipeIsFinal = dataSnapshot.child("final").getValue(String.class);
                text.setText(recipeText);
                if (!nextStep.isEnabled())
                    nextStep.setEnabled(true);
                if(!recipeImage.equals("none"))
                    new RetrieveFeedTask().execute(recipeImage);
                else
                    image.setImageBitmap(null);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);
    }

    class RetrieveFeedTask extends AsyncTask<String, String, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... recipeImage) {
            URL url = null;
            Bitmap bmp = null;
            try {
                url = new URL(recipeImage[0]);
            } catch (java.net.MalformedURLException E) {
                Log.d("RecipeFlowActivity", "Deu merda no URL");
            }

            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (java.io.IOException E) {
                Log.d("RecipeFlowActivity", "Deu merda no bitmap");
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap bmp) {
            image.setImageBitmap(bmp);
        }
    }
}
