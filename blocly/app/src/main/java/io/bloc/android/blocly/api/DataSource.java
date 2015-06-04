package io.bloc.android.blocly.api;

import android.database.Cursor;
import android.os.Handler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.BuildConfig;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.api.model.database.DatabaseOpenHelper;
import io.bloc.android.blocly.api.model.database.table.RssFeedTable;
import io.bloc.android.blocly.api.model.database.table.RssItemTable;
import io.bloc.android.blocly.api.model.database.table.Table;
import io.bloc.android.blocly.api.network.GetFeedsNetworkRequest;
import io.bloc.android.blocly.api.network.NetworkRequest;

/**
 * Created by tonyk_000 on 3/11/2015.
 */
public class DataSource {

    public static interface Callback<Result>{
        public void onSuccess(Result result);
        public void onError(String errorMessage);
    }


    private DatabaseOpenHelper databaseOpenHelper;
    private RssFeedTable rssFeedTable;
    private RssItemTable rssItemTable;
    private ExecutorService executorService;

    public DataSource() {
        rssFeedTable = new RssFeedTable();
        rssItemTable = new RssItemTable();
        executorService = Executors.newSingleThreadExecutor();

        databaseOpenHelper = new DatabaseOpenHelper(BloclyApplication.getSharedInstance(),
                rssFeedTable, rssItemTable);
        if (BuildConfig.DEBUG && true) {
            BloclyApplication.getSharedInstance().deleteDatabase("blocly_db");
        }
    }
//checkpoint 56 method

    long insertResponseToFeed(String feedURL, GetFeedsNetworkRequest.ItemResponse itemResponse) {
        long itemPubDate = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
        try {
            itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new RssItemTable.Builder()
                .setTitle(itemResponse.itemTitle)
                .setDescription(itemResponse.itemDescription)
                .setEnclosure(itemResponse.itemEnclosureURL)
                .setMIMEType(itemResponse.itemEnclosureMIMEType)
                .setLink(itemResponse.itemURL)
                .setGUID(itemResponse.itemGUID)
                .setPubDate(itemPubDate)
                .insert(databaseOpenHelper.getWritableDatabase());
    }

    public void fetchNewItem(final String feedURL, final Callback callback){
        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable(){
            @Override
            public void run(){
                GetFeedsNetworkRequest getFeedsNetworkRequest = new GetFeedsNetworkRequest(feedURL);
                final List<RssItem> newItems = new ArrayList<RssItem>();
                List<GetFeedsNetworkRequest.FeedResponse> feedResponses = getFeedsNetworkRequest.performRequest();
                if (getFeedsNetworkRequest.getErrorCode() != 0) {
                    final String errorMessage;
                    if (getFeedsNetworkRequest.getErrorCode() == NetworkRequest.ERROR_IO) {
                        errorMessage = "Network error";
                    } else if (getFeedsNetworkRequest.getErrorCode() == NetworkRequest.ERROR_MALFORMED_URL) {
                        errorMessage = "Malformed URL error";
                    } else if (getFeedsNetworkRequest.getErrorCode() == GetFeedsNetworkRequest.ERROR_PARSING) {
                        errorMessage = "Error parsing feed";
                    } else {
                        errorMessage = "Error unknown";
                    }
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(errorMessage);
                        }
                    });
                    return;
                }
                GetFeedsNetworkRequest.FeedResponse feedResponse = feedResponses.get(0);
                for (GetFeedsNetworkRequest.ItemResponse itemResponse : feedResponse.channelItems){
                    long newItemRowId = insertResponseToFeed(feedURL, itemResponse);
                    Cursor newItemCursor = RssItemTable.fetchItemsForFeed(databaseOpenHelper.getReadableDatabase(), newItemRowId);
                        if (newItemCursor.getCount() != 0) {
                            newItemCursor.moveToNext();
                            newItems.add(itemFromCursor(newItemCursor));
                            newItemCursor.close();
                        }
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(newItems);
                        }
                    });
                }
            }
        });
    }

//    public static Cursor fetchItemsForFeed(SQLiteDatabase readonlyDatabase, long feedRowId) {
//        RssItemTable newItems = new RssItemTable();
//        return readonlyDatabase.query(true, newItems.getName(), null, newItems. + " = ?",
//                new String[]{String.valueOf(feedRowId)},
//                null, null, COLUMN_PUB_DATE + " DESC", null);
//    }
//    public void fetchNewItem(final String feedURL, Callback<RssFeed> callback){
//        fetchNewFeed(feedURL, callback = new Callback<RssFeed>() {
//            @Override
//            public void onSuccess(RssFeed rssFeed) {
//                GetFeedsNetworkRequest getFeedsNetworkRequest = new GetFeedsNetworkRequest(feedURL);
//                List<GetFeedsNetworkRequest.FeedResponse> feedResponses = getFeedsNetworkRequest.performRequest();
//                //loop throuh feed responses
//                for (GetFeedsNetworkRequest.FeedResponse feed : feedResponses) {
//
//                    //loop through channel items in each feed
//                    for (GetFeedsNetworkRequest.ItemResponse itemResponse : feed.channelItems) {
//
//                        long itemPubDate = System.currentTimeMillis();
//                        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
//                        try {
//                            itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        Cursor newItemCursor = RssItemTable.fetchItemsForFeed(databaseOpenHelper.getReadableDatabase(), itemPubDate);
//                        if (newItemCursor.getCount() == 0) {
//                            RssItem fetchedItem = itemFromCursor(newItemCursor);
//                            databaseOpenHelper.getWritableDatabase().insert(fetchedItem.getTitle(), null, contentValues);
//                            newItemCursor.close();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Toast.makeText(null, errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//    }



    public void fetchNewFeed(final String feedURL, final Callback<RssFeed> callback){
        final Handler callbackThreadHandler = new Handler();

        submitTask(new Runnable() {
            @Override
            public void run() {
                Cursor existingFeedCursor = RssFeedTable.fetchFeedWithURL(databaseOpenHelper.getReadableDatabase(),
                        feedURL);
                if (existingFeedCursor.moveToFirst()) {
                    final RssFeed fetchedFeed = feedFromCursor(existingFeedCursor);
                    existingFeedCursor.close();

                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(fetchedFeed);
                        }
                    });
                    return;
                }

                GetFeedsNetworkRequest getFeedsNetworkRequest = new GetFeedsNetworkRequest(feedURL);
                List<GetFeedsNetworkRequest.FeedResponse> feedResponses = getFeedsNetworkRequest.performRequest();

                if (getFeedsNetworkRequest.getErrorCode() != 0) {
                    final String errorMessage;
                    if (getFeedsNetworkRequest.getErrorCode() == NetworkRequest.ERROR_IO) {
                        errorMessage = "Network error";
                    } else if (getFeedsNetworkRequest.getErrorCode() == NetworkRequest.ERROR_MALFORMED_URL) {
                        errorMessage = "Malformed URL error";
                    } else if (getFeedsNetworkRequest.getErrorCode() == GetFeedsNetworkRequest.ERROR_PARSING) {
                        errorMessage = "Error parsing feed";
                    } else {
                        errorMessage = "Error unknown";
                    }
                    // #8b
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(errorMessage);
                        }
                    });
                    return;
                }

                GetFeedsNetworkRequest.FeedResponse newFeedResponse = feedResponses.get(0);
                long newFeedId = new RssFeedTable.Builder()
                        .setFeedURL(newFeedResponse.channelFeedURL)
                        .setSiteURL(newFeedResponse.channelURL)
                        .setTitle(newFeedResponse.channelTitle)
                        .setDescription(newFeedResponse.channelDescription)
                        .insert(databaseOpenHelper.getWritableDatabase());

                for (GetFeedsNetworkRequest.ItemResponse itemResponse : newFeedResponse.channelItems) {
                    long itemPubDate = System.currentTimeMillis();
                    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
                    try {
                        itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // #9
                    new RssItemTable.Builder()
                            .setTitle(itemResponse.itemTitle)
                            .setDescription(itemResponse.itemDescription)
                            .setEnclosure(itemResponse.itemEnclosureURL)
                            .setMIMEType(itemResponse.itemEnclosureMIMEType)
                            .setLink(itemResponse.itemURL)
                            .setGUID(itemResponse.itemGUID)
                            .setPubDate(itemPubDate)
                                    // #10
                            .setRSSFeed(newFeedId)
                            .insert(databaseOpenHelper.getWritableDatabase());
                }

                Cursor newFeedCursor = rssFeedTable.fetchRow(databaseOpenHelper.getReadableDatabase(), newFeedId);
                newFeedCursor.moveToFirst();
                final RssFeed fetchedFeed = feedFromCursor(newFeedCursor);
                newFeedCursor.close();
                callbackThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(fetchedFeed);
                    }
                });
            }
        });
    }

    public void fetchItemsForFeed(final RssFeed rssFeed, final Callback<List<RssItem>> callback) {

        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable() {
            @Override
            public void run() {
                final List<RssItem> resultList = new ArrayList<RssItem>();
                Cursor cursor = RssItemTable.fetchItemsForFeed(
                        databaseOpenHelper.getReadableDatabase(),
                        rssFeed.getRowId());
                // #12
                if (cursor.moveToFirst()) {
                    do {
                        resultList.add(itemFromCursor(cursor));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                callbackThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(resultList);
                    }
                });
            }
        });
    }

                // #4a

            static RssFeed feedFromCursor(Cursor cursor) {
                return new RssFeed(Table.getRowId(cursor), RssFeedTable.getTitle(cursor), RssFeedTable.getDescription(cursor),
                        RssFeedTable.getSiteURL(cursor), RssFeedTable.getFeedURL(cursor));
            }

            // #4b
            static RssItem itemFromCursor(Cursor cursor) {
                return new RssItem(Table.getRowId(cursor), RssItemTable.getGUID(cursor), RssItemTable.getTitle(cursor),
                        RssItemTable.getDescription(cursor), RssItemTable.getLink(cursor),
                        RssItemTable.getEnclosure(cursor), RssItemTable.getRssFeedId(cursor),
                        RssItemTable.getPubDate(cursor), false, RssItemTable.getFavorite(cursor),
                        RssItemTable.getArchived(cursor));
            }

            void submitTask(Runnable task) {
                if (executorService.isShutdown() || executorService.isTerminated()) {
                    executorService = Executors.newSingleThreadExecutor();
                }
                executorService.submit(task);
            }
        }


