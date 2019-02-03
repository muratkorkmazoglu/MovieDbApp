package com.murat.moviedbapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.murat.moviedbapp.Adapter.NowPlayingAdapters.NowPlayingAdapter;
import com.murat.moviedbapp.Adapter.NowPlayingAdapters.NowPlayingGridAdapter;
import com.murat.moviedbapp.Interface.ApiListener;
import com.murat.moviedbapp.Interface.RetroInterface;
import com.murat.moviedbapp.Models.NowPlayingModel;
import com.murat.moviedbapp.Models.TopRatedModel;
import com.murat.moviedbapp.R;
import com.murat.moviedbapp.Utils.ApiName;
import com.murat.moviedbapp.Utils.ApiResponse;
import com.murat.moviedbapp.Utils.BottomNavigationViewHelper;
import com.murat.moviedbapp.Utils.RetroClient;

import java.util.ArrayList;

import retrofit2.Call;

public class NowPlaying extends AppCompatActivity implements ApiListener {
    private RetroInterface retroInterface;
    private NowPlayingModel nowPlayingModel;
    private ArrayList<NowPlayingModel.Result> modelList;
    private ArrayList<NowPlayingModel.Result> newList;

    private NowPlayingAdapter nowPlayingAdapter;
    private int page = 1;

    //---------For GridView Layout------------
    private ViewStub stubGrid;
    private ViewStub stubList;
    private GridView gridView;
    private ListView listView;
    private NowPlayingGridAdapter nowPlayingGridAdapter;
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    private int currentViewMode = 0;
    BottomNavigationViewEx bottomNavigationViewEx;

    SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_one);

        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        stubList.inflate();
        stubGrid.inflate();
        listView = (ListView) findViewById(R.id.mylistview);
        gridView = (GridView) findViewById(R.id.mygridView);
        bottomNavigationViewEx = findViewById(R.id.navigation);

        retroInterface = RetroClient.getClient().create(RetroInterface.class);
        modelList = new ArrayList<>();
        Call<NowPlayingModel> call = retroInterface.getNowPlaying(getString(R.string.apiKey),  page);
        ApiResponse.callRetrofit(call, ApiName.getNowPlaying, NowPlaying.this, this);

        setupNavigationView();

    }


    public void setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }
    private void loadMore() {
        page++;

            Call<NowPlayingModel> call = retroInterface.getNowPlaying(getString(R.string.apiKey),  page);
            ApiResponse.callRetrofit(call, ApiName.getNowPlaying, NowPlaying.this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        switchView();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if ( scrollState == SCROLL_STATE_IDLE && listView.getLastVisiblePosition() == modelList.size() - 1 ) {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if ( scrollState == SCROLL_STATE_IDLE && gridView.getLastVisiblePosition() == modelList.size() - 1 ) {

                    loadMore();


                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void switchView() {


        if ( VIEW_MODE_LISTVIEW == currentViewMode ) {
            //Display listview
            stubList.setVisibility(View.VISIBLE);
            //Hide gridview
            stubGrid.setVisibility(View.GONE);

        } else if ( VIEW_MODE_GRIDVIEW == currentViewMode ) {
            //Hide listview
            stubList.setVisibility(View.GONE);
            //Display gridview
            stubGrid.setVisibility(View.VISIBLE);

        }
        setAdapters();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.one_row:
                if ( VIEW_MODE_LISTVIEW == currentViewMode ) {
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_one_row));
                } else {
                    currentViewMode = VIEW_MODE_LISTVIEW;
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.grid));

                }

                switchView();

                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                newList = new ArrayList<>();
                for (NowPlayingModel.Result model : modelList) {
                    String title = model.getTitle().toLowerCase();
                    //String title = model.getOverview().toLowerCase();
                    if (title.contains(newText)) {
                        newList.add(model);
                    }
                }
                if (currentViewMode == VIEW_MODE_LISTVIEW)
                    nowPlayingAdapter.setFilter(newList);
                else
                    nowPlayingGridAdapter.setFilter(newList);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setAdapters() {
        if ( VIEW_MODE_LISTVIEW == currentViewMode ) {

            if ( listView.getAdapter() != null ) {
                nowPlayingAdapter = (NowPlayingAdapter) listView.getAdapter();

            } else {
                nowPlayingAdapter = new NowPlayingAdapter(getApplicationContext(), modelList);
                listView.setAdapter(nowPlayingAdapter);
            }

            nowPlayingAdapter.notifyDataSetChanged();

        } else if ( VIEW_MODE_GRIDVIEW == currentViewMode ) {

            if ( gridView.getAdapter() != null ) {

                nowPlayingGridAdapter = (NowPlayingGridAdapter) gridView.getAdapter();

            } else {
                nowPlayingGridAdapter = new NowPlayingGridAdapter(getApplicationContext(), modelList);
                gridView.setAdapter(nowPlayingGridAdapter);

            }
            nowPlayingGridAdapter.notifyDataSetChanged();

        }
//---------For GridView Layout------------
    }

    @Override
    public void success(ApiName strApiName, Object response) {
        if ( ApiName.getNowPlaying == strApiName ) {
            nowPlayingModel = (NowPlayingModel) response;

            modelList.addAll(nowPlayingModel.results);
            setAdapters();
            Log.e("RESPONSE----", " " + modelList.size());
        }
    }

    @Override
    public void error(ApiName strApiName, String error) {

    }

    @Override
    public void failure(ApiName strApiName, String message) {

    }
}
