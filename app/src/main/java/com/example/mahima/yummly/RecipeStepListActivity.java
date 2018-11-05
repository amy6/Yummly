package com.example.mahima.yummly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.LOG_TAG;

public class RecipeStepListActivity extends AppCompatActivity implements OnItemClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        ButterKnife.bind(this);

        isTwoPane = findViewById(R.id.recipe_step_detail_container) != null;

        Log.d(LOG_TAG, "Is this a two pane ? " + isTwoPane);

        if (getIntent() != null) {
            if (getIntent().hasExtra("recipe_id")) {
                int id = getIntent().getIntExtra("recipe_id", 0);

                recipe = Utils.getRecipeById(this, id);
                setTitle(recipe.getName());
                RecipeStepListFragment recipeStepListFragment = RecipeStepListFragment.newInstance(recipe);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipe_step_list_container, recipeStepListFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        List<RecipeStep> recipeSteps = recipe.getSteps();
        RecipeStep recipeStep = recipeSteps.get(position);
        if (isTwoPane) {
            recipeStepDesc.setText(recipeStep.getDescription());
            Log.d(LOG_TAG, "Video url is :" + recipeStep.getVideoURL());
            uri = Uri.parse(recipeStep.getVideoURL());
            initializePlayer();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtra("recipe_step", recipeStep);
            startActivity(intent);
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this,
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
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
            hideSystemUi();
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

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (isTwoPane && player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
