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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.example.goranminov.bakeapp.viewpager.TabbedActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by goranminov on 14/05/2017.
 */

class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_INGREDIENT = 0;
    private final int VIEW_TYPE_STEP = 1;

    private final Context mContext;
    private Cursor ingredientCursor;
    private Cursor stepCursor;

    OnItemClickListener mCallback;

    interface OnItemClickListener {
        void onItemSelected(@Nullable Uri uri,
                            @Nullable String video,
                            @Nullable String description,
                            @Nullable String title);
    }

    /**
     * Creates a MainAdapter.
     */
    DetailAdapter(@NonNull Context context,
                  OnItemClickListener clickListener) {
        mContext = context;
        mCallback = clickListener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If the RecyclerView has more than one type of item.
     * @return A new MovieOverviewViewHolder that holds the View for each grid item
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_INGREDIENT:
                View ingredientView = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.detail_ingredient, parent, false);
                return new IngredientViewHolder(ingredientView);
            case VIEW_TYPE_STEP:
                View stepView = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.detail_step, parent, false);
                return new StepViewHolder(stepView);
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the
     * contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_INGREDIENT:
                final IngredientViewHolder ingredientViewHolder = (IngredientViewHolder) holder;
                ingredientViewHolder.bindViews(mContext);
                break;
            case VIEW_TYPE_STEP:
                final StepViewHolder stepViewHolder = (StepViewHolder) holder;
                stepViewHolder.bindViews(mContext, position - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_INGREDIENT;
        } else {
            return VIEW_TYPE_STEP;
        }
    }

    /*
         * Cache of the children views.
         */
    class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.detail_ingredient_text_view)
                TextView ingredientText;

        IngredientViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }
        void bindViews (Context context) {
            ingredientCursor.moveToPosition(getAdapterPosition());
            ingredientText.setText(R.string.detail_ingredients);
        }

        @Override
        public void onClick(View v) {
            ingredientCursor.moveToPosition(getAdapterPosition());

            Uri uri = BakingContract.RecipeIngredients
                    .buildIngredientUriWithId
                            (ingredientCursor.getLong(DetailFragment.INDEX_RECIPE_ID));
            mCallback.onItemSelected(uri, null, null, null);
        }
    }

    /*
     * Cache of the children views.
     */
    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.detail_step_text_view)
                TextView stepText;

        StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        void bindViews(Context context, int position) {
            stepCursor.moveToPosition(position);
            stepText.setText(stepCursor.getString(DetailFragment.INDEX_SHORT_DESCRIPTION));
        }

        @Override
        public void onClick(View v) {
            stepCursor.moveToPosition(getAdapterPosition() - 1);
            String title = stepCursor.getString(DetailFragment.INDEX_STEP_NAME);
            String description = stepCursor.getString(DetailFragment.INDEX_STEP_DESCRIPTION);
            String video = stepCursor.getString(DetailFragment.INDEX_STEP_VIDEO);
            mCallback.onItemSelected(null, video, description, title);
        }
    }

    /**
     * This method returns the number of items to display.
     *
     * @return The number of items available.
     */
    @Override
    public int getItemCount() {
        if (ingredientCursor == null || stepCursor == null) return 0;
        return 1 + stepCursor.getCount();
    }

    /**
     * This method is used to set the movie data on a MainAdapter if we've already
     * created one.
     *
     * @param newCursor The new movie data to be displayed.
     */
    public void swapIngredientCursor(Cursor newCursor) {
        ingredientCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * This method is used to set the movie data on a MainAdapter if we've already
     * created one.
     *
     * @param newCursor The new movie data to be displayed.
     */
    public void swapStepCursor(Cursor newCursor) {
        stepCursor = newCursor;
        notifyDataSetChanged();
    }
}
