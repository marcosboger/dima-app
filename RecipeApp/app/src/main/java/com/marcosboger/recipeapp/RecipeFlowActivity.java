package com.marcosboger.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

    private Bitmap playButtonBitmap;
    private boolean paused = true;
    private int numberSteps = -1;
    private int j = 0;
    private Bitmap[] bitmapArray = new Bitmap[20];
    private boolean bitmapFinished = false;
    private int step = 0;
    private String[] recipeText = new String[20];
    private String[] recipeImage = new String[20];
    private String[] recipeVideo = new String[20];
    private String[] recipeIsFinal = new String[20];
    TextView text;
    Button lastStepButton, nextStepButton, startButton;
    ImageView image;
    VideoView video;
    DataSnapshot steps;
    private String recipeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseReference mDatabase;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_flow);
        recipeNumber = getIntent().getStringExtra("recipe_number");
        lastStepButton = findViewById(R.id.last_step_button);
        text = findViewById(R.id.recipe_text);
        nextStepButton = findViewById(R.id.next_step_button);
        startButton = findViewById(R.id.start_video_button);
        image = findViewById(R.id.recipe_image);
        video = findViewById(R.id.recipe_video);
        lastStepButton.setEnabled(false);
        nextStepButton.setEnabled(false);
        playButtonBitmap = getBitmap(getResources().getDrawable(R.drawable.ic_play_arrow_24px));

        mDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeNumber).child("steps");

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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(textListener);

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                startButton.setEnabled(true);
                startButton.setText(getString(R.string.start_video));
            }
        });
    }

    public void onStartVideoClicked(View view){
        Log.d("RecipeFlowActivity",Boolean.toString(paused));
        if(paused){
            image.setImageBitmap(null);
            video.start();
            paused = false;
        }
        else{
            video.pause();
            image.setImageBitmap(playButtonBitmap);
            paused = true;
        }
    }

    public void onNextStepClicked(View view){
        if(step == 0)
            lastStepButton.setEnabled(true);
        step++;
        paused = true;
        text.setText(recipeText[step]);
        image.setImageBitmap(bitmapArray[step]);
        if(recipeVideo[step].equals("none")){
            video.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
        }
        else{
            startButton.setEnabled(false);
            startButton.setText(getString(R.string.loading_video));
            startButton.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse(recipeVideo[step]));
            video.setVisibility(View.VISIBLE);
            video.pause();
        }
        if(recipeIsFinal[step].equals("true"))
            nextStepButton.setEnabled(false);
    }

    public void onLastStepClicked(View view){
        if(recipeIsFinal[step].equals("true"))
            nextStepButton.setEnabled(true);
        step--;
        paused = true;
        text.setText(recipeText[step]);
        image.setImageBitmap(bitmapArray[step]);
        if(recipeVideo[step].equals("none")){
            video.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
        }
        else{
            startButton.setEnabled(false);
            startButton.setText(getString(R.string.loading_video));
            startButton.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse(recipeVideo[step]));
            video.setVisibility(View.VISIBLE);
            video.pause();
        }
        if(step == 0)
            lastStepButton.setEnabled(false);
    }

    private Bitmap getBitmap(Drawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(200,
                200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    class RetrieveFeedTask extends AsyncTask<String, String, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... recipeImage) {
            if (recipeImage[0].equals("none"))
                return playButtonBitmap;
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
                nextStepButton.setEnabled(true);
                image.setImageBitmap(bitmapArray[0]);
                text.setText(recipeText[0]);
                if(recipeVideo[0].equals("none")){
                    video.setVisibility(View.GONE);
                    startButton.setVisibility(View.GONE);
                }
                else
                    video.setVideoURI(Uri.parse(recipeVideo[0]));
            }
        }
    }
}
