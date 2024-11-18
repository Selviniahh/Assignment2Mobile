package com.example.assignment2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> moviesList;
    private Context context;
    private OkHttpClient client;

    public MovieAdapter(Context context, List<Movie> moviesList) {
        this.context = context;
        this.moviesList = moviesList;
        client = new OkHttpClient();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPoster;
        public TextView textViewTitle, textViewYear, textViewType;

        public MovieViewHolder(View view) {
            super(view);
            imageViewPoster = view.findViewById(R.id.imageViewPoster);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewYear = view.findViewById(R.id.textViewYear);
            textViewType = view.findViewById(R.id.textViewType);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Movie selectedMovie = moviesList.get(position);

                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("imdbID", selectedMovie.getImdbID());
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_movie, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = moviesList.get(position);

        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewYear.setText("Year: " + movie.getYear());
        holder.textViewType.setText("Type: " + movie.getType());

        // Load poster image without Picasso
        loadImageFromUrl(movie.getPoster(), holder.imageViewPoster);
    }

    private void loadImageFromUrl(String url, ImageView imageView) {
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
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageView.setImageBitmap(bitmap);
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
