package com.example.mahima.yummly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.HORIZONTAL_LINEAR_LAYOUT;
import static com.example.mahima.yummly.Constants.LOG_TAG;

public class RecipeStepAdapter extends RecyclerView.Adapter {

    private static final int RECIPE_INGREDIENTS_VIEW = 0;
    private static final int RECIPE_STEPS_VIEW = 1;

    private Context context;
    private List<RecipeStep> recipeSteps;
    private List<RecipeIngredient> ingredients;
    private OnItemClickListener onItemClickListener;
    private boolean isTwoPane;
    private int selectedPosition = 1;

    public RecipeStepAdapter(Context context, OnItemClickListener listener, List<RecipeStep> recipeSteps, List<RecipeIngredient> recipeIngredients, boolean isTwoPane) {
        this.context = context;
        onItemClickListener = listener;
        this.recipeSteps = recipeSteps;
        ingredients = recipeIngredients;
        this.isTwoPane = isTwoPane;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (i) {
            case RECIPE_INGREDIENTS_VIEW:
                viewHolder = new RecipeIngredientViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_recipe_list, viewGroup, false));
                break;
            case RECIPE_STEPS_VIEW:
                viewHolder = new RecipeStepViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.layout_recipe_step_list_item, viewGroup, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case RECIPE_INGREDIENTS_VIEW:
                RecipeIngredientViewHolder recipeIngredientViewHolder =
                        (RecipeIngredientViewHolder) viewHolder;
                Utils.setUpRecyclerView(context, recipeIngredientViewHolder.recyclerView,
                        HORIZONTAL_LINEAR_LAYOUT);
                RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(context, ingredients);
                recipeIngredientViewHolder.recyclerView.setAdapter(adapter);
                break;
            case RECIPE_STEPS_VIEW:
                RecipeStep recipeStep = recipeSteps.get(i-1);
                RecipeStepViewHolder recipeStepViewHolder =
                        (RecipeStepViewHolder) viewHolder;
                recipeStepViewHolder.shortDesc.setText(recipeStep.getShortDescription());
                Log.d(LOG_TAG, "Position " + i + " desc : " + recipeStep.getShortDescription());

                if (isTwoPane) {
                    if (selectedPosition == i) {
                        recipeStepViewHolder.itemView.setSelected(true);
                    } else {
                        recipeStepViewHolder.itemView.setSelected(false);
                    }
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return recipeSteps == null ? 0 : recipeSteps.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return RECIPE_INGREDIENTS_VIEW;
        } else {
            return RECIPE_STEPS_VIEW;
        }
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_step_card)
        CardView cardView;
        @BindView(R.id.recipe_step_short_desc)
        TextView shortDesc;

        public RecipeStepViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition() - 1;
            onItemClickListener.onItemClick(position);
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public class RecipeIngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_recycler_view)
        RecyclerView recyclerView;

        public RecipeIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
