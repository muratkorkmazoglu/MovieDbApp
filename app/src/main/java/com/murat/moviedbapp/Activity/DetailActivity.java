package com.murat.moviedbapp.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.murat.moviedbapp.Adapter.GenresAdapter;
import com.murat.moviedbapp.Interface.ApiListener;
import com.murat.moviedbapp.Interface.RetroInterface;

import com.murat.moviedbapp.Models.MovieInfoModel;
import com.murat.moviedbapp.Models.MovieModel;
import com.murat.moviedbapp.R;
import com.murat.moviedbapp.Utils.ApiName;
import com.murat.moviedbapp.Utils.ApiResponse;
import com.murat.moviedbapp.Utils.RetroClient;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

import static java.text.NumberFormat.getCurrencyInstance;

public class DetailActivity extends AppCompatActivity implements ApiListener {

    ImageView imageView;
    TextView releaseText;
    TextView overviewText;
    TextView budgetText;
    TextView revenueText;
    TextView avarageText;
    RecyclerView recyclerView;
    Boolean saved = false;
    MovieInfoModel movieInfoModel;

    private RetroInterface retroInterface;
    private MovieModel movieModel;
    private ArrayList<MovieModel.Genre> modelList;
    private GenresAdapter genresAdapter;
    private Realm realm;
    int itemId = -1;
    Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_movies);
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        imageView = findViewById(R.id.poster_path);
        releaseText = findViewById(R.id.release);
        overviewText = findViewById(R.id.overviewText2);
        budgetText = findViewById(R.id.budgetMiktar);
        revenueText = findViewById(R.id.revenueMiktar);
        avarageText = findViewById(R.id.average);
        recyclerView = findViewById(R.id.recycler_view_genres);

        retroInterface = RetroClient.getClient().create(RetroInterface.class);
        modelList = new ArrayList<>();

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            movieInfoModel = (MovieInfoModel) bundle.getSerializable("movieInfo");
            Log.e("asdasd", movieInfoModel.getDate());
            //((MainActivity) Objects.requireNonNull(getApplicationContext())).setActionBarTitle(movieInfoModel.getTitle());
            releaseText.setText(movieInfoModel.getDate());
            overviewText.setText(movieInfoModel.getOverview());
            Picasso.get().load(getString(R.string.imagePath) + movieInfoModel.getImage()).into(imageView);
            itemId = movieInfoModel.getId();
            Call<MovieModel> call = retroInterface.getMovie(movieInfoModel.getMovieId(), getString(R.string.apiKey));
            ApiResponse.callRetrofit(call, ApiName.getMovieCall, DetailActivity.this, this);

        }
        if (bundle.getBoolean("favourite")) {
            saved = true;

        }
    }

    @Override
    public void success(ApiName strApiName, Object response) {
        if (ApiName.getMovieCall.equals(strApiName)) {
            movieModel = (MovieModel) response;
            NumberFormat format = getCurrencyInstance(Locale.getDefault());

            budgetText.setText(format.format(movieModel.getBudget()));
            revenueText.setText(format.format(movieModel.getRevenue()));
            avarageText.setText("%" + movieModel.getVoteAverage().toString());

            for (int i = 0; i < movieModel.getGenres().size(); i++) {
                modelList.add(movieModel.getGenres().get(i));
            }

            genresAdapter = new GenresAdapter(getApplicationContext(), modelList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            recyclerView.setAdapter(genresAdapter);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            if (saved)
                menu.findItem(R.id.add_favorite).setIcon(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void error(ApiName strApiName, String error) {

    }

    @Override
    public void failure(ApiName strApiName, String message) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.add_favorite_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_favorite:
                if (saved) {
                    Toast.makeText(getApplicationContext(), " Deleted", Toast.LENGTH_SHORT).show();
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (itemId != -1) {
                                RealmResults<MovieInfoModel> result = realm.where(MovieInfoModel.class).equalTo("id", itemId).findAll();
                                result.deleteAllFromRealm();

                            }

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Intent intent=new Intent(DetailActivity.this,FavoritesActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();

                        }
                    });
                    saved = false;

                } else {

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Number maxId = realm.where(MovieInfoModel.class).max("id");
                            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                            MovieInfoModel movieInfoModel = realm.createObject(MovieInfoModel.class, nextId);
                            movieInfoModel.setDate(movieModel.getReleaseDate());
                            movieInfoModel.setImage(movieModel.getPosterPath());
                            movieInfoModel.setOverview(movieModel.getOverview());
                            movieInfoModel.setTitle(movieModel.getTitle());
                            movieInfoModel.setBudget(movieModel.getBudget());
                            movieInfoModel.setRevenue(movieModel.getRevenue());

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(DetailActivity.this, "added to favorites", Toast.LENGTH_SHORT).show();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(DetailActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    //Toast.makeText(getApplicationContext(), " Eklendi", Toast.LENGTH_SHORT).show();

                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                    saved = true;


                }


                break;
        }
        return true;
    }


}
