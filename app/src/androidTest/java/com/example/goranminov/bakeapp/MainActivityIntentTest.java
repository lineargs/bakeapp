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
import android.support.test.espresso.intent.rule.IntentsTestRule;
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
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by goranminov on 03/06/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    private static final Uri uri = BakingContract.RecipeEntry.buildRecipeUriWithId(4);
    private static final String RECIPE_NAME = "Cheesecake";
    private static final String KEY_TITLE = "title";

    @Rule
    public IntentsTestRule<MainActivity> mainActivityActivityTestRule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Test
    public void onClickCheesecakeInRecyclerView() {

        onView(ViewMatchers.withId(R.id.main_recycler_view))
                .perform(RecyclerViewActions.actionOnItem
                        (hasDescendant(withText(RECIPE_NAME)), click()));

        intended(allOf
                (hasExtra(KEY_TITLE, RECIPE_NAME),
                hasData(uri)));
    }
}
