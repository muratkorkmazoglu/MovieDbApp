package com.murat.moviedbapp.Interface;

import com.murat.moviedbapp.Models.DiscoverModel;
import com.murat.moviedbapp.Models.MovieModel;
import com.murat.moviedbapp.Models.NowPlayingModel;
import com.murat.moviedbapp.Models.TopRatedModel;
import com.murat.moviedbapp.Models.UpComingModel;

import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RetroInterface {

    @GET("movie/top_rated?")
    Call<TopRatedModel> getTopRated(@Query("api_key") String apikey, @Query("page") Integer page);

    @GET("movie/upcoming?")
    Call<UpComingModel> getUpComing(@Query("api_key") String apikey, @Query("page") Integer page);

    @GET("movie/now_playing?")
    Call<NowPlayingModel> getNowPlaying(@Query("api_key") String apikey, @Query("page") Integer page);

    @GET("movie/{movie_id}?")
    Call<MovieModel> getMovie(@Path("movie_id") int movieId, @Query("api_key") String apikey
    );
}
