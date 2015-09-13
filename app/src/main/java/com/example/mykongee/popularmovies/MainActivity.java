package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mykongee.popularmovies.Models.Movie;
import com.example.mykongee.popularmovies.MovieDetailActivity.MovieFragment;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_container, new MovieFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
                Log.v(LOG_TAG, "commited");
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MovieFragment mf = (MovieFragment) getSupportFragmentManager()
                .findFragmentById(R.id.movie_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            // If there are two panes, call the SupportFragmentManager
            // to set a new fragment, with the given movie data,
            // onto the second pane
            Bundle extras = new Bundle();
            extras.putParcelable("MOVIE", movie);

            MovieFragment fragment = new MovieFragment();
            fragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_container, fragment, MOVIEFRAGMENT_TAG)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onItemSelected MOVIE");
            Intent intent = new Intent(this, MovieDetailActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("MOVIE", movie);

            intent.putExtras(extras);
            startActivity(intent);
        }

    }

    @Override
    public void onItemSelected(Bundle movieInfo) {
        String title = movieInfo.getString("TITLE");
        String overview = movieInfo.getString("OVERVIEW");
        String releaseDate = movieInfo.getString("RELEASEDATE");
        String posterPath = movieInfo.getString("POSTERPATH");
        String rating = movieInfo.getString("RATING");

        if (mTwoPane) {
            Bundle extras = new Bundle();
            extras.putString("OVERVIEW", overview);
            extras.putString("POSTERPATH", posterPath);
            extras.putString("RATING", rating);
            extras.putString("RELEASEDATE", releaseDate);
            extras.putString("TITLE", title);

            MovieFragment fragment = new MovieFragment();
            fragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_container, fragment, MOVIEFRAGMENT_TAG)
                    .commit();
        } else {
            Log.v(LOG_TAG, "onItemSelected BUNDLE");

            Intent intent = new Intent(this, MovieDetailActivity.class);
            Bundle extras = new Bundle();
            extras.putString("OVERVIEW", overview);
            extras.putString("POSTERPATH", posterPath);
            extras.putString("RATING", rating);
            extras.putString("RELEASEDATE", releaseDate);
            extras.putString("TITLE", title);

            intent.putExtras(extras);
            startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
