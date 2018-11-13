package com.example.mahima.yummly.adapter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahima.yummly.R;
import com.example.mahima.yummly.model.Recipe;
import com.example.mahima.yummly.ui.RecipeStepListActivity;
import com.example.mahima.yummly.utils.Utils;
import com.example.mahima.yummly.widget.YummlyWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    public static final String SHARED_PREFERENCES_FILE = "com.example.mahima.yummly" + ".recipe";
    private int[] imageIds;
    private Context context;
    private List<Recipe> recipeList;

    public RecipeListAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        this.imageIds = Utils.getRecipeImages(context);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecipeViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_recipe_card_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {
        Recipe recipe = recipeList.get(i);
        recipeViewHolder.recipeName.setText(recipe.getName());
        if (recipe.getImage() != null && !TextUtils.isEmpty(recipe.getImage())) {
            // verify if the image provided in the data source is valid
            Glide.with(context)
                    .load(recipe.getImage())
                    .into(recipeViewHolder.recipeImage);
        } else {
            Glide.with(context)
                    .load(imageIds[i])
                    .into(recipeViewHolder.recipeImage);
        }
        recipeViewHolder.recipeServings.setText(context.getResources().getString(
                R.string.recipe_servings, recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_card)
        CardView cardView;
        @BindView(R.id.recipe_name)
        TextView recipeName;
        @BindView(R.id.recipe_card_image)
        ImageView recipeImage;
        @BindView(R.id.recipe_servings)
        TextView recipeServings;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Recipe recipe = recipeList.get(position);
            Intent intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            context.startActivity(intent);

            // save recipe id in shared preferences to be used for widget data
            SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_FILE,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("recipe_id", recipe.getId());
            editor.apply();

            // notify widget manager to update widget list data
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, YummlyWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_list_view);

            // send a broadcast with widget update action to update the recipe name for widget
            Intent widgetIntent = new Intent(context, YummlyWidgetProvider.class);
            widgetIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            widgetIntent.putExtra("recipe_name", recipe.getName());
            context.sendBroadcast(widgetIntent);
        }
    }
}
