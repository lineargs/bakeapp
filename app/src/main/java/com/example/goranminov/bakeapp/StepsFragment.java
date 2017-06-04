/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.goranminov.bakeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A placeholder fragment containing a simple view.
 */

public class StepsFragment extends Fragment {

    @BindView(R.id.step_player_view)
    SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    @BindView(R.id.step_section_label)
    TextView stepDescription;
    @Nullable
    @BindView(R.id.steps_navigation_layout)
    RelativeLayout stepNavigation;
    @Nullable
    @BindView(R.id.fab_previous)
    FloatingActionButton fabPrevious;
    @Nullable
    @BindView(R.id.fab_next)
    FloatingActionButton fabNext;

    @Optional
    @OnClick(R.id.fab_next)
    void nextStep() {
        Intent intent = new Intent(getContext(), StepsActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(RECIPE_ID, recipeId);
        intent.putExtra(STEP_ID, ++stepId);
        intent.putExtra(TOTAL_STEPS, totalSteps);
        startActivity(intent);
    }

    @Optional
    @OnClick(R.id.fab_previous)
    void previousStep() {
        Intent intent = new Intent(getContext(), StepsActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(RECIPE_ID, recipeId);
        intent.putExtra(STEP_ID, --stepId);
        intent.putExtra(TOTAL_STEPS, totalSteps);
        startActivity(intent);
    }

    private long playbackPosition;
    private int currentWindow;

    private ComponentListener componentListener;

    private boolean playWhenReady = true;

    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();

    private static final String RECIPE_ID = "recipeId";
    private static final String STEP_ID = "stepId";
    private static final String TITLE = "title";
    private static final String TOTAL_STEPS = "total";
    private static final String VIDEO_LIST = "video_list";
    private static final String DESCRIPTION_LIST = "description_list";

    private List<String> videoIds, descriptionIds;
    private String title;
    private Integer stepId, totalSteps, recipeId;


    public StepsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            recipeId = savedInstanceState.getInt(RECIPE_ID);
            stepId = savedInstanceState.getInt(STEP_ID);
            videoIds = savedInstanceState.getStringArrayList(VIDEO_LIST);
            descriptionIds = savedInstanceState.getStringArrayList(DESCRIPTION_LIST);
            title = savedInstanceState.getString(TITLE);
        }

        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, rootView);
        getActivity().setTitle(title);
        componentListener = new ComponentListener();
        if (descriptionIds != null) {
            stepDescription.setText(descriptionIds.get(stepId));
        }

        if (stepId.equals(0)) {
            if (fabPrevious != null) {
                fabPrevious.setVisibility(View.INVISIBLE);
            }
        }

        if (stepId.equals(totalSteps)) {
            if (fabNext != null) {
                fabNext.setVisibility(View.INVISIBLE);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(RECIPE_ID, recipeId);
        outState.putInt(STEP_ID, stepId);
        outState.putStringArrayList(VIDEO_LIST, (ArrayList<String>) videoIds);
        outState.putStringArrayList(DESCRIPTION_LIST, (ArrayList<String>) descriptionIds);
        outState.putString(TITLE, title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
            stepDescription.setVisibility(View.GONE);
            if (stepNavigation != null) {
                stepNavigation.setVisibility(View.GONE);
            }
        } else {
            showSystemUi();
            stepDescription.setVisibility(View.VISIBLE);
            if (stepNavigation != null) {
                stepNavigation.setVisibility(View.VISIBLE);
            }
        }
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
        if ((Util.SDK_INT <= 23 || player == null)) {
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

    private void initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // let the factory create a player instance with default components
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

            player.addListener(componentListener);
            player.setVideoListener(componentListener);
            player.setVideoDebugListener(componentListener);
            player.setAudioDebugListener(componentListener);
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        if (videoIds != null) {
            MediaSource mediaSource = buildMediaSource(Uri.parse(videoIds.get(stepId)));
            player.prepare(mediaSource, true, false);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            currentWindow = player.getCurrentWindowIndex();
            player.removeListener(componentListener);
            player.setVideoListener(null);
            player.setVideoDebugListener(null);
            player.setAudioDebugListener(null);
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("BakeApp"),
                new DefaultExtractorsFactory(), null, null);
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

    @SuppressLint("InlinedApi")
    private void showSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public void setDescriptionIds(List<String> descriptionIds) {
        this.descriptionIds = descriptionIds;
    }


    public void setVideoIds(List<String> videoIds) {
        this.videoIds = videoIds;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    private class ComponentListener implements ExoPlayer.EventListener,
            VideoRendererEventListener,
            AudioRendererEventListener,
            SimpleExoPlayer.VideoListener {


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    break;
                case ExoPlayer.STATE_READY:
                    break;
                case ExoPlayer.STATE_ENDED:
                    player.seekTo(0);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    if (getView() != null) {
                        Snackbar.make(getView(), error.getSourceException().getMessage(),
                                Snackbar.LENGTH_LONG).show();
                        playerView.setDefaultArtwork(BitmapFactory.decodeResource
                                (getResources(), R.drawable.question_mark));
                    }
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    if (getView() != null) {
                        Snackbar.make(getView(), error.getRendererException().getMessage(),
                                Snackbar.LENGTH_LONG).show();
                        playerView.setDefaultArtwork(BitmapFactory.decodeResource
                                (getResources(), R.drawable.question_mark));
                    }
                    break;

                case ExoPlaybackException.TYPE_UNEXPECTED:
                    if (getView() != null) {
                        Snackbar.make(getView(), error.getUnexpectedException().getMessage(),
                                Snackbar.LENGTH_LONG).show();
                        playerView.setDefaultArtwork(BitmapFactory.decodeResource
                                (getResources(), R.drawable.question_mark));
                    }
                    break;
            }
        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame() {

        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }
    }
}
