package com.example.mahima.yummly;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.mahima.yummly.Constants.RECIPE_TYPE;
import static com.example.mahima.yummly.Constants.VERTICAL_LINEAR_LAYOUT;

public final class Utils {


    public static void setUpRecyclerView(Context context, RecyclerView recyclerView, int layoutType) {
        switch (layoutType) {
            case VERTICAL_LINEAR_LAYOUT:
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                break;
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

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

    public static Recipe getRecipeById(Context context, int id) {
        return getRecipes(context).get(id-1);
    }
}
