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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.goranminov.bakeapp.data.BakingContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by goranminov on 14/05/2017.
 */

public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.main_recycler_view) RecyclerView mRecyclerView;
    private MainAdapter mMainAdapter;
    @BindView(R.id.loading_data_progress_bar)
    ProgressBar mLoadingData;

    private static final int RECIPE_LOADER_ID = 29;

    public static final String[] MAIN_PROJECTION = {
            BakingContract.RecipeEntry.COLUMN_NAME,
            BakingContract.RecipeEntry.COLUMN_RECIPE_ID,
            BakingContract.RecipeEntry.COLUMN_SERVINGS,
            BakingContract.RecipeEntry.COLUMN_IMAGE
    };

    public static final int INDEX_RECIPE_NAME = 0;
    public static final int INDEX_RECIPE_ID = 1;
    public static final int INDEX_RECIPE_SERVINGS = 2;
    public static final int INDEX_RECIPE_IMAGE = 3;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMainAdapter = new MainAdapter(getContext());
        mRecyclerView.setAdapter(mMainAdapter);
        showLoading();
        getLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);
        return rootView;
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns == 2) {
            return 1;
        }

        return nColumns;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case RECIPE_LOADER_ID:
                return new CursorLoader(getContext(),
                        BakingContract.RecipeEntry.CONTENT_URI,
                        MAIN_PROJECTION,
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
                case RECIPE_LOADER_ID:
                    mMainAdapter.swapCursor(data);
                    if (data != null && data.getCount() != 0) {
                        data.moveToFirst();
                    }
                    if (data.getCount() != 0) showData();
                    break;
                default:
                    throw new RuntimeException("Loader not implemented: " + loader.getId());
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
    }

    /*
     * Method used to set the loading indicator as invisible and the RecyclerView
     * as visible.
     */
    private void showData() {
        /* First, hide the loading indicator */
        mLoadingData.setVisibility(View.INVISIBLE);
        /* Finally, make sure the data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /*
     * Method used to set the loading indicator as visible and the RecyclerView
     * as invisible.
     */
    private void showLoading() {
        /* Then, hide the data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingData.setVisibility(View.VISIBLE);
    }
}
