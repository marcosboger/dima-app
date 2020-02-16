package com.marcosboger.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.URL;


public class RecipeOverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "number";
    private static final String ARG_PARAM0 = "name";
    private static final String ARG_PARAM1 = "difficulty";
    private static final String ARG_PARAM2 = "time";
    private static final String ARG_PARAM3 = "serving";
    private static final String ARG_PARAM4 = "ingredients";
    private static final String ARG_PARAM5 = "image";
    private static final String ARG_PARAM6 = "numberReviews";
    private static final String ARG_PARAM7 = "reviewsAvg";
    // TODO: Rename and change types of parameters
    private String number;
    private String name;
    private String difficulty;
    private String time;
    private String serving;
    private String ingredients;
    private String image;
    private Integer numberReviews;
    private Float reviewsAvg;
    public RecipeOverviewFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeOverviewFragment newInstance(String param, String param0, String param1, String param2, String param3, String param4, String param5, Integer param6, Float param7) {
        RecipeOverviewFragment fragment = new RecipeOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        args.putString(ARG_PARAM0, param0);
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putInt(ARG_PARAM6, param6);
        args.putFloat(ARG_PARAM7, param7);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getString(ARG_PARAM);
            name = getArguments().getString(ARG_PARAM0);
            difficulty = getArguments().getString(ARG_PARAM1);
            time  = getArguments().getString(ARG_PARAM2);
            serving = getArguments().getString(ARG_PARAM3);
            ingredients = getArguments().getString(ARG_PARAM4);
            image = getArguments().getString(ARG_PARAM5);
            numberReviews = getArguments().getInt(ARG_PARAM6);
            reviewsAvg = getArguments().getFloat(ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_overview, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Inflate the layout for this fragment
        TextView recipe_name_text = getView().findViewById(R.id.recipe_name_overview);
        TextView difficulty_text = getView().findViewById(R.id.difficulty_text_overview);
        TextView time_text = getView().findViewById(R.id.time_text_overview);
        TextView serving_text = getView().findViewById(R.id.people_text_overview);
        TextView ingredients_text = getView().findViewById(R.id.recipe_text_overview);
        RatingBar ratingBar = getView().findViewById(R.id.ratingBar_overview);
        TextView numberReviews_text = getView().findViewById(R.id.number_reviews_text);

        recipe_name_text.setText(name);
        difficulty_text.setText(difficulty);
        time_text.setText(time);
        serving_text.setText(serving);
        ingredients_text.setText(ingredients);
        ratingBar.setRating(reviewsAvg);
        String numberReviewsText = numberReviews + " Reviews";
        numberReviews_text.setText(numberReviewsText);
        new RetrieveFeedTask().execute(image);
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
            ImageView recipe_image = getView().findViewById(R.id.recipe_image_overview);
            recipe_image.setImageBitmap(bmp);
        }
    }

}