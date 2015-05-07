package io.bloc.android.blocly.api.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by tonyk_000 on 5/1/2015.
 */

    public abstract class NetworkRequest<RssFeed> {

        // #2
        public static final int ERROR_IO = 1;
        public static final int ERROR_MALFORMED_URL = 2;

        private int errorCode;

        protected void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }

        // #3
        public abstract List<io.bloc.android.blocly.api.model.RssFeed> performRequest();

    // #4
    protected InputStream openStream(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            setErrorCode(ERROR_MALFORMED_URL);
            return null;
        }
        InputStream inputStream = null;
        try {
// #5
            inputStream = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            setErrorCode(ERROR_IO);
            return null;
        }
        return inputStream;
    }
    }

