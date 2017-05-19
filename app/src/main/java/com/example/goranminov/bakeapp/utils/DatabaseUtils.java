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

import android.content.ContentValues;

import com.example.goranminov.bakeapp.data.BakingContract;
import com.example.goranminov.bakeapp.utils.retrofit.BakingRecipes;
import com.example.goranminov.bakeapp.utils.retrofit.Ingredient;
import com.example.goranminov.bakeapp.utils.retrofit.Step;

/**
 * Created by goranminov on 19/05/2017.
 */

class DatabaseUtils {

    static ContentValues[] getRecipesContentValues (BakingRecipes bakingRecipes) {
        ContentValues[] recipeContentValues = new ContentValues[1];
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(BakingContract.RecipeEntry.COLUMN_RECIPE_ID, bakingRecipes.getId());
        recipeValues.put(BakingContract.RecipeEntry.COLUMN_NAME, bakingRecipes.getName());
        recipeValues.put(BakingContract.RecipeEntry.COLUMN_SERVINGS, bakingRecipes.getServings());
        if (bakingRecipes.getImage() != null) {
            recipeValues.put(BakingContract.RecipeEntry.COLUMN_IMAGE, bakingRecipes.getImage());
        }
        recipeContentValues[0] = recipeValues;
        return recipeContentValues;
    }

    static ContentValues[] getIngredientsValues (BakingRecipes bakingRecipes, Ingredient ingredient) {
        ContentValues[] ingredientsContentValues = new ContentValues[1];
        ContentValues ingredientsValues = new ContentValues();
        ingredientsValues.put(BakingContract.RecipeIngredients.COLUMN_RECIPE_ID, bakingRecipes.getId());
        ingredientsValues.put(BakingContract.RecipeIngredients.COLUMN_QUANTITY, ingredient.getQuantity());
        ingredientsValues.put(BakingContract.RecipeIngredients.COLUMN_MEASURE,
                BakingUtils.getMeasure(ingredient.getMeasure(), ingredient.getQuantity()));
        ingredientsValues.put(BakingContract.RecipeIngredients.COLUMN_INGREDIENT, ingredient.getIngredient());
        ingredientsContentValues[0] = ingredientsValues;
        return ingredientsContentValues;
    }

    static ContentValues[] getStepsValues (BakingRecipes bakingRecipes, Step step) {
        ContentValues[] stepsContentValues = new ContentValues[1];
        ContentValues stepsValues = new ContentValues();
        stepsValues.put(BakingContract.RecipeSteps.COLUMN_RECIPE_ID, bakingRecipes.getId());
        stepsValues.put(BakingContract.RecipeSteps.COLUMN_STEP_ID, step.getId());
        stepsValues.put(BakingContract.RecipeSteps.COLUMN_SHORT_DESCRIPTION, step.getShortDescription());
        stepsValues.put(BakingContract.RecipeSteps.COLUMN_DESCRIPTION, step.getDescription());
        if (step.getVideoURL() != null) {
            stepsValues.put(BakingContract.RecipeSteps.COLUMN_VIDEO, step.getVideoURL());
        }
        if (step.getThumbnailURL() != null) {
            stepsValues.put(BakingContract.RecipeSteps.COLUMN_THUMBNAIL, step.getThumbnailURL());
        }
        stepsContentValues[0] = stepsValues;
        return stepsContentValues;
    }
}
