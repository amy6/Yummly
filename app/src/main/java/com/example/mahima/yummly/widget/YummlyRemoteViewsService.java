package com.example.mahima.yummly.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.example.mahima.yummly.model.Recipe;
import com.example.mahima.yummly.model.RecipeIngredient;
import com.example.mahima.yummly.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.example.mahima.yummly.adapter.RecipeListAdapter.SHARED_PREFERENCES_FILE;

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
            // fetch recipe id from shared preferences
            SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
            if (sharedPrefs.contains("recipe_id")) {
                int recipeId = sharedPrefs.getInt("recipe_id", 0);
                Recipe recipe = Utils.getRecipeById(context, recipeId);
                // get ingredients list
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
            // update remote views for widget list
            RecipeIngredient ingredient = ingredients.get(position);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
            remoteViews.setTextColor(android.R.id.text1, context.getResources().getColor(android.R.color.black));
            remoteViews.setTextViewText(android.R.id.text1, ingredient.getIngredient());
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
