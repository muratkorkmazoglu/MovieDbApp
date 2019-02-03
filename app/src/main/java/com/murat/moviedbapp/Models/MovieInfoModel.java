package com.murat.moviedbapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RealmClass
public class MovieInfoModel extends RealmObject implements Serializable {
    @PrimaryKey
    private int id;
    private String title, image, date, overview;
    private int movieId;
    private Integer budget;
    private Integer revenue;


}
