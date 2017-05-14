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

import android.util.Log;

import com.example.goranminov.bakeapp.utils.retrofit.BakingRecipes;
import com.example.goranminov.bakeapp.utils.retrofit.RecipesAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by goranminov on 14/05/2017.
 */

public class BakingUtils {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    private static final String LOG_TAG = BakingUtils.class.getSimpleName();

    public static void getRecipes() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipesAPI recipesAPI = retrofit.create(RecipesAPI.class);

        Call<List<BakingRecipes>> call = recipesAPI.bakingRecipes("baking.json");

        call.enqueue(new Callback<List<BakingRecipes>>() {
            @Override
            public void onResponse(Call<List<BakingRecipes>> call, Response<List<BakingRecipes>> response) {
                List<BakingRecipes> bakingRecipesList = response.body();
                for (BakingRecipes bakingRecipes : bakingRecipesList) {
                    Log.v(LOG_TAG, String.valueOf(bakingRecipes.getId()));
                    Log.v(LOG_TAG, bakingRecipes.getName());
                }
            }

            @Override
            public void onFailure(Call<List<BakingRecipes>> call, Throwable t) {

            }
        });

    }
}
