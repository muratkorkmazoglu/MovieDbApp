package com.murat.moviedbapp.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;

import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.murat.moviedbapp.Activity.DetailActivity;
import com.murat.moviedbapp.Activity.FavoritesActivity;
import com.murat.moviedbapp.Activity.NowPlaying;
import com.murat.moviedbapp.Activity.TopRated;
import com.murat.moviedbapp.Activity.UpComing;
import com.murat.moviedbapp.R;

import java.lang.reflect.Field;

public class BottomNavigationViewHelper {
   
    public static void setupBottomNavigationView(BottomNavigationViewEx navigationView) {
        navigationView.enableAnimation(false);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableShiftingMode(false);
        navigationView.setTextVisibility(true);
    }

    public static void setupNavigation(final Context context, BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_one:
                        Intent a = new Intent(context, TopRated.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(a);

                        break;
                    case R.id.navigation_two:
                        Intent b = new Intent(context, UpComing.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(b);

                        break;
                    case R.id.navigation_three:
                        Intent c = new Intent(context, NowPlaying.class);
                        c.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(c);

                        break;
                    case R.id.navigation_favourite:
                        Intent d = new Intent(context, FavoritesActivity.class);
                        context.startActivity(d);
                        d.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        break;
                }


                return true;
            }
        });

    }
}
