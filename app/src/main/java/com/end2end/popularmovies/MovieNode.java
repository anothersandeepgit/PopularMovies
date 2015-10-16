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

    protected MovieNode(Parcel in) {
        id = in.readString();
        title = in.readString();
        summary = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(voteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieNode> CREATOR = new Creator<MovieNode>() {
        @Override
        public MovieNode createFromParcel(Parcel in) {
            return new MovieNode(in);
        }

        @Override
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