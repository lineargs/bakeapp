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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
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
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by goranminov on 22/05/2017.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    /**
     * Creates a StepAdapter.
     */
    public StepAdapter(@NonNull Context context) {
        mContext = context;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent   The ViewGroup that these ViewHolders are contained within.
     * @param viewType If the RecyclerView has more than one type of item.
     * @return A new MovieOverviewViewHolder that holds the View for each grid item
     */
    @Override
    public StepAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.step_item, parent, false);
        return new StepAdapter.StepViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(StepAdapter.StepViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mStepDescription.setText(mCursor.getString(StepFragment.INDEX_STEP_DESCRIPTION));
        holder.playerView.setDefaultArtwork(BitmapFactory.decodeResource
                (Resources.getSystem(), R.drawable.question_mark));
        Uri uri = Uri.parse(mCursor.getString(StepFragment.INDEX_STEP_VIDEO));
        holder.initializePlayer(uri);
    }

    /*
     * Cache of the children views.
     */
    class StepViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.step_player_view)
        SimpleExoPlayerView playerView;
        @BindView(R.id.step_section_label)
        TextView mStepDescription;
        private SimpleExoPlayer player;
        private ComponentListener componentListener;
        private boolean playWhenReady = true;
        private int currentWindow;
        private long playbackPosition;
        private final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

        StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void initializePlayer(Uri uri) {
            if (player == null) {
                TrackSelection.Factory adaptiveTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(mContext),
                        new DefaultTrackSelector(adaptiveTrackSelectionFactory),
                        new DefaultLoadControl());
//                player.addListener(componentListener);
                player.setVideoDebugListener(componentListener);
//                player.setAudioDebugListener(componentListener);
                playerView.setPlayer(player);
                player.setPlayWhenReady(playWhenReady);
                player.seekTo(currentWindow, playbackPosition);
            }
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
        }

        private void releasePlayer() {
            if (player != null) {
                playbackPosition = 0;
                currentWindow = 0;
                playWhenReady = player.getPlayWhenReady();
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
                    new DefaultHttpDataSourceFactory("ua"),
                    new DefaultExtractorsFactory(), null, null);
        }

        private class ComponentListener implements ExoPlayer.EventListener, VideoRendererEventListener, AudioRendererEventListener {
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

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

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

    /**
     * This method returns the number of items to display.
     *
     * @return The number of items available.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * This method is used to set the movie data on a MainAdapter if we've already
     * created one.
     *
     * @param newCursor The new movie data to be displayed.
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
