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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.example.goranminov.bakeapp.utils.retrofit.BakingRecipes;
import com.example.goranminov.bakeapp.utils.retrofit.Ingredient;
import com.example.goranminov.bakeapp.utils.retrofit.RecipesAPI;
import com.example.goranminov.bakeapp.utils.retrofit.Step;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by goranminov on 14/05/2017.
 */

public class RetrofitCall {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static void getRecipes(final Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipesAPI recipesAPI = retrofit.create(RecipesAPI.class);

        Call<List<BakingRecipes>> call = recipesAPI.bakingRecipes("baking.json");

        call.enqueue(new Callback<List<BakingRecipes>>() {
            @Override
            public void onResponse(Call<List<BakingRecipes>> call, Response<List<BakingRecipes>> response) {

                if (response.isSuccessful()) {

                    ContentResolver contentResolver = context.getContentResolver();
                    List<BakingRecipes> bakingRecipesList = response.body();

                    for (BakingRecipes bakingRecipes : bakingRecipesList) {
                        List<Ingredient> ingredientList = bakingRecipes.getIngredients();

                        contentResolver.delete(BakingContract.RecipeIngredients.buildIngredientUriWithId(bakingRecipes.getId()),
                                null,
                                new String[]{String.valueOf(bakingRecipes.getId())});

                        contentResolver.delete(BakingContract.RecipeSteps.buildStepUriWithId(bakingRecipes.getId()),
                                null,
                                new String[]{String.valueOf(bakingRecipes.getId())});

                        for (Ingredient ingredient : ingredientList) {
                            ContentValues[] ingredientsValues = DatabaseUtils.getIngredientsValues(bakingRecipes, ingredient);

                            if (ingredientsValues != null && ingredientsValues.length != 0) {

                                contentResolver.bulkInsert(BakingContract.RecipeIngredients.CONTENT_URI,
                                        ingredientsValues);
                            }
                        }
                        List<Step> stepList = bakingRecipes.getSteps();
                        for (Step step : stepList) {
                            ContentValues[] stepValues = DatabaseUtils.getStepsValues(bakingRecipes, step);

                            if (stepValues != null && stepValues.length != 0) {
                                contentResolver.bulkInsert(BakingContract.RecipeSteps.CONTENT_URI,
                                        stepValues);
                            }
                        }
                        ContentValues[] recipeValues = DatabaseUtils.getRecipesContentValues(bakingRecipes);
                        if (recipeValues != null && recipeValues.length != 0) {

                            contentResolver.bulkInsert(BakingContract.RecipeEntry.CONTENT_URI,
                                    recipeValues);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BakingRecipes>> call, Throwable t) {
                Log.e("Failure ", t.getMessage());
            }
        });
    }
}
