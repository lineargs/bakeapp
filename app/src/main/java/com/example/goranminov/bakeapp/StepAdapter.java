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
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by goranminov on 22/05/2017.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private final Context mContext;
    private Cursor mCursor;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

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
        String stepDescription = mCursor.getString(StepFragment.INDEX_STEP_VIDEO);

        if (stepDescription.equals(null)) {
            holder.mPlayerView.setVisibility(View.INVISIBLE);
        } else {
            holder.initializePlayer(Uri.parse(stepDescription));
        }
        holder.mStepDescription.setText(mCursor.getString(StepFragment.INDEX_STEP_DESCRIPTION));
    }

    /*
     * Cache of the children views.
     */
    class StepViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.step_section_label)
        TextView mStepDescription;
        private SimpleExoPlayer mExoPlayer;
        private SimpleExoPlayerView mPlayerView;

        StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.step_player_view);
        }

        private void initializePlayer(Uri mediaUri) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(mContext),
                        trackSelector, loadControl);
                mPlayerView.setPlayer(mExoPlayer);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(mContext, "BakeApp");
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                        mContext, userAgent), new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
//                mExoPlayer.setPlayWhenReady(true);
            }
        }

        private void releasePlayer() {
            if (mExoPlayer != null) {
                mExoPlayer.stop();
                mExoPlayer.release();
                mExoPlayer = null;
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
