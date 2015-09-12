package com.example.mykongee.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Movie implements Parcelable{

    public String id;
    public String movieTitle;
    public String moviePosterPath;
    public String movieReleaseDate;
    public Double movieRating;
    public String movieOverview;
    public ArrayList<Trailer> movieTrailers;
    public ArrayList<Review> movieReviews;

    public Movie() {
        movieTrailers = new ArrayList<Trailer>();
        movieReviews = new ArrayList<Review>();
    }

    private Movie(Parcel in){
        this();
        this.id = in.readString();
        this.movieTitle = in.readString();
        this.moviePosterPath = in.readString();
        this.movieReleaseDate = in.readString();
        this.movieRating = in.readDouble();
        this.movieOverview = in.readString();
        this.movieTrailers = in.readArrayList(Trailer.class.getClassLoader());
        this.movieReviews = in.readArrayList(Review.class.getClassLoader());
    }

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

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(id);
        parcel.writeString(movieTitle);
        parcel.writeString(moviePosterPath);
        parcel.writeString(movieReleaseDate);
        parcel.writeDouble(movieRating);
        parcel.writeString(movieOverview);
        parcel.writeList(movieTrailers);
        parcel.writeList(movieReviews);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel){
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };


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

    public void setMovieTrailers(ArrayList<Trailer> trailers) {
        movieTrailers = trailers;
    }

    public void setMovieReviews(ArrayList<Review> reviews) {
        movieReviews = reviews;
    }

    public ArrayList<Trailer> getMovieTrailers() {
        return movieTrailers;
    }

    public ArrayList<Review> getMovieReviews() {
        return movieReviews;
    }

}