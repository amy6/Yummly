package com.example.mahima.yummly;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public final class Constants {

    public static final String LOG_TAG = RecipeListActivity.class.getSimpleName();

    public static final Type RECIPE_TYPE = new TypeToken<List<Recipe>>() {}.getType();

    public static final int VERTICAL_LINEAR_LAYOUT = 0;
}
