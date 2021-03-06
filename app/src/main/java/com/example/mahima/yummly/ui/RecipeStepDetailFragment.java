package com.example.mahima.yummly.ui;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mahima.yummly.R;
import com.example.mahima.yummly.model.Recipe;
import com.example.mahima.yummly.model.RecipeStep;
import com.example.mahima.yummly.utils.Utils;
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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static com.example.mahima.yummly.utils.Constants.LOG_TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeStepDetailFragment extends Fragment {

    @BindView(R.id.recipe_step_video)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.recipe_step_desc)
    TextView recipeStepDesc;
    @Nullable
    @BindView(R.id.prev_button)
    ImageButton prevButton;
    @Nullable
    @BindView(R.id.next_button)
    ImageButton nextButton;
    @BindView(R.id.empty_image_view)
    ImageView emptyImageView;
    @Nullable
    @BindView(R.id.recipe_step_count)
    TextView recipeStepCount;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Uri uri;
    private SimpleExoPlayer player;
    private int count;
    private int position;
    private int id;
    private RecipeStep recipeStep;


    public RecipeStepDetailFragment() {
        // Required empty public constructor
    }

    public static RecipeStepDetailFragment newInstance(int id, RecipeStep recipeStep, int position, int count) {
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        Bundle args = new Bundle();
        // save recipe details into fragment
        args.putInt("recipe_id", id);
        args.putParcelable("recipe_step", recipeStep);
        args.putInt("recipe_step_position", position);
        args.putInt("recipe_steps_count", count);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore saved state if any
        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean("play_when_ready");
            currentWindow = savedInstanceState.getInt("current_window");
            playbackPosition = savedInstanceState.getLong("playback_position");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        // retrieve recipe details saved as fragment arguments
        if (getArguments() != null) {
            id = getArguments().getInt("recipe_id");
            recipeStep = getArguments().getParcelable("recipe_step");
            count = getArguments().getInt("recipe_steps_count");
            position = getArguments().getInt("recipe_step_position");
        }

        // retrieve saved state if any
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt("recipe_id");
            position = savedInstanceState.getInt("recipe_step_position");
            if (playerView == null) {
                Recipe recipe = Utils.getRecipeById(getContext(), id);
                recipeStep = recipe.getSteps().get(position);
            }
        }
            displayRecipeDetails();
    }

    private void displayRecipeDetails() {

        // disable next and previous buttons for the last and first step video
        if (getContext() != null && nextButton != null && prevButton != null) {
            if (position == count - 1) {
                nextButton.setEnabled(false);
                DrawableCompat.setTint(nextButton.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            } else {
                nextButton.setEnabled(true);
                DrawableCompat.setTint(nextButton.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.white));
            }
            if (position == 0) {
                prevButton.setEnabled(false);
                DrawableCompat.setTint(prevButton.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            } else {
                prevButton.setEnabled(true);
                DrawableCompat.setTint(prevButton.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.white));
            }
        }

        if (getContext() != null && Utils.checkInternetConnection(getContext())) {
            Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        // display recipe description, current step count and total steps
        if (recipeStep != null) {
            if (recipeStepDesc != null) {
                recipeStepDesc.setText(recipeStep.getDescription());
                if (recipeStepCount != null) {
                    if (position == 0) {
                        recipeStepCount.setText(R.string.recipe_introduction);
                        if (prevButton != null) {
                            prevButton.setEnabled(false);
                        }
                    } else {
                        recipeStepCount.setText(getString(R.string.recipe_step_count, recipeStep.getId(), count - 1));
                    }
                }
            }

            // display a default image in case of an invalid video/thumbnail url
            if (recipeStep.getVideoURL() == null || TextUtils.isEmpty(recipeStep.getVideoURL())) {
                playerView.setVisibility(View.GONE);
                emptyImageView.setVisibility(View.VISIBLE);
                if (recipeStep.getThumbnailURL() != null &&
                        !TextUtils.isEmpty(recipeStep.getThumbnailURL()) &&
                        !recipeStep.getThumbnailURL().contains(".mp4")) {
                    emptyImageView.setImageURI(Uri.parse(recipeStep.getThumbnailURL()));
                } else {
                    if (getContext() != null) {
                        int[] imageIds = Utils.getRecipeImages(getContext());
                        Glide.with(getContext())
                                .load(imageIds[id - 1])
                                .into(emptyImageView);
                    }
                }
            } else {
                playerView.setVisibility(View.VISIBLE);
                emptyImageView.setVisibility(View.GONE);
            }

            uri = Uri.parse(recipeStep.getVideoURL());
        }
        initializePlayer();
    }

    @Optional
    @OnClick(R.id.prev_button)
    public void displayPreviousStep() {
        if (prevButton != null && nextButton != null) {
            if (position > 0 && position < count) {
                prevButton.setEnabled(true);

                position--;
                nextButton.setEnabled(true);
                DrawableCompat.setTint(nextButton.getDrawable(), ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.white));
                // disable previous button if reached the beginning
                if (position <= 0) {
                    prevButton.setEnabled(false);
                    DrawableCompat.setTint(prevButton.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                }
                // get new recipe step after decrementing the position
                Recipe recipe = Utils.getRecipeById(getContext(), id);
                recipeStep = recipe.getSteps().get(position);
                // reset video seek state
                currentWindow = 0;
                playbackPosition = 0;
                displayRecipeDetails();
            }
        }

    }

    @Optional
    @OnClick(R.id.next_button)
    public void displayNextStep() {
        if (nextButton != null && prevButton != null) {
            if (position >= 0 && position < count - 1) {
                nextButton.setEnabled(true);

                position++;
                prevButton.setEnabled(true);
                DrawableCompat.setTint(prevButton.getDrawable(), ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.white));
                // disable next button if reached the end
                if (position >= count - 1) {
                    nextButton.setEnabled(false);
                    DrawableCompat.setTint(nextButton.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                }
                // get new recipe step after incrementing the position
                Recipe recipe = Utils.getRecipeById(getContext(), id);
                recipeStep = recipe.getSteps().get(position);
                // reset video seek state
                currentWindow = 0;
                playbackPosition = 0;
                displayRecipeDetails();
            }
        }

    }

    private void initializePlayer() {

        // set step description as title for mobile screens
        if (getActivity() != null && recipeStep != null) {
            getActivity().setTitle(recipeStep.getShortDescription());
        }

        // initialize player
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(getContext(),
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }

        playerView.setPlayer(player);

        // define media source and set up the player
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(),
                        LOG_TAG)))
                .createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    // release player when no longer in use
    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();

            // save player state and current recipe step position
            outState.putBoolean("play_when_ready", playWhenReady);
            outState.putInt("current_window", currentWindow);
            outState.putLong("playback_position", playbackPosition);
            outState.putInt("recipe_id", id);
            outState.putInt("recipe_step_position", position);
        }
    }
}
