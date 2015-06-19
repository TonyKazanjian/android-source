package io.bloc.android.blocly.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;

/**
 * Created by tonyk_000 on 6/5/2015.
 */
public class RssItemDetailFragment extends Fragment  implements ImageLoadingListener, ItemAdapter.Delegate, View.OnClickListener, RssItemListFragment.Delegate {

    private static final String BUNDLE_EXTRA_RSS_ITEM = RssItemDetailFragment.class.getCanonicalName().concat(".EXTRA_RSS_ITEM");


    public static RssItemDetailFragment detailFragmentForRssItem(RssItem rssItem) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_EXTRA_RSS_ITEM, rssItem.getRowId());
        RssItemDetailFragment rssItemDetailFragment = new RssItemDetailFragment();
        rssItemDetailFragment.setArguments(arguments);
        return rssItemDetailFragment;
    }

    ImageView headerImage;
    TextView title;
    TextView content;
    ProgressBar progressBar;
    Toolbar mToolbar;
    TextView visitSite;
    MenuItem shareItem;
    Menu menu;
    int currentOrientation;

    //for onVisitCLicked
    ItemAdapter tabletAdapter = new ItemAdapter();
    RssItem rssItem;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

       mToolbar = (Toolbar)getActivity().findViewById(R.id.tb_activity_blocly);
       ((ActionBarActivity)getActivity()).setSupportActionBar(mToolbar);
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        long rssItemId = arguments.getLong(BUNDLE_EXTRA_RSS_ITEM);
        BloclyApplication.getSharedDataSource().fetchRSSItemWithId(rssItemId, new DataSource.Callback<RssItem>() {
            @Override
            public void onSuccess(RssItem rssItem) {
                if (getActivity() == null)
                    return;
                title.setText(rssItem.getTitle());
                content.setText(rssItem.getDescription());
                ImageLoader.getInstance().loadImage(rssItem.getImageUrl(), RssItemDetailFragment.this);
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_rss_item_detail, container, false);
        mToolbar = (Toolbar) inflate.findViewById(R.id.fl_tb_activity_blocly);
        shareItem = (MenuItem)inflate.findViewById(R.id.fragment_action_share);
        visitSite = (TextView)inflate.findViewById(R.id.tv_rss_item_visit_site);
        visitSite.setOnClickListener(this);
        headerImage = (ImageView) inflate.findViewById(R.id.iv_fragment_rss_item_detail_header);
        progressBar = (ProgressBar) inflate.findViewById(R.id.pb_fragment_rss_item_detail_header);
        title = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_title);
        content = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_content);
        return inflate;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if(currentOrientation==Configuration.ORIENTATION_LANDSCAPE) {
            ((ActionBarActivity) getActivity()).getMenuInflater().inflate(R.menu.fragment_blocly, menu);
            return ((ActionBarActivity)getActivity()).onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        currentOrientation = newConfig.orientation;
        if(currentOrientation==Configuration.ORIENTATION_LANDSCAPE){
            Log.e("On Config Change", "LANDSCAPE"); }else{ Log.e("On Config Change","PORTRAIT"); }
    }


        /*
      * ImageLoadingListener
      */

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        progressBar.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.setImageBitmap(loadedImage);
        headerImage.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {}

    /*
    ItemAdapter.Delegate
     */

    public void onVisitClicked(ItemAdapter itemAdapter, RssItem rssItem) {

            if (tabletAdapter.getDelegate() != null) {
                tabletAdapter.getDelegate().onVisitClicked(tabletAdapter, rssItem);
            }
    }
    public void onItemClicked(ItemAdapter itemAdapter, RssItem rssItem){}

    public void onClick(View view){
        onVisitClicked(tabletAdapter,rssItem);
    }

    /*
    RssItemListFragment.Delagate
     */

    public void onItemExpanded(RssItemListFragment rssItemListFragment, RssItem rssItem){}
    public void onItemContracted(RssItemListFragment rssItemListFragment, RssItem rssItem){}
    public void onItemVisitClicked(RssItemListFragment rssItemListFragment, RssItem rssItem){
        Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getUrl()));
        startActivity(visitIntent);

    }
}

