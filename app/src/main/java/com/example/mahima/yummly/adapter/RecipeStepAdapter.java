package com.example.mahima.yummly.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahima.yummly.R;
import com.example.mahima.yummly.listener.OnItemClickListener;
import com.example.mahima.yummly.model.RecipeIngredient;
import com.example.mahima.yummly.model.RecipeStep;
import com.example.mahima.yummly.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.utils.Constants.GRID_LAYOUT;
import static com.example.mahima.yummly.utils.Constants.STAGGERRED_GRID_LAYOUT;
import static com.example.mahima.yummly.utils.Constants.VERTICAL_LINEAR_LAYOUT;

public class RecipeStepAdapter extends RecyclerView.Adapter {

    // define different view types for the recycler view
    private static final int RECIPE_HEADER_TEXT_VIEW = 0;
    private static final int RECIPE_INGREDIENTS_VIEW = 1;
    private static final int RECIPE_STEPS_VIEW = 2;

    private Context context;
    private List<RecipeStep> recipeSteps;
    private List<RecipeIngredient> ingredients;
    private OnItemClickListener onItemClickListener;
    private boolean isTwoPane;
    private int selectedPosition = 1;

    public RecipeStepAdapter(Context context, OnItemClickListener listener, List<RecipeStep> recipeSteps, List<RecipeIngredient> recipeIngredients, boolean isTwoPane) {
        this.context = context;
        this.onItemClickListener = listener;
        this.recipeSteps = recipeSteps;
        this.ingredients = recipeIngredients;
        this.isTwoPane = isTwoPane;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (i) {
            case RECIPE_HEADER_TEXT_VIEW:
                viewHolder = new RecipeHeaderTextViewHolder(LayoutInflater.from(context)
                        .inflate(android.R.layout.simple_list_item_1, viewGroup, false));
                break;
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
            case RECIPE_HEADER_TEXT_VIEW:
                RecipeHeaderTextViewHolder recipeHeaderTextViewHolder =
                        (RecipeHeaderTextViewHolder) viewHolder;
                recipeHeaderTextViewHolder.headerText.setGravity(Gravity.CENTER);
                recipeHeaderTextViewHolder.headerText.setAllCaps(true);
                recipeHeaderTextViewHolder.headerText.setTypeface(null, Typeface.BOLD);

                // set header texts to separate ingredients and steps
                if (i == 0) {
                    recipeHeaderTextViewHolder.headerText.setText(R.string.ingredients);
                }
                if (i == 2) {
                    recipeHeaderTextViewHolder.headerText.setText(R.string.steps);
                }
                break;

            case RECIPE_INGREDIENTS_VIEW:
                RecipeIngredientViewHolder recipeIngredientViewHolder =
                        (RecipeIngredientViewHolder) viewHolder;

                // handle layout managers for different orientation and device sizes
                if (isTwoPane) {
                    Utils.setUpRecyclerView(context, recipeIngredientViewHolder.recyclerView,
                            VERTICAL_LINEAR_LAYOUT);
                } else if (Utils.isInLandscape(context)) {
                    Utils.setUpRecyclerView(context, recipeIngredientViewHolder.recyclerView,
                            GRID_LAYOUT);
                } else {
                    Utils.setUpRecyclerView(context, recipeIngredientViewHolder.recyclerView,
                            STAGGERRED_GRID_LAYOUT);
                }

                // set adapter for displaying ingredients
                RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(context, ingredients);
                recipeIngredientViewHolder.recyclerView.setAdapter(adapter);
                break;

            case RECIPE_STEPS_VIEW:
                RecipeStep recipeStep = recipeSteps.get(i - 3);
                RecipeStepViewHolder recipeStepViewHolder =
                        (RecipeStepViewHolder) viewHolder;
                recipeStepViewHolder.shortDesc.setText(recipeStep.getShortDescription());

                // display step description, current step count and total steps
                if (recipeStep.getId() > 0) {
                    recipeStepViewHolder.recipeStepCount.setVisibility(View.VISIBLE);
                    recipeStepViewHolder.recipeStepCount.setText(String.valueOf(recipeStep.getId()));
                    recipeStepViewHolder.shortDesc.setTypeface(null, Typeface.NORMAL);
                } else {
                    recipeStepViewHolder.recipeStepCount.setVisibility(View.INVISIBLE);
                    recipeStepViewHolder.shortDesc.setTypeface(null, Typeface.BOLD);
                }

                // display current selected step for tablets
                if (isTwoPane) {
                    recipeStepViewHolder.playRecipeStep.setVisibility(View.GONE);
                    if (selectedPosition == i) {
                        recipeStepViewHolder.itemView.setSelected(true);
                    } else {
                        recipeStepViewHolder.itemView.setSelected(false);
                    }
                } else {
                    recipeStepViewHolder.playRecipeStep.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return recipeSteps == null ? 0 : recipeSteps.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
            case 2:
                return RECIPE_HEADER_TEXT_VIEW;
            case 1:
                return RECIPE_INGREDIENTS_VIEW;
            default:
                return RECIPE_STEPS_VIEW;
        }
    }

    public class RecipeHeaderTextViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        TextView headerText;

        public RecipeHeaderTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_step_layout)
        ConstraintLayout constraintLayout;
        @BindView(R.id.recipe_step_short_desc)
        TextView shortDesc;
        @BindView(R.id.recipe_step_count)
        TextView recipeStepCount;
        @BindView(R.id.play_button)
        ImageView playRecipeStep;

        public RecipeStepViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            constraintLayout.setOnClickListener(this);
            playRecipeStep.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recipe_step_layout:
                case R.id.play_button:
                    int position = getAdapterPosition() - 3;
                    onItemClickListener.onItemClick(position);
                    selectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    break;
            }
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
