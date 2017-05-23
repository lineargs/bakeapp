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

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class StepFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.step_recycler_view) RecyclerView mRecyclerView;
    private StepAdapter mStepAdapter;

    private static final int LOADER_ID = 29;

    public static final String[] STEP_PROJECTION = {
            BakingContract.RecipeSteps.COLUMN_DESCRIPTION,
            BakingContract.RecipeSteps.COLUMN_VIDEO,
    };

    public static final int INDEX_STEP_DESCRIPTION = 0;
    public static final int INDEX_STEP_VIDEO = 1;

    private Uri mUri;
    private int mStepId;

    public StepFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity().getIntent().hasExtra("title") &&
                getActivity().getIntent().hasExtra("step_id")) {
            mStepId = Integer.parseInt(getActivity().getIntent().getStringExtra("step_id"));
            Log.v("StepId ", getActivity().getIntent().getStringExtra("step_id"));
            getActivity().setTitle(getActivity().getIntent().getStringExtra("title"));
        }

        if (getActivity().getIntent().getData() != null) {
            mUri = getActivity().getIntent().getData();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mStepAdapter = new StepAdapter(getContext());
        mRecyclerView.setAdapter(mStepAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOADER_ID:
                String recipeId = mUri.getLastPathSegment();
                String stepId = String.valueOf(mStepId);
                Log.v("String stepId", stepId);
                String[] selectionArguments = new String[]{stepId, recipeId};
                return new CursorLoader(getContext(),
                        BakingContract.RecipeSteps.CONTENT_URI,
                        STEP_PROJECTION,
                        BakingContract.RecipeSteps.COLUMN_STEP_ID + " = ? AND " +
                                BakingContract.RecipeSteps.COLUMN_RECIPE_ID + " = ? ",
                        selectionArguments,
                        null);
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                mStepAdapter.swapCursor(data);
                if (data != null && data.getCount() != 0) {
                    data.moveToFirst();
                }
                break;
            default:
                throw new RuntimeException("Loader not implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStepAdapter.swapCursor(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
