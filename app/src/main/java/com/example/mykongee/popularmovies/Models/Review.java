package com.example.mykongee.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mykongee on 9/11/15.
 */
public class Review implements Parcelable {

    public String id;
    public String content;
    public String author;
    public String url;

    public Review() {

    }

    private Review(Parcel in) {
        this.id = in.readString();
        this.content = in.readString();
        this.author = in.readString();
        this.url = in.readString();
    }

    public static Review fromJsonObject(JSONObject jsonObject) {
        Review review = new Review();

        try {
            review.id = jsonObject.getString("id");
            review.content = jsonObject.getString("content");
            review.author = jsonObject.getString("author");
            review.id = jsonObject.getString("url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return review;
    }

    public static ArrayList<Review> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Review> reviewArrayList = new ArrayList<Review>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject reviewJson = null;
            try {
                reviewJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Review review = fromJsonObject(reviewJson);
            if (review != null) {
                reviewArrayList.add(review);
            }
        }
        return reviewArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(content);
        parcel.writeString(author);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

}
