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
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;

/**
 * Created by goranminov on 27/05/2017.
 */

public class ExoPlayerStepRecyclerView extends RecyclerView {

    private ExoPlayerView mExoPlayerView;

    private Context mContext;

    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;

    private int playPosition = -1;

    private boolean addedVideo = false;
    private View rowParent;

    public ExoPlayerStepRecyclerView(Context context) {
        super(context);
        initialize(context);
    }

    public ExoPlayerStepRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ExoPlayerStepRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    //play the video in the row
    public void playVideo() {
        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1;
        }

        if (startPosition < 0 || endPosition < 0) {
            return;
        }

        int targetPosition;
        if (startPosition != endPosition) {
            int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
            int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
            targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;

            Log.d("20672067Position", " startPosition:" + startPosition + " endPosition:" + endPosition);
            Log.d("20672067Position", " startPositionVideoHeight:" + startPositionVideoHeight + " endPositionVideoHeight:" + endPositionVideoHeight);
            Log.d("20672067Position", " startPosition != endPosition:" + " targetPosition:" + targetPosition);

        } else {
            targetPosition = startPosition;
            Log.d("20672067Position", " startPosition == endPosition:" + " targetPosition:" + targetPosition);
        }

        if (targetPosition < 0 || targetPosition == playPosition) {
            return;
        }
        playPosition = targetPosition;
        removeVideoView(mExoPlayerView);

        // get target View targetPosition in RecyclerView
        int at = targetPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(targetPosition);
        if (child == null) {
            return;
        }

        StepAdapter.StepViewHolder holder
                = (StepAdapter.StepViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }

        holder.mExoPlayerContainer.addView(mExoPlayerView);
        addedVideo = true;
        rowParent = holder.parent;

        mExoPlayerView.initializePlayer(mContext);

    }

    private void initialize(Context context) {

        mContext = context.getApplicationContext();

        mExoPlayerView = ExoPlayerView.getInstance(mContext);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                playVideo();
                Log.v("ScrollState", "Scroll State Changed");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (addedVideo && rowParent != null &&
                        rowParent.equals(view)) {
                    removeVideoView(mExoPlayerView);
                    playPosition = -1;
                }
            }
        });
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location01 = new int[2];
        child.getLocationInWindow(location01);

        if (location01[1] < 0) {
            return location01[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location01[1];
        }
    }

    //remove the player from the row
    private void removeVideoView(ExoPlayerView videoView) {

        ViewGroup parent = (ViewGroup) videoView.getParent();

        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            addedVideo = false;
        }
    }

    public void onPausePlayer() {
        if (mExoPlayerView != null) {
            removeVideoView(mExoPlayerView);
            mExoPlayerView.releasePlayer();
            mExoPlayerView = null;
        }
    }

    public void onRestartPlayer() {
        if (mExoPlayerView == null) {
            playPosition = -1;
            mExoPlayerView = ExoPlayerView.getInstance(mContext);
            playVideo();
        }
    }

    public void onRelease() {
        if (mExoPlayerView != null) {
            mExoPlayerView.releasePlayer();
            mExoPlayerView = null;
        }

        rowParent = null;
    }
}
