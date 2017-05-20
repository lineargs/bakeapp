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
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.goranminov.bakeapp.data.BakingContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.detail_recycler_view)
    RecyclerView mRecyclerView;
    private DetailAdapter mDetailAdapter;

    private static final int INGREDIENT_LOADER_ID = 101;
    private static final int STEP_LOADER_ID = 102;

    public static final String[] DETAIL_INGREDIENT_PROJECTION = {
            BakingContract.RecipeEntry.COLUMN_NAME,
            BakingContract.RecipeEntry.COLUMN_RECIPE_ID
    };

    public static final int INDEX_RECIPE_NAME = 0;
    public static final int INDEX_RECIPE_ID = 0;

    public static final String[] DETAIL_STEP_PROJECTION = {
            BakingContract.RecipeSteps.COLUMN_SHORT_DESCRIPTION
    };

    public static final int INDEX_SHORT_DESCRIPTION = 0;

    private Uri mRecipeUri;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity().getIntent().hasExtra("title")) {
            getActivity().setTitle(getActivity().getIntent().getStringExtra("title"));
        }

        if (getActivity().getIntent().getData() == null) {
            throw new NullPointerException("URI cannot be null");
        } else {
            mRecipeUri = getActivity().getIntent().getData();
            Log.v("Recipe Uri: ", mRecipeUri.toString());
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mDetailAdapter = new DetailAdapter(getContext());
        mRecyclerView.setAdapter(mDetailAdapter);
        getLoaderManager().initLoader(INGREDIENT_LOADER_ID, null, this);
        getLoaderManager().initLoader(STEP_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case INGREDIENT_LOADER_ID:
                return new CursorLoader(getContext(),
                        mRecipeUri,
                        DETAIL_INGREDIENT_PROJECTION,
                        null,
                        null,
                        null);
            case STEP_LOADER_ID:
                Uri uri = BakingContract.RecipeSteps.buildStepUriWithId(
                        Long.parseLong(mRecipeUri.getLastPathSegment()));
                return new CursorLoader(getContext(),
                        uri,
                        DETAIL_STEP_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case INGREDIENT_LOADER_ID:
                mDetailAdapter.swapIngredientCursor(data);
                break;
            case STEP_LOADER_ID:
                mDetailAdapter.swapStepCursor(data);
                break;
            default:
                throw new RuntimeException("loader not implemented: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
