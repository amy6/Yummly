package com.example.mahima.yummly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeStepDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);

        if (getIntent() != null) {
            if (getIntent().hasExtra("recipe_step")) {
                RecipeStep recipeStep = getIntent().getParcelableExtra("recipe_step");

                RecipeStepDetailFragment recipeStepDetailFragment = RecipeStepDetailFragment.newInstance(recipeStep);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipe_step_detail_container, recipeStepDetailFragment)
                        .commit();

                setTitle(recipeStep.getShortDescription());
            }
        }
    }
}