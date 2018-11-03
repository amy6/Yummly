package com.example.mahima.yummly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static com.example.mahima.yummly.Constants.LOG_TAG;

public class RecipeStepListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        if (getIntent() != null) {
            if (getIntent().hasExtra("recipe_id")) {
                int id = getIntent().getIntExtra("recipe_id", 0);
                Recipe recipe = Utils.getRecipeById(this, id);
                Bundle args = new Bundle();
                args.putParcelable("recipe", recipe);
                Log.d(LOG_TAG, "Recipe id inside activity class : " + id);
                RecipeStepListFragment recipeStepListFragment = RecipeStepListFragment.getNewInstance(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipe_step_list_container, recipeStepListFragment)
                        .commit();

                setTitle(recipe.getName());
            }
        }
    }
}
