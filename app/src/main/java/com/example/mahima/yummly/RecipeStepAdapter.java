package com.example.mahima.yummly;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    private Context context;
    private List<RecipeStep> recipeSteps;
    private OnItemClickListener onItemClickListener;

    public RecipeStepAdapter(Context context, OnItemClickListener listener, List<RecipeStep> recipeSteps) {
        this.context = context;
        onItemClickListener = listener;
        this.recipeSteps = recipeSteps;
    }

    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecipeStepViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_recipe_step_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder recipeStepViewHolder, int i) {
        RecipeStep recipeStep = recipeSteps.get(i);
        recipeStepViewHolder.shortDesc.setText(recipeStep.getShortDescription());
    }

    @Override
    public int getItemCount() {
        return recipeSteps == null ? 0 : recipeSteps.size();
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
            int position = getAdapterPosition();
            onItemClickListener.onItemClick(position);
        }
    }
}
