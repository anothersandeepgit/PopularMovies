package com.end2end.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lendevsanadmin on 8/22/2015.
 */
public class MovieNode implements Parcelable {
    String id;
    String title;
    String summary;
    String releaseDate;
    String posterPath;
    String voteAverage;

    public MovieNode(String id, String title, String summary, String releaseDate, String posterPath, String voteAverage) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
    }

    public MovieNode (Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.id = data[0];
        this.title = data[1];
        this.summary = data[2];
        this.releaseDate = data[3];
        this.posterPath = data[4];
        this.voteAverage = data[5];
    }
    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.id, this.title, this.summary,
                                            this.releaseDate, this.posterPath, this.voteAverage});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public MovieNode createFromParcel(Parcel in) {
            return new MovieNode(in);
        }
        public MovieNode[] newArray(int size) {
            return new MovieNode[size];
        }
    };

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
}