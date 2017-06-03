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
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.example.goranminov.bakeapp.utils.BakingUtils;

/**
 * Created by goranminov on 03/06/2017.
 */

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        Cursor mCursor;

        final String[] INGREDIENT_PROJECTION = {
                BakingContract.RecipeIngredients.COLUMN_QUANTITY,
                BakingContract.RecipeIngredients.COLUMN_INGREDIENT,
                BakingContract.RecipeIngredients.COLUMN_MEASURE
        };

        final int INDEX_QUANTITY = 0;
        final int INDEX_INGREDIENT = 1;
        final int INDEX_MEASURE = 2;

        ListRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        //called on start
        @Override
        public void onDataSetChanged() {

            if (mCursor != null) mCursor.close();
            mCursor = mContext.getContentResolver().query(
                    BakingContract.RecipeIngredients
                            .buildIngredientUriWithId(1),
                    INGREDIENT_PROJECTION,
                    null,
                    null,
                    null);
        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        /**
         * This method acts like the onBindViewHolder method in an Adapter
         *
         * @param position The current position of the item in the ListView to be displayed
         * @return The RemoteViews object to display for the provided postion
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor == null || mCursor.getCount() == 0) return null;
            mCursor.moveToPosition(position);

            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.ingredient_widget);
            views.setTextViewText(R.id.widget_ingredient,
                    BakingUtils.getIngredientString
                            (mCursor.getString(INDEX_QUANTITY),
                                    mCursor.getString(INDEX_MEASURE),
                                    mCursor.getString(INDEX_INGREDIENT)));

            Intent fillInIntent = new Intent();
            fillInIntent.setData(BakingContract.RecipeIngredients
                    .buildIngredientUriWithId(1));
            views.setOnClickFillInIntent(R.id.widget_ingredient, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
