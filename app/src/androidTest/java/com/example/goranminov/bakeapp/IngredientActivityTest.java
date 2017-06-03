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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.goranminov.bakeapp.data.BakingContract;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by goranminov on 03/06/2017.
 */

@RunWith(AndroidJUnit4.class)
public class IngredientActivityTest {

    private static final Uri uri = BakingContract.RecipeIngredients
            .buildIngredientUriWithId
                    (2);
    private static final String INGREDIENT_TEXT = "5 units of large eggs";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested, IngredientActivity in this case, will be launched
     * before each test that's annotated with @Test and before methods annotated with @Before.
     *
     * The activity will be terminated after the test and methods annotated with @After are
     * complete. This rule allows you to directly access the activity during the test.
     */
    @Rule
    public ActivityTestRule<IngredientActivity> ingredientActivityTestRule =
            new ActivityTestRule<>(IngredientActivity.class, false, false);

    /**
     * Opens up the IngredientActivity, clicks on a RecyclerView item and checks it with the correct details.
     */
    @Test
    public void ingredientTestRule() {

        Intent intent = new Intent();
        intent.setData(uri);
        ingredientActivityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.ingredient_recycler_view))
                .check(matches(hasDescendant(withText(INGREDIENT_TEXT))));
    }
}
