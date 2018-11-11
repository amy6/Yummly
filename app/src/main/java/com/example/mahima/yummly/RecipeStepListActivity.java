package com.example.mahima.yummly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.LOG_TAG;

public class RecipeStepListActivity extends AppCompatActivity implements OnItemClickListener, Player.EventListener {

    @Nullable
    @BindView(R.id.recipe_step_video)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.recipe_step_desc)
    TextView recipeStepDesc;

    private boolean isTwoPane;
    private Recipe recipe;
    private Uri uri;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private int id;
    private int count;
    private RecipeStep recipeStep;
    private List<RecipeStep> recipeSteps;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        ButterKnife.bind(this);

        recipeSteps = new ArrayList<>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean("play_when_ready");
            currentWindow = savedInstanceState.getInt("current_window");
            playbackPosition = savedInstanceState.getLong("playback_position");
            currentPosition = savedInstanceState.getInt("recipe_step_position");
            Log.d(LOG_TAG, "TwoPane Restoring exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
            Log.d(LOG_TAG, "TwoPane Restoring steps position:::\t\t" + currentPosition);
        }

        isTwoPane = findViewById(R.id.recipe_step_detail_container) != null;

        if (getIntent() != null) {
            if (getIntent().hasExtra("recipe_id")) {
                id = getIntent().getIntExtra("recipe_id", 0);
                Log.d(LOG_TAG, "Inside get intent");
                recipe = Utils.getRecipeById(this, id);
                setTitle(recipe.getName());

                if (isTwoPane) {
                    recipeSteps = recipe.getSteps();
                    count = recipeSteps.size();
                    recipeStep = recipeSteps.get(currentPosition);
                    uri = Uri.parse(recipeStep.getVideoURL());
                    initializePlayer();
                }

                if (getSupportFragmentManager().findFragmentById(R.id.recipe_step_list_container) == null) {
                    RecipeStepListFragment recipeStepListFragment = RecipeStepListFragment.newInstance(recipe);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.recipe_step_list_container, recipeStepListFragment)
                            .commit();
                }

            }
        }

        player.addListener(this);
    }

    @Override
    public void onItemClick(int position) {
        currentWindow = 0;
        playbackPosition = 0;
        currentPosition = position;
        recipeStep = recipeSteps.get(currentPosition);
        if (isTwoPane) {
            if (recipeStepDesc != null) {
                recipeStepDesc.setText(recipeStep.getDescription());
            }
            Log.d(LOG_TAG, "Video url is :" + recipeStep.getVideoURL());
            uri = Uri.parse(recipeStep.getVideoURL());
            initializePlayer();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtra("recipe_id", id);
            intent.putExtra("recipe_step", recipeStep);
            intent.putExtra("recipe_steps_count", count);
            intent.putExtra("recipe_step_position", position);
            startActivity(intent);
        }
    }

    private void initializePlayer() {

        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }

        if (playerView != null) {
            playerView.setPlayer(player);
        }

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
        Log.d(LOG_TAG, "TwoPane Initializing exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this,
                        LOG_TAG)))
                .createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isTwoPane && Util.SDK_INT > 23) {
            initializePlayer();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (isTwoPane) {
            if (Util.SDK_INT <= 23 || player == null) {
                initializePlayer();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTwoPane && Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isTwoPane && Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (isTwoPane && player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            Log.d(LOG_TAG, "TwoPane Releasing exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isTwoPane) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            outState.putBoolean("play_when_ready", playWhenReady);
            outState.putInt("current_window", currentWindow);
            outState.putLong("playback_position", playbackPosition);
            outState.putInt("recipe_step_position", currentPosition);
            Log.d(LOG_TAG, "TwoPane Saving exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
            Log.d(LOG_TAG, "TwoPane Saving steps position:::\t\t" + currentPosition);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        this.playWhenReady = playWhenReady;
    }
}
