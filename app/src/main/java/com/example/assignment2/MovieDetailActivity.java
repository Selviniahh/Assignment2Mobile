package com.example.assignment2;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imageViewPoster;
    private TextView textViewTitle, textViewYear, textViewRating, textViewStudio, textViewPlot;
    private OkHttpClient client;
    private static final String API_KEY = "585e2784";
    private TextView textViewGenre, textViewDirector, textViewActors, textViewRuntime, textViewLanguage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        imageViewPoster = findViewById(R.id.imageViewPosterDetail);
        textViewTitle = findViewById(R.id.textViewTitleDetail);
        textViewYear = findViewById(R.id.textViewYearDetail);
        textViewRating = findViewById(R.id.textViewRatingDetail);
        textViewStudio = findViewById(R.id.textViewStudioDetail);
        textViewPlot = findViewById(R.id.textViewPlotDetail);
        textViewGenre = findViewById(R.id.textViewGenreDetail);
        textViewDirector = findViewById(R.id.textViewDirectorDetail);
        textViewActors = findViewById(R.id.textViewActorsDetail);
        textViewRuntime = findViewById(R.id.textViewRuntimeDetail);
        textViewLanguage = findViewById(R.id.textViewLanguageDetail);

        client = new OkHttpClient();

        String imdbID = getIntent().getStringExtra("imdbID");

        if (imdbID != null) {
            getMovieDetails(imdbID);
        } else {
            Toast.makeText(this, "No movie ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getMovieDetails(String imdbID) {
        String url = "https://www.omdbapi.com/?apikey=" + API_KEY + "&i=" + imdbID + "&plot=full";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MovieDetailActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String responseStatus = jsonResponse.getString("Response");
                        if (responseStatus.equals("True")) {
                            String title = jsonResponse.getString("Title");
                            String year = jsonResponse.getString("Year");
                            String rating = jsonResponse.getString("Rated");
                            String studio = jsonResponse.getString("Production");
                            String plot = jsonResponse.getString("Plot");
                            String poster = jsonResponse.getString("Poster");

                            // New fields
                            String genre = jsonResponse.getString("Genre");
                            String director = jsonResponse.getString("Director");
                            String actors = jsonResponse.getString("Actors");
                            String runtime = jsonResponse.getString("Runtime");
                            String language = jsonResponse.getString("Language");

                            runOnUiThread(() -> {
                                textViewTitle.setText(title);
                                textViewYear.setText("Year: " + year);
                                textViewRating.setText("Rating: " + handleNAValue(rating));
                                textViewStudio.setText("Studio: " + handleNAValue(studio));
                                textViewPlot.setText(plot);

                                // New fields
                                textViewGenre.setText("Genre: " + handleNAValue(genre));
                                textViewDirector.setText("Director: " + handleNAValue(director));
                                textViewActors.setText("Actors: " + handleNAValue(actors));
                                textViewRuntime.setText("Runtime: " + handleNAValue(runtime));
                                textViewLanguage.setText("Language: " + handleNAValue(language));

                                // Load image
                                loadImageFromUrl(poster, imageViewPoster);
                            });

                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(MovieDetailActivity.this, "No details found", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(MovieDetailActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MovieDetailActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void loadImageFromUrl(String url, ImageView imageView) {
        // Use OkHttp to download the image and set it to the ImageView
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] imageBytes = response.body().bytes();
                    runOnUiThread(() -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageView.setImageBitmap(bitmap);
                    });
                }
            }
        });
    }

    //A helper function I just extracted. In case of any NA, I will instead return Not available, if it's available I will return the value I just got
    private String handleNAValue(String value) {
        if (value == null || value.equalsIgnoreCase("N/A")) {
            return "Not Available";
        } else {
            return value;
        }
    }

    public void onBackButtonClick(View view) {
        finish(); //close the activity and go back to the previous one 
    }
}
