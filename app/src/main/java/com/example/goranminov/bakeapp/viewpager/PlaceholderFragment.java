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

package com.example.goranminov.bakeapp.viewpager;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.goranminov.bakeapp.R;
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

/**
 * Created by goranminov on 22/05/2017.
 */
    /**
     *
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private SimpleExoPlayer mExoPlayer;
        private SimpleExoPlayerView mPlayerView;

        private static final String TAG = PlaceholderFragment.class.getSimpleName();
        private static final String ARG_DESCRIPTION = "description";
        private static final String ARG_VIDEO = "video";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String description, String mediaUri) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_DESCRIPTION, description);
            args.putString(ARG_VIDEO, mediaUri);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);
            initializePlayer(Uri.parse(getArguments().getString(ARG_VIDEO)));
            textView.setText(getArguments().getString(ARG_DESCRIPTION));
            return rootView;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mExoPlayer.release();
            mExoPlayer = null;
        }

        private void initializePlayer(Uri uri) {
            if (mExoPlayer == null) {
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                        trackSelector, loadControl);
                mPlayerView.setPlayer(mExoPlayer);
                MediaSource mediaSource = new ExtractorMediaSource(uri,
                        new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "BakeApp")),
                        new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
//                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }
