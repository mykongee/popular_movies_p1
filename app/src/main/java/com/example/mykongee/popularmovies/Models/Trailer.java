package com.example.mykongee.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mykongee on 9/11/15.
 */
public class Trailer implements Parcelable {

    public String name;
    public String source;

    public Trailer() {
    }

    private Trailer(Parcel in) {
        this.name = in.readString();
        this.source = in.readString();
    }

    public static Trailer fromJsonObject(JSONObject jsonObject) {
        Trailer trailer = new Trailer();

        try {
            trailer.name = jsonObject.getString("name");
            trailer.source = jsonObject.getString("source");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trailer;
    }

    public static ArrayList<Trailer> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Trailer> trailerArrayList = new ArrayList<Trailer>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject trailerJson = null;
            try {
                trailerJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Trailer trailer = fromJsonObject(trailerJson);
            if (trailer != null) {
                trailerArrayList.add(trailer);
            }
        }

        return trailerArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(source);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }
}
