package com.example.mahima.yummly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.RecipeIngredientViewHolder> {

    private Context context;
    private List<RecipeIngredient> ingredientList;

    public RecipeIngredientAdapter(Context context, List<RecipeIngredient> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public RecipeIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecipeIngredientViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_recipe_ingredient_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientViewHolder recipeIngredientViewHolder, int i) {
        RecipeIngredient ingredient = ingredientList.get(i);
        recipeIngredientViewHolder.ingredient.setText(ingredient.getIngredient());
        recipeIngredientViewHolder.quantity.setText(String.valueOf(ingredient.getQuantity()));
        recipeIngredientViewHolder.measure.setText(ingredient.getMeasure());
        int imageId = Utils.getIngredientMeasureIndicator(ingredient.getMeasure());
        Glide.with(context)
                .load(imageId)
                .into(recipeIngredientViewHolder.measureIndicator);

    }

    @Override
    public int getItemCount() {
        return ingredientList == null ? 0 : ingredientList.size();
    }

    public static class RecipeIngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredient)
        TextView ingredient;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.measure)
        TextView measure;
        @BindView(R.id.measure_indicator)
        ImageView measureIndicator;

        public RecipeIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
