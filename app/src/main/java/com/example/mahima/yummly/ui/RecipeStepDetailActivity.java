package com.example.mahima.yummly.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mahima.yummly.R;
import com.example.mahima.yummly.model.RecipeStep;

public class RecipeStepDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (getIntent() != null) {
            if (getIntent().hasExtra("recipe_id")) {
                int id = getIntent().getIntExtra("recipe_id", -1);
                RecipeStep recipeStep = getIntent().getParcelableExtra("recipe_step");

                // get current step and total number of steps
                int position = getIntent().getIntExtra("recipe_step_position", -1);
                int count = getIntent().getIntExtra("recipe_steps_count", -1);

                if (getSupportFragmentManager().findFragmentById(R.id.recipe_step_detail_container) == null) {
                    RecipeStepDetailFragment recipeStepDetailFragment = RecipeStepDetailFragment.newInstance(id, recipeStep, position, count);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.recipe_step_detail_container, recipeStepDetailFragment)
                            .commit();
                }

                setTitle(recipeStep.getShortDescription());
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
