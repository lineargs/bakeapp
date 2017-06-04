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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.goranminov.bakeapp.utils.BakingUtils;

import java.util.List;

public class StepsActivity extends AppCompatActivity {

    private static final String RECIPE_ID = "recipeId";
    private static final String STEP_ID = "stepId";
    private static final String TITLE = "title";
    private static final String TOTAL_STEPS = "total";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_steps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            StepsFragment stepsFragment = new StepsFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();

            Integer stepId = getIntent().getIntExtra(STEP_ID, 0);
            stepsFragment.setStepId(stepId);

            Integer recipeId = getIntent().getIntExtra(RECIPE_ID, 0);
            stepsFragment.setRecipeId(recipeId);
            stepsFragment.setVideoIds(BakingUtils.getStepsVideo
                    (this, recipeId));
            stepsFragment.setDescriptionIds(BakingUtils.getStepsDescription
                    (this, recipeId));
            String title = getIntent().getStringExtra(TITLE);
            stepsFragment.setTitle(title);

            Integer totalSteps = getIntent().getIntExtra(TOTAL_STEPS, 0);
            stepsFragment.setTotalSteps(totalSteps);

            fragmentManager.beginTransaction()
                    .add(R.id.steps_container, stepsFragment)
                    .commit();
        }
    }

}
