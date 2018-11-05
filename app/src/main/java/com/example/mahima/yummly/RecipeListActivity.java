package com.example.mahima.yummly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mahima.yummly.Constants.VERTICAL_LINEAR_LAYOUT;

public class RecipeListActivity extends AppCompatActivity {

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_text_view)
    TextView emptyTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        ButterKnife.bind(this);

        Utils.setUpRecyclerView(this, recyclerView, VERTICAL_LINEAR_LAYOUT);

        RecipeListAdapter adapter = new RecipeListAdapter(this, Utils.getRecipes(this));
        recyclerView.setAdapter(adapter);

    }
}
