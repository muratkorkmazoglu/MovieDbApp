package com.murat.moviedbapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.murat.moviedbapp.Adapter.FavouriteAdapter;
import com.murat.moviedbapp.Adapter.TopRatedAdapters.TopRatedAdapter;
import com.murat.moviedbapp.Models.MovieInfoModel;
import com.murat.moviedbapp.Models.TopRatedModel;
import com.murat.moviedbapp.R;
import com.murat.moviedbapp.Utils.BottomNavigationViewHelper;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class FavoritesActivity extends AppCompatActivity {

    private Realm realm;
    private ArrayList<MovieInfoModel> modelList;
    private FavouriteAdapter favouriteAdapter;
    private ListView favouriteListview;
    BottomNavigationViewEx bottomNavigationViewEx;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_favourite);
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        modelList = new ArrayList<>();
        favouriteListview = findViewById(R.id.mylistview);
        bottomNavigationViewEx = findViewById(R.id.navigation);

        setupNavigationView();

    }
    public void setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }
    private void getList() {
        RealmResults<MovieInfoModel> realmResults = Realm.getDefaultInstance().where(MovieInfoModel.class).findAll();
        modelList.addAll(realmResults);
        if ( favouriteListview.getAdapter() != null ) {
            favouriteAdapter = (FavouriteAdapter) favouriteListview.getAdapter();

        }else {
            favouriteAdapter = new FavouriteAdapter(getApplicationContext(), modelList);
            favouriteListview.setAdapter(favouriteAdapter);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getList();
    }
}
