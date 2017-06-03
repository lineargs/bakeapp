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
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.goranminov.bakeapp.data.BakingContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing RecyclerView.
 */
public class DetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DetailAdapter.OnItemClickListener {

    public static final String[] DETAIL_INGREDIENT_PROJECTION = {
            BakingContract.RecipeEntry.COLUMN_RECIPE_ID,
            BakingContract.RecipeEntry.COLUMN_NAME
    };
    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;
    public static final String[] DETAIL_STEP_PROJECTION = {
            BakingContract.RecipeSteps.COLUMN_VIDEO,
            BakingContract.RecipeSteps.COLUMN_SHORT_DESCRIPTION,
            BakingContract.RecipeSteps.COLUMN_DESCRIPTION,
            BakingContract.RecipeSteps.COLUMN_NAME
    };
    public static final int INDEX_STEP_VIDEO = 0;
    public static final int INDEX_SHORT_DESCRIPTION = 1;
    public static final int INDEX_STEP_DESCRIPTION = 2;
    public static final int INDEX_STEP_NAME = 3;
    private static final int INGREDIENT_LOADER_ID = 101;
    private static final int STEP_LOADER_ID = 102;
    @BindView(R.id.detail_recycler_view)
    RecyclerView mRecyclerView;
    private DetailAdapter mDetailAdapter;
    private Uri mRecipeUri;
    private boolean mDualPane;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);


        if (getActivity().getIntent().hasExtra("title") &&
                getActivity().getIntent().getData() != null) {
            getActivity().setTitle(getActivity().getIntent().getStringExtra("title"));
            mRecipeUri = getActivity().getIntent().getData();
        } else {
            throw new NullPointerException("URI and title cannot be null");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mDetailAdapter = new DetailAdapter(getContext(), this);
        mRecyclerView.setAdapter(mDetailAdapter);
        getLoaderManager().initLoader(INGREDIENT_LOADER_ID, null, this);
        getLoaderManager().initLoader(STEP_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View detailsFrame = getActivity().findViewById(R.id.step_container);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
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

    @Override
    public void onItemSelected(@Nullable Uri uri, @Nullable String video, @Nullable String description, @Nullable String title) {

        if (mDualPane) {

            if (uri != null) {
                IngredientFragment ingredientFragment = new IngredientFragment();
                ingredientFragment.setmUri(uri);
                getFragmentManager().beginTransaction()
                        .replace(R.id.step_container, ingredientFragment)
                        .commit();
            }

            if (video != null && description != null && title != null) {
                StepsFragment stepsFragment = new StepsFragment();
                stepsFragment.setDescription(description);
                stepsFragment.setTitle(title);
                stepsFragment.setVideo(video);
                getFragmentManager().beginTransaction()
                        .replace(R.id.step_container, stepsFragment)
                        .commit();
            }

        } else {

            if (uri != null) {
                Intent intent = new Intent(getContext(), IngredientActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }

            if (video != null && description != null && title != null) {
                Intent intent = new Intent(getContext(), StepsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("video", video);
                startActivity(intent);
            }
        }
    }
}
