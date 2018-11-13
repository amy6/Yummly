package com.example.mahima.yummly.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahima.yummly.R;
import com.example.mahima.yummly.listener.OnItemClickListener;
import com.example.mahima.yummly.model.Recipe;
import com.example.mahima.yummly.model.RecipeStep;
import com.example.mahima.yummly.utils.Utils;
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

import static com.example.mahima.yummly.utils.Constants.LOG_TAG;

public class RecipeStepListActivity extends AppCompatActivity implements OnItemClickListener, Player.EventListener {

    @Nullable
    @BindView(R.id.recipe_step_video)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.recipe_step_desc)
    TextView recipeStepDesc;
    @Nullable
    @BindView(R.id.recipe_step_count)
    TextView recipeStepCount;
    @Nullable
    @BindView(R.id.empty_image_view)
    ImageView emptyImageView;

    private boolean isTwoPane;
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

        // restore saved state if any
        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean("play_when_ready");
            currentWindow = savedInstanceState.getInt("current_window");
            playbackPosition = savedInstanceState.getLong("playback_position");
            currentPosition = savedInstanceState.getInt("recipe_step_position");
        }

        // determine if we are displaying the details in a 2-pane mode
        isTwoPane = findViewById(R.id.recipe_step_detail_container) != null;

        if (getIntent() != null) {
            if (getIntent().hasExtra("recipe_id")) {
                id = getIntent().getIntExtra("recipe_id", 0);

                //fetch recipe details
                Recipe recipe = Utils.getRecipeById(this, id);
                setTitle(recipe.getName());

                recipeSteps = recipe.getSteps();
                count = recipeSteps.size();
                recipeStep = recipeSteps.get(currentPosition);

                // define field data to be displayed for 2-pane mode
                if (isTwoPane) {
                    uri = Uri.parse(recipeStep.getVideoURL());
                    if (recipeStepDesc != null) {
                        recipeStepDesc.setText(recipeStep.getShortDescription());
                    }
                    if (recipeStepCount != null && recipeStep.getId() > 0) {
                        recipeStepCount.setText(getString(R.string.recipe_step_count, recipeStep.getId(), count - 1));
                    }
                    initializePlayer();
                }

                if (getSupportFragmentManager().findFragmentById(R.id.recipe_step_list_container) == null) {
                    RecipeStepListFragment recipeStepListFragment = RecipeStepListFragment.newInstance(recipe, isTwoPane);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.recipe_step_list_container, recipeStepListFragment)
                            .commit();
                }

            }
        }

        // set up event listener for player to save player state
        if (player != null) {
            player.addListener(this);
        }
    }

    @Override
    public void onItemClick(int position) {
        // reset video seek state
        currentWindow = 0;
        playbackPosition = 0;
        currentPosition = position;
        recipeStep = recipeSteps.get(currentPosition);

        // display a default image in case of an invalid video/thumbnail url
        if (playerView != null && emptyImageView != null && recipeStep != null) {
            if (recipeStep.getVideoURL() == null || TextUtils.isEmpty(recipeStep.getVideoURL())) {
                playerView.setVisibility(View.GONE);
                emptyImageView.setVisibility(View.VISIBLE);
                if (recipeStep.getThumbnailURL() != null &&
                        !TextUtils.isEmpty(recipeStep.getThumbnailURL()) &&
                        !recipeStep.getThumbnailURL().contains(".mp4")) {
                    emptyImageView.setImageURI(Uri.parse(recipeStep.getThumbnailURL()));
                } else {
                    int[] imageIds = Utils.getRecipeImages(this);
                    Glide.with(this)
                            .load(imageIds[id - 1])
                            .into(emptyImageView);
                }
            } else {
                playerView.setVisibility(View.VISIBLE);
                emptyImageView.setVisibility(View.GONE);
            }
        }

        // display recipe description, current step count and total steps
        if (isTwoPane && recipeStep != null) {
            if (recipeStepDesc != null) {
                recipeStepDesc.setText(recipeStep.getDescription());
            }
            if (recipeStepCount != null && recipeStep.getId() > 0) {
                recipeStepCount.setText(getString(R.string.recipe_step_count, recipeStep.getId(), count - 1));
            }
            uri = Uri.parse(recipeStep.getVideoURL());
            initializePlayer();
        } else {
            // handle intent for mobile screens to display video in a new screen
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtra("recipe_id", id);
            intent.putExtra("recipe_step", recipeStep);
            intent.putExtra("recipe_steps_count", count);
            intent.putExtra("recipe_step_position", position);
            startActivity(intent);
        }
    }

    private void initializePlayer() {

        // initialize player
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }

        if (playerView != null) {
            playerView.setPlayer(player);
        }

        // define media source and set up the player
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

    // release player when no longer in use
    private void releasePlayer() {
        if (isTwoPane && player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
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

            // save player state
            outState.putBoolean("play_when_ready", playWhenReady);
            outState.putInt("current_window", currentWindow);
            outState.putLong("playback_position", playbackPosition);
            outState.putInt("recipe_step_position", currentPosition);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        this.playWhenReady = playWhenReady;
    }
}
