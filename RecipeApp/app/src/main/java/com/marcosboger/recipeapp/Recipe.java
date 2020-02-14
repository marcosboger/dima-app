package com.marcosboger.recipeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Recipe {

    private static int recipeNumber = 0;
    final static String[] name = new String[20];
    final static String[] image = new String[20];

    public static void initialize() {
        return;
    }

    /**
     * An array of sample (dummy) items.
     */
    public static final List<RecipeItem> ITEMS = new ArrayList<RecipeItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, RecipeItem> ITEM_MAP = new HashMap<String, RecipeItem>();

    private static final int COUNT = 25;

    static {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        ValueEventListener nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i = 0; i < dataSnapshot.child("number_recipes").getValue(Integer.class); i++){
                    name[i] = dataSnapshot.child(Integer.toString(i)).child("name").getValue(String.class);
                    image[i] = dataSnapshot.child(Integer.toString(i)).child("image").getValue(String.class);
                    new Recipe.RetrieveFeedTask().execute(image[i]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Main Menu Activity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(nameListener);
    }

    private static void addItem(RecipeItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static RecipeItem createRecipeItem(int position, String name, Bitmap image) {
        return new RecipeItem(String.valueOf(position), image, name, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class RecipeItem {
        public final String id;
        public final Bitmap image;
        public final String content;
        public final String details;

        public RecipeItem(String id, Bitmap image, String content, String details) {
            this.id = id;
            this.image = image;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    static class RetrieveFeedTask extends AsyncTask<String, String, Bitmap> {

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
            addItem(createRecipeItem(recipeNumber ,name[recipeNumber], bmp));
            recipeNumber++;
        }
    }
}
