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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goranminov.bakeapp.data.BakingContract;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by goranminov on 14/05/2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MovieAdapterViewHolder>{


    private final Context mContext;
    private Cursor mCursor;

    /**
     * Creates a MainAdapter.
     */
    public MainAdapter(@NonNull Context context) {
        mContext = context;
    }

    /*
     * Cache of the children views.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.recipe_name_card_view)
        TextView recipeName;
        @BindView(R.id.recipe_image_card_view)
        ImageView recipeImage;
        @BindView(R.id.recipe_servings_card_view)
        TextView recipeServings;

        public MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
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
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.main_card_layout, parent, false);
        return new MovieAdapterViewHolder(view);
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
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.recipeImage.setImageResource(R.drawable.baking);
        holder.recipeName.setText(mCursor.getString(MainFragment.INDEX_RECIPE_NAME));
        holder.recipeServings.setText(R.string.resipe_servings);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(holder.getAdapterPosition());
                Intent intent = new Intent(mContext, DetailActivity.class);
                Uri uri = BakingContract.RecipeEntry.buildRecipeUriWithId(mCursor.getLong(MainFragment.INDEX_RECIPE_ID));
                intent.setData(uri);
                intent.putExtra("title", mCursor.getString(MainFragment.INDEX_RECIPE_NAME));
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * This method returns the number of items to display.
     *
     * @return The number of items available.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * This method is used to set the movie data on a MainAdapter if we've already
     * created one.
     *
     * @param newCursor The new movie data to be displayed.
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
