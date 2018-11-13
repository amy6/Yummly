package com.example.mahima.yummly.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.mahima.yummly.R;
import com.example.mahima.yummly.adapter.RecipeListAdapter;
import com.example.mahima.yummly.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.utils.Constants.GRID_LAYOUT;
import static com.example.mahima.yummly.utils.Constants.VERTICAL_LINEAR_LAYOUT;

public class RecipeListActivity extends AppCompatActivity {

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        ButterKnife.bind(this);

        // set up recycler view
        if (Utils.isTablet(this) || Utils.isInLandscape(this)) {
            Utils.setUpRecyclerView(this, recyclerView, GRID_LAYOUT);
        } else {
            Utils.setUpRecyclerView(this, recyclerView, VERTICAL_LINEAR_LAYOUT);
        }

        RecipeListAdapter adapter = new RecipeListAdapter(this, Utils.getRecipes(this));
        recyclerView.setAdapter(adapter);

    }
}
