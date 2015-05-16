package io.bloc.android.blocly.api.model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.api.model.database.DatabaseOpenHelper;

/**
 * Created by tonyk_000 on 5/5/2015.
 */
public class RssItemTable extends Table {

    public static class Builder implements Table.Builder{

        ContentValues values = new ContentValues();

        public Builder setLink(String link){
            values.put(COLUMN_LINK, link);
            return this;
        }

        public Builder setTitle(String title){
            values.put(COLUMN_TITLE,title);
            return this;
        }

        public Builder setDescription(String description) {
            values.put(COLUMN_DESCRIPTION, description);
            return this;
        }

        public Builder setGUID(String guid) {
            values.put(COLUMN_GUID, guid);
            return this;
        }

        public Builder setPubDate(long pubDate) {
            values.put(COLUMN_PUB_DATE, pubDate);
            return this;
        }

        public Builder setEnclosure(String enclosure) {
            values.put(COLUMN_ENCLOSURE, enclosure);
            return this;
        }

        public Builder setMIMEType(String mimeType) {
            values.put(COLUMN_MIME_TYPE, mimeType);
            return this;
        }

        public Builder setRSSFeed(long rssFeed) {
            values.put(COLUMN_RSS_FEED, rssFeed);
            return this;
        }

        @Override
        public long insert(SQLiteDatabase writableDB) {
            return writableDB.insert(RssItemTable.NAME, null, values);
        }
    }

    public static String getLink(Cursor cursor) {
        return getString(cursor, COLUMN_LINK);
    }

    public static String getTitle(Cursor cursor) {
        return getString(cursor, COLUMN_TITLE);
    }

    public static String getDescription(Cursor cursor) {
        return getString(cursor, COLUMN_DESCRIPTION);
    }

    public static String getGUID(Cursor cursor) {
        return getString(cursor, COLUMN_GUID);
    }

    public static long getRssFeedId(Cursor cursor) {
        return getLong(cursor, COLUMN_RSS_FEED);
    }

    public static long getPubDate(Cursor cursor) {
        return getLong(cursor, COLUMN_PUB_DATE);
    }

    public static String getEnclosure(Cursor cursor) {
        return getString(cursor, COLUMN_ENCLOSURE);
    }

    public static boolean getFavorite(Cursor cursor) {
        return getBoolean(cursor, COLUMN_FAVORITE);
    }

    public static boolean getArchived(Cursor cursor) {
        return getBoolean(cursor, COLUMN_ARCHIVED);
    }

    private static final String NAME = "rss_items";


    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_GUID = "guid";
    private static final String COLUMN_PUB_DATE = "pub_date";
    private static final String COLUMN_ENCLOSURE = "enclosure";
    private static final String COLUMN_MIME_TYPE = "mime_type";
    private static final String COLUMN_RSS_FEED = "rss_feed";
    private static final String COLUMN_FAVORITE = "is_favorite";
    private static final String COLUMN_ARCHIVED = "is_archived";

    //checkpoint 55 assignment

    //method to fetch archived items, favorited items, or all items in a particular feed
    public Cursor fetchRow(SQLiteDatabase readOnlyDatabase, long rowId, Boolean isArchived, Boolean isFavorite,
                           int offset, int limit) {
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("SELECT * FROM " + COLUMN_RSS_FEED + " WHERE rowID = " + rowId);
        if (isArchived != null) {
            sqlStatement.append(" AND " + COLUMN_ARCHIVED + " = " + isArchived);
        }
        if (isFavorite != null) {
            sqlStatement.append(" AND " + COLUMN_FAVORITE + " = " + isFavorite);
        }
        sqlStatement.append(" OFFSET " + offset + " LIMIT " + limit);
        sqlStatement.append(";");
        return readOnlyDatabase.rawQuery(sqlStatement.toString(), null);
    }

    DatabaseOpenHelper db = new DatabaseOpenHelper(BloclyApplication.getSharedInstance());
    SQLiteDatabase readableDatabase = db.getReadableDatabase();
    RssItemTable rssItemTable = new RssItemTable();
    long feedId = new RssFeedTable.Builder().setTitle("cool feed").insert(readableDatabase);
    //returning all archived Rss Items
    Cursor findArchived = rssItemTable.fetchRow(readableDatabase, 0 ,true,null,0,0);
    //returnign all archived Rss Items from a particular feed
    Cursor findArchivedInFeed = rssItemTable.fetchRow(readableDatabase,feedId,true,null,0,0);
    //returning all favorited
    Cursor findFavorited = rssItemTable.fetchRow(readableDatabase,0,null,true,0,0);
    //returning all favorited from a particular feed
    Cursor findFavoritedInFeed = rssItemTable.fetchRow(readableDatabase,feedId,null,true,0,0);
    //returning all items from a particular feed
    Cursor allFromFeed = rssItemTable.fetchRow(readableDatabase,feedId,null,null,0,0);
    //fetch all items from a particular RSS feed with a given OFFSET and LIMIT
    Cursor allFromFeedWithOffsetAndLimit = rssItemTable.fetchRow(readableDatabase,feedId,null,null,10,20);

    @Override
    public  String getName() {
        return "rss_items";
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LINK + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_GUID + " TEXT,"
                + COLUMN_PUB_DATE + " INTEGER,"
                + COLUMN_ENCLOSURE + " TEXT,"
                + COLUMN_MIME_TYPE + " TEXT,"
                // #4
                + COLUMN_RSS_FEED + " INTEGER,"
                // #5
                + COLUMN_FAVORITE + " INTEGER DEFAULT 0,"
                + COLUMN_ARCHIVED + " INTEGER DEFAULT 0)";
    }
}
