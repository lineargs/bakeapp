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

/**
 * Created by goranminov on 19/05/2017.
 */

public class BakingUtils {

    public static String getMeasure(String measure, double quantity) {

        if (measure.equals("CUP")) {

            if (quantity != 1) return "cups";
            else return "cup";
        } else if (measure.equals("TBLSP")) {
            if (quantity != 1) return "tablespoons";
            else return "tablespoon";
        } else if (measure.equals("TSP")) {
            if (quantity != 1) return "teaspoons";
            else return "teaspoon";
        } else if (measure.equals("K")) {
            if (quantity != 1) return "kilos";
            else return "kilo";
        } else if (measure.equals("G")) {
            if (quantity != 1) return "grams";
            else return "gram";
        } else if (measure.equals("OZ")) {
            if (quantity != 1) return "ounces";
            else return "ounce";
        } else {
            if (quantity != 1) return "units";
            else return "unit";
        }
    }

    public static String getIngredientString(String quantity, String measure, String ingredient) {
        return quantity + " " + measure + " of " + ingredient;
    }
}
