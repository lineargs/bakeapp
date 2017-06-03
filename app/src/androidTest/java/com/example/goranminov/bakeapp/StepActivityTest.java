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

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by goranminov on 03/06/2017.
 */

@RunWith(AndroidJUnit4.class)
public class StepActivityTest {

    private static final String TITLE = "Yellow Cake";
    private static final String DESCRIPTION = "3. Lightly beat together the egg yolks, 1 tablespoon of vanilla, and 80 grams (1/3 cup) of the milk in a small bowl.";
    private static final String VIDEO = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffde36_2-mix-all-wet-ingredients-yellow-cake/2-mix-all-wet-ingredients-yellow-cake.mp4";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_VIDEO = "video";


    @Rule
    public IntentsTestRule<StepsActivity> stepsActivityIntentsTestRule =
            new IntentsTestRule<>(StepsActivity.class, false, false);

    /**
     * Opens up the StepActivity and checks if the Activity has the correct details.
     */
    @Test
    public void checkDescriptionTextView() {

        Intent intent = new Intent();
        intent.putExtra(KEY_TITLE, TITLE);
        intent.putExtra(KEY_DESCRIPTION, DESCRIPTION);
        intent.putExtra(KEY_VIDEO, VIDEO);
        stepsActivityIntentsTestRule.launchActivity(intent);

        onView(withId(R.id.step_section_label)).check(matches(withText(DESCRIPTION)));
    }
}
