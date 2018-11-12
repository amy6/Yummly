package com.example.mahima.yummly;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public final class Constants {

    public static final String LOG_TAG = RecipeListActivity.class.getSimpleName();

    public static final Type RECIPE_TYPE = new TypeToken<List<Recipe>>() {}.getType();

    public static final int VERTICAL_LINEAR_LAYOUT = 0;
    public static final int HORIZONTAL_LINEAR_LAYOUT = 1;
    public static final int GRID_LAYOUT = 2;
    public static final int STAGGERRED_GRID_LAYOUT = 3;
}
