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

package com.example.goranminov.bakeapp.utils;

import android.content.Context;
import android.database.Cursor;

import com.example.goranminov.bakeapp.data.BakingContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goranminov on 19/05/2017.
 */

public class BakingUtils {

    static String getMeasure(String measure) {
        switch (measure) {
            case "CUP":
                return "cups";
            case "TBLSP":
                return "tablespoons";
            case "TSP":
                return "teaspoons";
            case "K":
                return "kilos";
            case "G":
                return "grams";
            case "OZ":
                return "ounces";
            case "UNIT":
                return "units";
            default:
                return null;
        }
    }

    public static String getIngredientString(String quantity, String measure, String ingredient) {
        return quantity + " " + measure + " of " + ingredient;
    }

    public static List<String> getStepsVideo (Context context, long recipeId) {
        List<String> video = new ArrayList<String>();
        final String[] PROJECTION = {
                BakingContract.RecipeSteps.COLUMN_VIDEO
        };

        final int INDEX_VIDEO = 0;

        Cursor cursor = context.getContentResolver().query
                (BakingContract.RecipeSteps.buildStepUriWithId(recipeId),
                        PROJECTION,
                        null,
                        null,
                        null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                video.add(cursor.getString(INDEX_VIDEO));
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return video;
    }

    public static List<String> getStepsDescription (Context context, long recipeId) {
        List<String> description = new ArrayList<String>();
        final String[] PROJECTION = {
                BakingContract.RecipeSteps.COLUMN_DESCRIPTION
        };

        final int INDEX_DESCRIPTION = 0;

        Cursor cursor = context.getContentResolver().query
                (BakingContract.RecipeSteps.buildStepUriWithId(recipeId),
                        PROJECTION,
                        null,
                        null,
                        null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                description.add(cursor.getString(INDEX_DESCRIPTION));
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return description;
    }

    public static List<String> getStepsThumbnail (Context context, long recipeId) {
        List<String> thumbnail = new ArrayList<String>();
        final String[] PROJECTION = {
                BakingContract.RecipeSteps.COLUMN_THUMBNAIL
        };

        final int INDEX_THUMBNAIL = 0;

        Cursor cursor = context.getContentResolver().query
                (BakingContract.RecipeSteps.buildStepUriWithId(recipeId),
                        PROJECTION,
                        null,
                        null,
                        null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                thumbnail.add(cursor.getString(INDEX_THUMBNAIL));
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return thumbnail;
    }
}
