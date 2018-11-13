package com.example.mahima.yummly.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;

import com.example.mahima.yummly.R;
import com.example.mahima.yummly.model.Recipe;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.mahima.yummly.utils.Constants.GRID_LAYOUT;
import static com.example.mahima.yummly.utils.Constants.HORIZONTAL_LINEAR_LAYOUT;
import static com.example.mahima.yummly.utils.Constants.RECIPE_TYPE;
import static com.example.mahima.yummly.utils.Constants.STAGGERRED_GRID_LAYOUT;
import static com.example.mahima.yummly.utils.Constants.VERTICAL_LINEAR_LAYOUT;

public final class Utils {


    /**
     * set up layout manager for recycler view
     *
     * @param context      reference to context to access resources
     * @param recyclerView reference to recycler view
     * @param layoutType   flag indicating layout manager type
     */
    public static void setUpRecyclerView(Context context, RecyclerView recyclerView, int layoutType) {
        switch (layoutType) {
            case VERTICAL_LINEAR_LAYOUT:
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                break;
            case HORIZONTAL_LINEAR_LAYOUT:
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                break;
            case GRID_LAYOUT:
                recyclerView.setLayoutManager(new GridLayoutManager(context, getSpanCount(context)));
                break;
            case STAGGERRED_GRID_LAYOUT:
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getSpanCount(context), StaggeredGridLayoutManager.VERTICAL));
                break;
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * determines column span count based on device screen size
     *
     * @param context reference to context to access resources
     * @return column span count
     */
    private static int getSpanCount(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    /**
     * fetches recipes from json
     *
     * @param context reference to context to access resources
     * @return list of recipes
     */
    public static List<Recipe> getRecipes(Context context) {
        List<Recipe> recipes = new ArrayList<>();
        Gson gson = new Gson();
        try {
            InputStream inputStream = context.getAssets().open("baking.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            JsonReader reader = new JsonReader(inputStreamReader);
            recipes = gson.fromJson(reader, RECIPE_TYPE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    /**
     * gets a recipe by a given id
     *
     * @param context reference to context to access resources
     * @param id      recipe id
     * @return recipe object
     */
    public static Recipe getRecipeById(Context context, int id) {
        return getRecipes(context).get(id - 1);
    }

    /**
     * fetches images for all recipes
     *
     * @param context reference to context to access resources
     * @return array of image resource identifiers
     */
    public static int[] getRecipeImages(Context context) {
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.recipe_image_ids);
        int count = typedArray.length();
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();

        return ids;
    }

    /**
     * defines image resource identifier for a give measure type
     *
     * @param measure measure type
     * @return image resource identifier for a given measure
     */
    public static int getIngredientMeasureIndicator(String measure) {
        int imageId;
        switch (measure) {
            case "CUP":
                imageId = R.drawable.ic_cup;
                break;
            case "TSP":
            case "TBLSP":
                imageId = R.drawable.ic_spoon;
                break;
            case "UNIT":
                imageId = R.drawable.ic_counter;
                break;
            default:
                imageId = R.drawable.ic_scale;
        }
        return imageId;
    }

    /**
     * determines if the device screen corresponds to that of a tablet
     *
     * @param context reference to context to access resources
     * @return boolean flag indicating whether the screen corresponds to that of a tablet
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * determines if the screen orientation is landscape
     *
     * @param context reference to context to access resources
     * @return boolean flag indicating whether the orientation is landscape
     */
    public static boolean isInLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
