package io.bloc.android.blocly.api.model;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by tonyk_000 on 3/11/2015.
 */
public class RssFeed extends Model implements Parcelable {
    private String title;
    private String description;
    private String siteUrl;
    private String feedUrl;

    public static final Creator<RssFeed> CREATOR = new Creator<RssFeed>() {
        @Override
        public RssFeed createFromParcel(Parcel source) {
            return new RssFeed(source);
        }

        @Override
        public RssFeed[] newArray(int size) {
            return new RssFeed[size];
        }
    };
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public RssFeed(Parcel parcel) {
        super(parcel.readLong());
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.siteUrl = parcel.readString();
        this.feedUrl = parcel.readString();
    }

    public RssFeed(long rowId, String title, String description, String siteUrl, String feedUrl) {
        super(rowId);
        this.title = title;
        this.description = description;
        this.siteUrl = siteUrl;
        this.feedUrl = feedUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getRowId());
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(siteUrl);
        dest.writeString(feedUrl);
    }
}
