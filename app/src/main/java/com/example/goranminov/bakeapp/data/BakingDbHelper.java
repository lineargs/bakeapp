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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by goranminov on 14/05/2017.
 */

public class BakingDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "baking.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 3;

    public BakingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our recipe data.
         */
        final String SQL_CREATE_RECIPE_TABLE =

                "CREATE TABLE " + BakingContract.RecipeEntry.TABLE_NAME + " (" +

                        /*
                         * RecipeEntry did not explicitly declare a column called "_ID". However,
                         * RecipeEntry implements the interface "BaseColumns", which does have a field
                         * named "_ID". We use that here to designate our table's primary key.
                         */
                        BakingContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        BakingContract.RecipeEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +

                        BakingContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                        /*
                         * To ensure this table can only contain one recipe entry per id, we declare
                         * the recipe_id column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                         * SQLite that if we have a recipe entry with a certain recipe_id and we attempt to
                         * insert another recipe entry with that recipe_id, we replace the old movie entry.
                         */
                        "UNIQUE (" + BakingContract.RecipeEntry.COLUMN_RECIPE_ID + ") ON CONFLICT REPLACE);";

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our ingredients data.
         */
        final String SQL_CREATE_INGREDIENTS_TABLE =

                "CREATE TABLE " + BakingContract.RecipeIngredients.TABLE_NAME + " (" +

                        BakingContract.RecipeIngredients._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        BakingContract.RecipeIngredients.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +

                        BakingContract.RecipeIngredients.COLUMN_QUANTITY + " REAL NOT NULL, " +

                        BakingContract.RecipeIngredients.COLUMN_MEASURE + " TEXT NOT NULL, " +

                        BakingContract.RecipeIngredients.COLUMN_INGREDIENT + " TEXT NOT NULL);";

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our steps data.
         */
        final String SQL_CREATE_STEPS_TABLE =

                "CREATE TABLE " + BakingContract.RecipeSteps.TABLE_NAME + " (" +

                        BakingContract.RecipeSteps._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        BakingContract.RecipeSteps.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +

                        BakingContract.RecipeSteps.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +

                        BakingContract.RecipeSteps.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +

                        BakingContract.RecipeSteps.COLUMN_VIDEO + " TEXT, " +

                        BakingContract.RecipeSteps.COLUMN_THUMBNAIL + " TEXT);";


        /*
         * After we've spelled out our SQLite table creation statements above, we actually execute
         * that SQLs with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        db.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    /**
     * The recipe table is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param db Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BakingContract.RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BakingContract.RecipeIngredients.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BakingContract.RecipeSteps.TABLE_NAME);
        onCreate(db);
    }
}

