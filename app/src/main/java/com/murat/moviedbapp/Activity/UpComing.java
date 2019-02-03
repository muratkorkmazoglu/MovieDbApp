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
import com.murat.moviedbapp.Adapter.UpComingAdapters.UpComingAdapter;
import com.murat.moviedbapp.Adapter.UpComingAdapters.UpComingGridAdapter;
import com.murat.moviedbapp.Interface.ApiListener;
import com.murat.moviedbapp.Interface.RetroInterface;
import com.murat.moviedbapp.Models.TopRatedModel;
import com.murat.moviedbapp.Models.UpComingModel;
import com.murat.moviedbapp.R;
import com.murat.moviedbapp.Utils.ApiName;
import com.murat.moviedbapp.Utils.ApiResponse;
import com.murat.moviedbapp.Utils.BottomNavigationViewHelper;
import com.murat.moviedbapp.Utils.RetroClient;

import java.util.ArrayList;

import retrofit2.Call;

public class UpComing extends AppCompatActivity implements ApiListener {

    private RetroInterface retroInterface;
    private UpComingModel upComingModel;
    private ArrayList<UpComingModel.Result> modelList;
    private ArrayList<UpComingModel.Result> newList;

    private UpComingAdapter upComingAdapter;
    private int page = 1;

    //---------For GridView Layout------------
    private ViewStub stubGrid;
    private ViewStub stubList;
    private GridView gridView;
    private ListView listView;
    private UpComingGridAdapter upComingGridAdapter;
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    private int currentViewMode = 0;
    SearchView searchView;
    BottomNavigationViewEx bottomNavigationViewEx;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
        Call<UpComingModel> call = retroInterface.getUpComing(getString(R.string.apiKey), page);
        ApiResponse.callRetrofit(call, ApiName.getUpComing, UpComing.this, this);

        setupNavigationView();
    }

    public void setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    @Override
    public void success(ApiName strApiName, Object response) {
        if (ApiName.getUpComing == strApiName) {
            upComingModel = (UpComingModel) response;

            modelList.addAll(upComingModel.results);
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

    private void loadMore() {
        page++;

        Call<UpComingModel> call = retroInterface.getUpComing(getString(R.string.apiKey), page);
        ApiResponse.callRetrofit(call, ApiName.getUpComing, UpComing.this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        switchView();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && listView.getLastVisiblePosition() == modelList.size() - 1) {
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
                if (scrollState == SCROLL_STATE_IDLE && gridView.getLastVisiblePosition() == modelList.size() - 1) {

                    loadMore();


                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void switchView() {


        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            //Display listview
            stubList.setVisibility(View.VISIBLE);
            //Hide gridview
            stubGrid.setVisibility(View.GONE);

        } else if (VIEW_MODE_GRIDVIEW == currentViewMode) {
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
                if (VIEW_MODE_LISTVIEW == currentViewMode) {
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
                for (UpComingModel.Result model : modelList) {
                    String title = model.getTitle().toLowerCase();
                    if (title.contains(newText)) {
                        newList.add(model);
                    }
                }
                if (currentViewMode == VIEW_MODE_LISTVIEW)
                    upComingAdapter.setFilter(newList);
                else
                    upComingGridAdapter.setFilter(newList);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {

            if (listView.getAdapter() != null) {
                upComingAdapter = (UpComingAdapter) listView.getAdapter();

            } else {
                upComingAdapter = new UpComingAdapter(getApplicationContext(), modelList);
                listView.setAdapter(upComingAdapter);
            }

            upComingAdapter.notifyDataSetChanged();

        } else if (VIEW_MODE_GRIDVIEW == currentViewMode) {

            if (gridView.getAdapter() != null) {

                upComingGridAdapter = (UpComingGridAdapter) gridView.getAdapter();

            } else {
                upComingGridAdapter = new UpComingGridAdapter(getApplicationContext(), modelList);
                gridView.setAdapter(upComingGridAdapter);

            }
            upComingGridAdapter.notifyDataSetChanged();

        }
//---------For GridView Layout------------
    }
}
