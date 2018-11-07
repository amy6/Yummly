package com.example.mahima.yummly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static com.example.mahima.yummly.Constants.LOG_TAG;

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
            if (getIntent().hasExtra("recipe_step")) {
                RecipeStep recipeStep = getIntent().getParcelableExtra("recipe_step");

                if (getSupportFragmentManager().findFragmentById(R.id.recipe_step_detail_container) == null) {
                    Log.d(LOG_TAG, "Inside getIntent method of Recipe Step Detail activity");
                    RecipeStepDetailFragment recipeStepDetailFragment = RecipeStepDetailFragment.newInstance(recipeStep);
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
