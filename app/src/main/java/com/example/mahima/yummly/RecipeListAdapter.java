package com.example.mahima.yummly;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.LOG_TAG;

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
        Glide.with(context)
                .load(imageIds[i])
                .into(recipeViewHolder.recipeImage);
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

            SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_FILE,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("recipe_id", recipe.getId());
            editor.apply();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, YummlyWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_list_view);

            Intent widgetIntent = new Intent(context, YummlyWidgetProvider.class);
            widgetIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            widgetIntent.putExtra("recipe_name", recipe.getName());
            Log.d(LOG_TAG, "Sending broadcast");
            context.sendBroadcast(widgetIntent);
        }
    }
}
