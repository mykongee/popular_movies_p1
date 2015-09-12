package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//         works with R.layout.activity_main being a fragment, but not as any other ViewGroup
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.linear_layout, new MainActivityFragment(), MOVIEFRAGMENT_TAG)
//                    // TODO problem with R.id.(something) ?
//                    .commit();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MainActivityFragment mf = (MainActivityFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.linear_layout);
//        Log.v(LOG_TAG, "got fragment by fragment tag");
//        Log.v(LOG_TAG, "onSortOrderChange() called");
//        if (mf != null) {
//            mf.onSortOrderChange();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
