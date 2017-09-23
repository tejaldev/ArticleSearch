package com.ny.search.article.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Article data model
 *
 * @author tejalpar
 */
public class Article implements Parcelable {
    public static final String BASE_IMAGE_PATH = "http://www.nytimes.com/";
    public static final String PARCEL_KEY = "ARTICLE_DATA";

    public String articleId;
    public String snippet;
    public String headline;
    public String newDesk;
    public String webUrl;
    public String wideUrl;
    public String xlargeUrl;
    public String thumbnailUrl;

    public Article() {}

    public Article(Parcel source) {
        // read values from parcel
        readFromParcel(source);
    }

    public static String getAbsoluteMoviePath(String relativePath) {
        return BASE_IMAGE_PATH + relativePath;
    }

    public boolean hasImage() {
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            return true;
        } else if (wideUrl != null && !wideUrl.isEmpty()) {
            return true;
        } else if (xlargeUrl != null && !xlargeUrl.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleId);
        dest.writeString(snippet);
        dest.writeString(newDesk);
        dest.writeString(headline);
        dest.writeString(webUrl);
        dest.writeString(wideUrl);
        dest.writeString(xlargeUrl);
        dest.writeString(thumbnailUrl);
    }

    private void readFromParcel(Parcel source) {
        // read in same order as written
        articleId = source.readString();
        snippet = source.readString();
        newDesk = source.readString();
        headline = source.readString();
        webUrl = source.readString();
        wideUrl = source.readString();
        xlargeUrl = source.readString();
        thumbnailUrl = source.readString();
    }

    public static final Creator<Article> CREATOR
            = new Creator<Article>() {

        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[0];
        }
    };
}
