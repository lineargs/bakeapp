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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by goranminov on 14/05/2017.
 */

/**
 * This class serves as the ContentProvider for all of BakingRecipe's data. This class allows us to
 * bulkInsert data, query data, and delete data.
 * <p>
 * Although ContentProvider implementation requires the implementation of additional methods to
 * perform single inserts, updates, and the ability to get the type of the data from a URI.
 * However, here, they are not implemented.
 */
public class BakingProvider extends ContentProvider {

    /*
     * These constants will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */

    public static final int CODE_RECIPES = 100;
    public static final int CODE_RECIPES_WITH_ID = 101;
    public static final int CODE_INGREDIENTS = 200;
    public static final int CODE_INGREDIENTS_WITH_ID = 201;
    public static final int CODE_STEPS = 300;
    public static final int CODE_STEPS_WITH_ID = 301;

    /*
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of BakingProvider and is a
     * common convention in Android programming.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BakingDbHelper mBakingDbHelper;

    /**
     * Creates the UriMatcher that will match each URI to the constants defined above.
     * UriMatcher does all the hard work for you. You just have to tell it which code to match
     * with which URI, and it does the rest automatically.
     *
     * @return A UriMatcher that correctly matches the constants above
     */
    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BakingContract.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you know
         * they aren't going to change.
         */

        /* This URI is content://com.example.goranminov.bakeapp/recipe/ */
        matcher.addURI(authority, BakingContract.PATH_RECIPE, CODE_RECIPES);

        /*
         * This URI would look something like content://com.example.goranminov.bakeapp/recipe/123567
         * The "/#" signifies to the UriMatcher that if PATH_RECIPE is followed by ANY number,
         * that it should return the CODE_RECIPES_WITH_ID code
         */
        matcher.addURI(authority, BakingContract.PATH_RECIPE + "/#", CODE_RECIPES_WITH_ID);

        /* This URI is content://com.example.goranminov.bakeapp/ingredients/ */
        matcher.addURI(authority, BakingContract.PATH_INGREDIENTS, CODE_INGREDIENTS);

        /*
         * This URI would look something like content://com.example.goranminov.bakeapp/ingredients/123567
         * The "/#" signifies to the UriMatcher that if PATH_INGREDIENTS is followed by ANY number,
         * that it should return the CODE_RECIPES_WITH_ID code
         */
        matcher.addURI(authority, BakingContract.PATH_INGREDIENTS + "/#", CODE_INGREDIENTS_WITH_ID);

        /* This URI is content://com.example.goranminov.bakeapp/steps/ */
        matcher.addURI(authority, BakingContract.PATH_STEPS, CODE_STEPS);

        /*
         * This URI would look something like content://com.example.goranminov.bakeapp/steps/123567
         * The "/#" signifies to the UriMatcher that if PATH_INGREDIENTS is followed by ANY number,
         * that it should return the CODE_STEPS_WITH_ID code
         */
        matcher.addURI(authority, BakingContract.PATH_STEPS + "/#", CODE_STEPS_WITH_ID);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     * <p>
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     * <p>
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {

        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since BakingDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mBakingDbHelper = new BakingDbHelper(getContext());
        return true;
    }

    /**
     * Handles query requests from clients. We will use this method in BakeApp to query for all
     * of our recipe data as well as to query for a particular recipe ID.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        /* The returning cursor*/
        Cursor retCursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.example.goranminov.bakeapp/recipe/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the recipes in our recipe table.
             *
             * In this case, we want to return a cursor that contains every row of recipe data
             * in our recipe table.
             */
            case CODE_RECIPES:
                retCursor = mBakingDbHelper.getReadableDatabase().query(

                        /* Table we are going to query */
                        BakingContract.RecipeEntry.TABLE_NAME,

                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.example.goranminov.bakeapp/recipe/123567
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the recipe for a particular recipe ID. The ID in this code is at the very
             * end of the URI (123567) and can be accessed programmatically using Uri's getLastPathSegment method.
             *
             * In this case, we want to return a cursor that contains one row of recipe data for
             * a particular recipe ID.
             */
            case CODE_RECIPES_WITH_ID:

                /*
                 * In order to determine the recipe ID associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 123567 and
                 * represents the recipe ID.
                 */
                String idRecipe = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionArguments = new String[]{idRecipe};
                retCursor = mBakingDbHelper.getReadableDatabase().query(

                        /* Table we are going to query */
                        BakingContract.RecipeEntry.TABLE_NAME,

                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,

                        /*
                         * The URI that matches CODE_RECIPES_WITH_ID contains an ID at the end
                         * of it. We extract that ID and use it with these next two lines to
                         * specify the row of recipe we want returned in the cursor. We use a
                         * question mark here and then designate selectionArguments as the next
                         * argument for performance reasons. Whatever Strings are contained
                         * within the selectionArguments array will be inserted into the
                         * selection statement by SQLite under the hood.
                         */
                        BakingContract.RecipeEntry.COLUMN_RECIPE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_INGREDIENTS:
                retCursor = mBakingDbHelper.getReadableDatabase().query(

                        /* Table we are going to query */
                        BakingContract.RecipeIngredients.TABLE_NAME,

                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_INGREDIENTS_WITH_ID:

                /*
                 * In order to determine the recipe ID associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 123567 and
                 * represents the recipe ID.
                 */
                String idIngredient = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionIngredient = new String[]{idIngredient};
                retCursor = mBakingDbHelper.getReadableDatabase().query(

                        /* Table we are going to query */
                        BakingContract.RecipeIngredients.TABLE_NAME,

                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,

                        /*
                         * The URI that matches CODE_RECIPES_WITH_ID contains an ID at the end
                         * of it. We extract that ID and use it with these next two lines to
                         * specify the row of recipe we want returned in the cursor. We use a
                         * question mark here and then designate selectionArguments as the next
                         * argument for performance reasons. Whatever Strings are contained
                         * within the selectionArguments array will be inserted into the
                         * selection statement by SQLite under the hood.
                         */
                        BakingContract.RecipeIngredients.COLUMN_RECIPE_ID + " = ? ",
                        selectionIngredient,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STEPS:
                retCursor = mBakingDbHelper.getReadableDatabase().query(

                        /* Table we are going to query */
                        BakingContract.RecipeSteps.TABLE_NAME,

                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STEPS_WITH_ID:

                /*
                 * In order to determine the recipe ID associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 123567 and
                 * represents the recipe ID.
                 */
                String idSteps = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionSteps = new String[]{idSteps};
                retCursor = mBakingDbHelper.getReadableDatabase().query(

                        /* Table we are going to query */
                        BakingContract.RecipeSteps.TABLE_NAME,

                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,

                        /*
                         * The URI that matches CODE_RECIPES_WITH_ID contains an ID at the end
                         * of it. We extract that ID and use it with these next two lines to
                         * specify the row of recipe we want returned in the cursor. We use a
                         * question mark here and then designate selectionArguments as the next
                         * argument for performance reasons. Whatever Strings are contained
                         * within the selectionArguments array will be inserted into the
                         * selection statement by SQLite under the hood.
                         */
                        BakingContract.RecipeSteps.COLUMN_RECIPE_ID + " = ? ",
                        selectionSteps,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType.");
    }

    /**
     * Handles requests to insert a set of new rows. In BakeApp, we are going to be
     * inserting multiple rows of data at a time from a json.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mBakingDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BakingContract.RecipeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case CODE_INGREDIENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BakingContract.RecipeIngredients.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case CODE_STEPS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BakingContract.RecipeSteps.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException("We are not implementing insert, use bulkInsert instead.");
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                numRowsDeleted = mBakingDbHelper.getWritableDatabase().delete(
                        BakingContract.RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_RECIPES_WITH_ID:
                numRowsDeleted = mBakingDbHelper.getWritableDatabase().delete(
                        BakingContract.RecipeEntry.TABLE_NAME,
                        BakingContract.RecipeEntry.COLUMN_RECIPE_ID + " = ? ",
                        selectionArgs);
                break;
            case CODE_INGREDIENTS:
                numRowsDeleted = mBakingDbHelper.getWritableDatabase().delete(
                        BakingContract.RecipeIngredients.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_INGREDIENTS_WITH_ID:
                numRowsDeleted = mBakingDbHelper.getWritableDatabase().delete(
                        BakingContract.RecipeIngredients.TABLE_NAME,
                        BakingContract.RecipeIngredients.COLUMN_RECIPE_ID + " = ? ",
                        selectionArgs);
                break;
            case CODE_STEPS:
                numRowsDeleted = mBakingDbHelper.getWritableDatabase().delete(
                        BakingContract.RecipeSteps.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_STEPS_WITH_ID:
                numRowsDeleted = mBakingDbHelper.getWritableDatabase().delete(
                        BakingContract.RecipeSteps.TABLE_NAME,
                        BakingContract.RecipeSteps.COLUMN_RECIPE_ID + " = ? ",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update.");
    }
}
