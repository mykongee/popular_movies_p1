package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Log.v("HEY LOOK HERE", "set content view with layout");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MovieFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MovieFragment extends Fragment {

        public MovieFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            ((TextView) rootView.findViewById(R.id.title)).setText(extras.getString("TITLE"));
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w500/" + extras.getString("POSTER"))
                    .into((ImageView) rootView.findViewById(R.id.poster));
            ((TextView) rootView.findViewById(R.id.overview)).setText("   " +
                    extras.getString("OVERVIEW"));
            ((TextView) rootView.findViewById(R.id.rating))
                    .setText("Rating: " + extras.getString("RATING") + "/10");
            ((TextView) rootView.findViewById(R.id.release_date))
                    .setText(extras.getString("RELEASE_DATE"));

            return rootView;
        }
    }
}

