package com.example.mykongee.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mykongee.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Mykongee on 9/5/15.
 */

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    public MovieAdapter movieAdapter;
    private Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    protected void getMovieDataFromJson(String movieJsonStr) throws JSONException {

        //Names of the JSON Objects we will extract from the url
        final String TMDB_RESULTS = "results";
        /* final String TMDB_TITLE = "original_title";
        final String TMDB_IMAGE = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_AVG_VOTE = "vote_average";*/


        // Get the JSON Array of the movies
        try {

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray(TMDB_RESULTS);
            Log.v(LOG_TAG, "Got the JSON Array");

            // Vector with information to be inserted into the database
            // Remember data must be inserted into SQLiteDatabase as ContentValues
            Vector<ContentValues> contentValuesVector = new Vector<>(movieJsonArray.length());

            // Make Movie models from JSON objects
            for (int i = 0; i < movieJsonArray.length(); i++) {
                // Get JsonObject, make Movie model
                JSONObject movieJsonObject = movieJsonArray.getJSONObject(i);
                Movie movieModel = Movie.fromJsonObject(movieJsonObject);

                // TODO Don't store the movies in a database, only the favorites,
                // TODO Do it like in P1

                ContentValues movieValues = new ContentValues();

                // Data that will populate the columns of a row in the database
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieModel.getId());
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieModel.getTitle());
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieModel.getOverview());
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                        movieModel.getPosterPath());
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieModel.getRating());
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                        movieModel.getReleaseDate());
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,
                        movieModel.getPopularity());


                // Add the ContentValues to the vector that will populate the database
                contentValuesVector.add(movieValues);

            }

            // Now that you have the vector of ContentValues, bulkInsert the data
            // into the database
            int inserted = 0;
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValuesArray);
                inserted = mContext.getContentResolver().bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValuesArray);
            }
            Log.d(LOG_TAG, "FetchMovieTask complete. " + inserted + "inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    @Override
    protected Void doInBackground(String... params) {
        //Declared outside try/catch block
        //In order to close these in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //String of the raw JSON response
        String movieJsonStr = null;

        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[APIKEY]

        try {
            final String API_KEY = "YOUR_API_KEY";
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";

            //Build an URI with given parameters
            Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                    appendQueryParameter(SORT_PARAM, params[0]).
                    appendQueryParameter(API_PARAM, API_KEY)
                    .build();

            //Build URL with Uri
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URL: " + url);

            //Create HttpUrlConnection and open the connection
            //Set request method to "GET"
            //Finally connect through the HttpURLConnection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Because we are now connected, we can read in data
            //Create inputstream from the HttpURLConnection
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                //Nothing to do
                return null;
            }

            //Chain InputStream to a BufferedReader
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            //Now create the data string with StringBuffer
            while ((line = reader.readLine()) != null) {
                // Adding a newline makes it easier to debug
                // Will not affect the parsing
                stringBuffer.append(line + "/n");
            }

            if (stringBuffer.length() == 0) {
                //Stream was empty, so nothing to parse
                return null;
            }

            movieJsonStr = stringBuffer.toString();
            //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[api_key]
            //parse json str to get data

            try {
                getMovieDataFromJson(movieJsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Fragment error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

}