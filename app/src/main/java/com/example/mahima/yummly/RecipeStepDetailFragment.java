package com.example.mahima.yummly;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.LOG_TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeStepDetailFragment extends Fragment {

    @BindView(R.id.recipe_step_video)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.recipe_step_desc)
    TextView recipeStepDesc;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Uri uri;
    private SimpleExoPlayer player;


    public RecipeStepDetailFragment() {
        // Required empty public constructor
    }

    public static RecipeStepDetailFragment newInstance(RecipeStep recipeStep) {
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("recipe_step", recipeStep);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean("play_when_ready");
            currentWindow = savedInstanceState.getInt("current_window");
            playbackPosition = savedInstanceState.getLong("playback_position");
            Log.d(LOG_TAG, "Retrieving state of exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(LOG_TAG, "OnCreateView\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
        return inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            RecipeStep recipeStep = getArguments().getParcelable("recipe_step");
            if (recipeStep != null) {
                if (recipeStepDesc != null) {
                    recipeStepDesc.setText(recipeStep.getDescription());
                }
                uri = Uri.parse(recipeStep.getVideoURL());
            }
        }

        Log.d(LOG_TAG, "OnViewCreated\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
    }

    private void initializePlayer() {
        if (player == null){
            player = ExoPlayerFactory.newSimpleInstance(getContext(),
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }

        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
        Log.d(LOG_TAG, "Initializing exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
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
        Log.d(LOG_TAG, "Inside onstart\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
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

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            Log.d(LOG_TAG, "Releasing exoplayer\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
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
            Log.d(LOG_TAG, "Saved instance state\t\t" + playWhenReady + "\t" + currentWindow + "\t" + playbackPosition);
            outState.putBoolean("play_when_ready", playWhenReady);
            outState.putInt("current_window", currentWindow);
            outState.putLong("playback_position", playbackPosition);
        }
    }
}
