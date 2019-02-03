package com.murat.moviedbapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.murat.moviedbapp.Activity.DetailActivity;
import com.murat.moviedbapp.Activity.FavoritesActivity;
import com.murat.moviedbapp.Models.MovieInfoModel;
import com.murat.moviedbapp.Models.UpComingModel;
import com.murat.moviedbapp.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Murat on 3.02.2019.
 */

public class FavouriteAdapter extends BaseAdapter {

    ArrayList<MovieInfoModel> modelList;

    Context context;
    @BindView(R.id.movie_name)
    TextView movieName;
    @BindView(R.id.movie_date)
    TextView movieDate;
    @BindView(R.id.movie_description)
    TextView movieDescription;
    @BindView(R.id.movie_photo)
    ImageView moviePhoto;
    @BindView(R.id.moreInfo)
    RelativeLayout moreInfo;


    public FavouriteAdapter(Context context, ArrayList<MovieInfoModel> discoverModel) {

        this.modelList = discoverModel;
        this.context = context;
    }


    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.discover_item, parent, false);

        }
        ButterKnife.bind(this, convertView);
        movieName.setText(modelList.get(position).getTitle());
        movieDate.setText(modelList.get(position).getDate());
        movieDescription.setText(modelList.get(position).getOverview());
        Picasso.get().load(context.getString(R.string.imagePath) + modelList.get(position).getImage()).into(moviePhoto);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieInfoModel movieInfoModel = new MovieInfoModel();
                movieInfoModel.setTitle(modelList.get(position).getTitle());
                movieInfoModel.setImage(modelList.get(position).getImage());
                movieInfoModel.setDate(modelList.get(position).getDate());
                movieInfoModel.setOverview(modelList.get(position).getOverview());
                movieInfoModel.setMovieId(modelList.get(position).getId());
                movieInfoModel.setBudget(modelList.get(position).getBudget());
                movieInfoModel.setRevenue(modelList.get(position).getRevenue());
                movieInfoModel.setId(modelList.get(position).getId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("movieInfo", (Serializable) movieInfoModel);
                bundle.putBoolean("favourite", true);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);




            }
        });


        return convertView;
    }


}