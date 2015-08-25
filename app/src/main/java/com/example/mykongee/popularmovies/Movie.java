package com.example.mykongee.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Movie {

    public String id;
    public String movieTitle;
    public String moviePosterPath;
    public String movieReleaseDate;
    public Double movieRating;
    public String movieOverview;

    public static Movie fromJsonObject(JSONObject jsonObject) {
        Movie movie = new Movie();

        try {
            movie.id = jsonObject.getString("id");
            movie.movieTitle = jsonObject.getString("original_title");
            movie.movieOverview = jsonObject.getString("overview");
            movie.moviePosterPath = jsonObject.getString("poster_path");
            movie.movieReleaseDate = jsonObject.getString("release_date");
            movie.movieRating = jsonObject.getDouble("vote_average");

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return movie;
    }

    public static ArrayList<Movie> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Movie> movieList = new ArrayList<Movie>(jsonArray.length());
        //Process each result in json array, decode, and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieJson = null;
            try {
                movieJson = jsonArray.getJSONObject(i);

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Movie movie = Movie.fromJsonObject(movieJson);
            if (movie != null) {
                movieList.add(movie);
            }
        }
        return movieList;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return movieTitle;
    }

    public String getPosterPath() {
        return moviePosterPath;
    }

    public String getReleaseDate() {
        return movieReleaseDate;
    }

    public Double getRating() {
        return movieRating;
    }

    public String getOverview() {
        return movieOverview;
    }

}