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

package com.example.goranminov.bakeapp.data;

/**
 * Created by goranminov on 14/05/2017.
 */

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class BakingContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.goranminov.bakeapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for BakeApp.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that BakeApp
     * can handle. For instance,
     *
     *     content://com.example.goranminov.bakeapp/recipe/
     *     [           BASE_CONTENT_URI             ][ PATH_RECIPE ]
     *
     * is a valid path for looking at recipe data.
     *
     *      content://com.example.goranminov.bakeapp/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot".
     */
    public static final String PATH_RECIPE = "recipe";


    /* Inner class that defines the table contents of the recipe table */
    public static final class RecipeEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the recipes table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .build();
        /* Used internally as the name of our recipes table. */
        public static final String TABLE_NAME = "recipes";

        /* The recipe ID as returned by API*/
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        /* The recipe name as returned by API*/
        public static final String COLUMN_NAME = "name";

        /**
         * Builds a URI that adds the recipe id to the end of the recipe content URI path.
         * This is used to query details about a single recipe entry by id. This is what we
         * use for the detail view query.
         *
         * @param id Recipe ID
         * @return Uri to query details about a single recipe entry
         */
        public static Uri buildRecipeUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
