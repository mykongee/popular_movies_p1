package com.example.mykongee.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Mykongee on 9/9/15.
 */
public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.sort_by_key),
                context.getString(R.string.default_sort_by_value));
    }
}
