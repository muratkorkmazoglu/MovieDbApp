package com.murat.moviedbapp.Adapter.NowPlayingAdapters;

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
import com.murat.moviedbapp.Models.MovieInfoModel;
import com.murat.moviedbapp.Models.NowPlayingModel;
import com.murat.moviedbapp.R;
import com.squareup.picasso.Picasso;
import java.io.Serializable;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NowPlayingGridAdapter extends BaseAdapter {

    ArrayList<NowPlayingModel.Result> modelList;
    private ArrayList<NowPlayingModel.Result> filter;
    Context context;
    @BindView(R.id.titleGrid)
    TextView movieTitle;
    @BindView(R.id.release_date)
    TextView movieDate;
    @BindView(R.id.moreInfoRL)
    RelativeLayout moreInfo;
    @BindView(R.id.lessons_image)
    ImageView moviePhoto;


    public NowPlayingGridAdapter(Context context, ArrayList<NowPlayingModel.Result> discoverModel) {
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
                    inflate(R.layout.grid_item, parent, false);
        }
        ButterKnife.bind(this, convertView);

        movieTitle.setText(modelList.get(position).getTitle().toString());
        movieDate.setText(modelList.get(position).getReleaseDate().toString());
        Picasso.get().load(context.getString(R.string.imagePath) + modelList.get(position).getPosterPath()).into(moviePhoto);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MovieInfoModel movieInfoModel = new MovieInfoModel();
                movieInfoModel.setTitle(modelList.get(position).getTitle());
                movieInfoModel.setImage(modelList.get(position).getPosterPath());
                movieInfoModel.setDate(modelList.get(position).getReleaseDate());
                movieInfoModel.setOverview(modelList.get(position).getOverview());
                movieInfoModel.setMovieId(modelList.get(position).getId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("movieInfo", (Serializable) movieInfoModel);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);

            }
        });


        return convertView;
    }
    public void setFilter(ArrayList<NowPlayingModel.Result> filter) {
        this.filter = filter;
        modelList = new ArrayList<>();
        if (this.filter != null) {

            modelList.addAll(filter);
            notifyDataSetChanged();
        } else {

            modelList = new ArrayList<>();
        }

    }


}

