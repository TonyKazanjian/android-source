package io.bloc.android.blocly;

import android.app.Application;

import io.bloc.android.blocly.api.DataSource;

/**
 * Created by tonyk_000 on 3/11/2015.
 */

    public class BloclyApplication extends Application {

        // #1 - creates singleton instance of BloclyApplication and DataSource
        public static BloclyApplication getSharedInstance() {
            return sharedInstance;
        }
        // #1 and 2 allow any class from any part of the application to grab a reference to these objects
        // #2
        public static DataSource getSharedDataSource() {
            return BloclyApplication.getSharedInstance().getDataSource();
        }

        private static BloclyApplication sharedInstance;
        private DataSource dataSource;

        // #3 - This method is used to initialize all of the shared application elements, in our case the DataSource.
        @Override
        public void onCreate() {
            super.onCreate();
            sharedInstance = this;
            dataSource = new DataSource();
        }

        public DataSource getDataSource() {
            return dataSource;
        }
}
