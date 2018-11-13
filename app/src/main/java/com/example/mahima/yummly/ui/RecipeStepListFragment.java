package com.example.mahima.yummly.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mahima.yummly.R;
import com.example.mahima.yummly.adapter.RecipeStepAdapter;
import com.example.mahima.yummly.listener.OnItemClickListener;
import com.example.mahima.yummly.model.Recipe;
import com.example.mahima.yummly.model.RecipeIngredient;
import com.example.mahima.yummly.model.RecipeStep;
import com.example.mahima.yummly.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.utils.Constants.VERTICAL_LINEAR_LAYOUT;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeStepListFragment extends Fragment implements OnItemClickListener {

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recyclerView;

    private OnItemClickListener onItemClickListener;


    public RecipeStepListFragment() {
        // Required empty public constructor
    }

    public static RecipeStepListFragment newInstance(Recipe recipe, boolean isTwoPane) {
        RecipeStepListFragment fragment = new RecipeStepListFragment();
        Bundle args = new Bundle();
        // save recipe details into fragment
        args.putParcelable("recipe", recipe);
        args.putBoolean("is_two_pane", isTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onItemClickListener = (OnItemClickListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        Utils.setUpRecyclerView(getContext(), recyclerView, VERTICAL_LINEAR_LAYOUT);
        List<RecipeStep> recipeSteps = new ArrayList<>();
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        boolean isTwoPane = false;
        if (getArguments() != null) {
            Recipe recipe = getArguments().getParcelable("recipe");
            if (recipe != null) {
                recipeSteps = recipe.getSteps();
                recipeIngredients = recipe.getIngredients();
            }
            isTwoPane = getArguments().getBoolean("is_two_pane");
        }

        RecipeStepAdapter adapter = new RecipeStepAdapter(getContext(), this, recipeSteps, recipeIngredients, isTwoPane);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        onItemClickListener.onItemClick(position);
    }
}
