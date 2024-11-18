package com.example.assignment2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> moviesList;
    private OkHttpClient client;

    private static final String API_KEY = "585e2784";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);

        moviesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, moviesList);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMovies.setAdapter(movieAdapter);

        client = new OkHttpClient();

        buttonSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovies(query);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a movie title", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchMovies(String query) {
        //If s not given at the end, I will get "Incorrect IMDb ID."
        String url = "https://www.omdbapi.com/?apikey=" + API_KEY + "&s=" + query;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Got IO exception: " + e, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String responseStatus = jsonResponse.getString("Response");
                        if (responseStatus.equals("True")) {
                            JSONArray jsonArray = jsonResponse.getJSONArray("Search");
                            moviesList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject movieObject = jsonArray.getJSONObject(i);

                                String title = movieObject.getString("Title");
                                String year = movieObject.getString("Year");
                                String imdbID = movieObject.getString("imdbID");
                                String type = movieObject.getString("Type");
                                String poster = movieObject.getString("Poster");

                                moviesList.add(new Movie(title, year, imdbID, type, poster));
                            }

                            runOnUiThread(() -> movieAdapter.notifyDataSetChanged());

                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "No results found responseStatus returned false", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {

                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Error parsing data " + e, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error responde returned false", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
