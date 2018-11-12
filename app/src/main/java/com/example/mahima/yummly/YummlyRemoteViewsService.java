package com.example.mahima.yummly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import static com.example.mahima.yummly.Constants.LOG_TAG;
import static com.example.mahima.yummly.RecipeListAdapter.SHARED_PREFERENCES_FILE;

public class YummlyRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new YummlyRemoteViewsFactory(getApplicationContext());
    }

    public class YummlyRemoteViewsFactory implements RemoteViewsFactory {

        private Context context;
        private List<RecipeIngredient> ingredients;

        public YummlyRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            Log.d(LOG_TAG, "onDataSetChanged called");
            SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
            if (sharedPrefs.contains("recipe_id")) {
                int recipeId = sharedPrefs.getInt("recipe_id", 0);
                Recipe recipe = Utils.getRecipeById(context, recipeId);
                ingredients = new ArrayList<>(recipe.getIngredients());
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients == null ? 0 : ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RecipeIngredient ingredient = ingredients.get(position);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
            remoteViews.setTextViewText(android.R.id.text1, ingredient.getIngredient());
//            remoteViews.setTextViewText(R.id.quantity, String.valueOf(ingredient.getQuantity()));
//            remoteViews.setTextViewText(R.id.measure, ingredient.getMeasure());
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
