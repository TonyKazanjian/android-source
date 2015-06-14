package io.bloc.android.blocly.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tonyk_000 on 3/11/2015.
 */
public class RssItem extends Model implements Parcelable {
    private String guid;
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private long rssFeedId;
    private long datePublished;
    private boolean read;
    private boolean favorite;
    private boolean archived;

    public static final Creator<RssItem> CREATOR = new Creator<RssItem>() {
        @Override
        public RssItem createFromParcel(Parcel source) {
            return new RssItem(source);
        }

        @Override
        public RssItem[] newArray(int size) {
            return new RssItem[size];
        }
    };

    public RssItem(Parcel src) {
        super(src.readLong());
        this.guid = src.readString();
        this.title = src.readString();
        this.description = src.readString();
        this.url = src.readString();
        this.imageUrl = src.readString();
        this.rssFeedId = src.readLong();
        this.datePublished = src.readLong();
        this.read = src.readInt() == 1;
        this.favorite = src.readInt() == 1;
        this.archived = src.readInt() == 1;
    }

    public RssItem(long rowId, String guid, String title, String description, String url, String imageUrl, long rssFeedId, long datePublished, boolean read, boolean favorite, boolean archived) {
        super(rowId);
        this.guid = guid;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.rssFeedId = rssFeedId;
        this.datePublished = datePublished;
        this.read = read;
        this.favorite = favorite;
        this.archived = archived;
    }

    public String getGuid() {
        return guid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getRssFeedId() {
        return rssFeedId;
    }

    public long getDatePublished() {
        return datePublished;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isArchived() {
        return archived;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getRowId());
        dest.writeString(guid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeLong(rssFeedId);
        dest.writeLong(datePublished);
        dest.writeInt(read ? 1 : 0);
        dest.writeInt(favorite ? 1 : 0);
        dest.writeInt(archived ? 1 : 0);
    }
}
