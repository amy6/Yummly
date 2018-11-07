package com.example.mahima.yummly;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.LOG_TAG;
import static com.example.mahima.yummly.Constants.VERTICAL_LINEAR_LAYOUT;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeStepListFragment extends Fragment implements OnItemClickListener{

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recyclerView;

    private List<RecipeStep> recipeSteps;
    private OnItemClickListener onItemClickListener;
    private List<RecipeIngredient> recipeIngredients;


    public RecipeStepListFragment() {
        // Required empty public constructor
    }

    public static RecipeStepListFragment newInstance(Recipe recipe) {
        RecipeStepListFragment fragment = new RecipeStepListFragment();
        Bundle args = new Bundle();
        args.putParcelable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onItemClickListener = (OnItemClickListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        Utils.setUpRecyclerView(getContext(), recyclerView, VERTICAL_LINEAR_LAYOUT);
        recipeSteps = new ArrayList<>();
        recipeIngredients = new ArrayList<>();

        if (getArguments() != null) {
            Recipe recipe = getArguments().getParcelable("recipe");
            if (recipe != null) {
                recipeSteps = recipe.getSteps();
                recipeIngredients = recipe.getIngredients();
            }
            Log.d(LOG_TAG, "Recipe steps count : " + recipeSteps.size());
        }

        RecipeStepAdapter adapter = new RecipeStepAdapter(getContext(), this, recipeSteps, recipeIngredients);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        onItemClickListener.onItemClick(position);
    }
}
