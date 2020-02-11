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

import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RecipeFlowActivity extends AppCompatActivity {

    private static int numberSteps = -1;
    private static int j = 0;
    private static Bitmap[] bitmapArray = new Bitmap[20];
    private static boolean bitmapFinished = false;
    private int step = 0;
    private String[] recipeText = new String[20];
    private String[] recipeImage = new String[20];
    private String[] recipeVideo = new String[20];
    private String[] recipeIsFinal = new String[20];
    TextView text;
    Button lastStep, nextStep;
    ImageView image;
    VideoView video;
    DataSnapshot steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String recipeName;
        DatabaseReference mDatabase;
        String vidAddress = "http://example.com/examplevideo.mp4";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_flow);
        recipeName = getIntent().getStringExtra("recipe_name");
        lastStep = findViewById(R.id.last_step_button);
        text = findViewById(R.id.recipe_text);
        nextStep = findViewById(R.id.next_step_button);
        image = findViewById(R.id.recipe_image);
        video = findViewById(R.id.recipe_video);
        lastStep.setEnabled(false);
        nextStep.setEnabled(false);
        //Uri vidUri = Uri.parse(vidAddress);
        //video.setVideoURI(vidUri);
        //video.requestFocus();
        //video.start();

        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeName).child("steps");

        ValueEventListener textListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                steps = dataSnapshot;
                numberSteps = steps.child("steps_number").getValue(Integer.class);
                for(int i = 0; i < numberSteps; i++){
                    recipeText[i] = steps.child(Integer.toString(i)).child("text").getValue(String.class);
                    recipeImage[i] = steps.child(Integer.toString(i)).child("image").getValue(String.class);
                    recipeVideo[i] = steps.child(Integer.toString(i)).child("video").getValue(String.class);
                    recipeIsFinal[i] = steps.child(Integer.toString(i)).child("final").getValue(String.class);
                    new RetrieveFeedTask().execute(recipeImage[i]);
                    if(j == numberSteps){
                        nextStep.setEnabled(true);
                        image.setImageBitmap(bitmapArray[0]);
                        text.setText(recipeText[0]);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);
        if(j == numberSteps){
            nextStep.setEnabled(true);
            image.setImageBitmap(bitmapArray[0]);
            text.setText(recipeText[0]);
        }
    }

    public void onNextStepClicked(View view){
        if(step == 0)
            lastStep.setEnabled(true);
        step++;
        text.setText(recipeText[step]);
        image.setImageBitmap(bitmapArray[step]);
        if(recipeIsFinal[step].equals("true"))
            nextStep.setEnabled(false);
    }

    public void onLastStepClicked(View view){
        if(recipeIsFinal[step].equals("true"))
            nextStep.setEnabled(true);
        step--;
        text.setText(recipeText[step]);
        image.setImageBitmap(bitmapArray[step]);
        if(step == 0)
            lastStep.setEnabled(false);
    }


    class RetrieveFeedTask extends AsyncTask<String, String, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... recipeImage) {
            if (recipeImage[0].equals("none"))
                return null;
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
            bitmapArray[j] = bmp;
            j++;
            if(j == numberSteps){
                nextStep.setEnabled(true);
                image.setImageBitmap(bitmapArray[0]);
                text.setText(recipeText[0]);
            }
        }
    }
}
