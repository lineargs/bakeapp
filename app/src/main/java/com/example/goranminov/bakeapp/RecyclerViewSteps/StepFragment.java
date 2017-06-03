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

package com.example.goranminov.bakeapp.RecyclerViewSteps;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.goranminov.bakeapp.R;
import com.example.goranminov.bakeapp.data.BakingContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class StepFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.step_recycler_view) RecyclerView mRecyclerView;
    private StepAdapter mStepAdapter;

    private static final int LOADER_ID = 29;
    private int oldOptions;

    public static final String[] STEP_PROJECTION = {
            BakingContract.RecipeSteps.COLUMN_DESCRIPTION,
            BakingContract.RecipeSteps.COLUMN_VIDEO,
    };

    public static final int INDEX_STEP_DESCRIPTION = 0;
    public static final int INDEX_STEP_VIDEO = 1;

    private Uri mUri;
    private int mCurrentItem;

    public StepFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);

            if (getActivity().getIntent().hasExtra("title") &&
                    getActivity().getIntent().getData() != null &&
                    getActivity().getIntent().hasExtra("step_id")) {
                getActivity().setTitle(getActivity().getIntent().getStringExtra("title"));
                mUri = getActivity().getIntent().getData();
                mCurrentItem = Integer.parseInt(getActivity().getIntent().getStringExtra("step_id"));
            } else {
                throw new NullPointerException("URI and title cannot be null");
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager
                    (getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            SnapHelper helper = new PagerSnapHelper();
            helper.attachToRecyclerView(mRecyclerView);
            mStepAdapter = new StepAdapter(getContext());
            mRecyclerView.setAdapter(mStepAdapter);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            oldOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            int newOptions = oldOptions;
            newOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            newOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            newOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            newOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            newOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getActivity().getWindow().getDecorView().setSystemUiVisibility(newOptions);
            ((StepActivity) getActivity()).getSupportActionBar().hide();
        }
        else
        {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(oldOptions);
            ((StepActivity) getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getContext(),
                        BakingContract.RecipeSteps.buildStepUriWithId
                                (Long.parseLong
                                        (mUri.getLastPathSegment())),
                        STEP_PROJECTION,
                        null,
                        null,
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
                mRecyclerView.smoothScrollToPosition(mCurrentItem);
                break;
            default:
                throw new RuntimeException("Loader not implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}