package com.example.mahima.yummly;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static com.example.mahima.yummly.Constants.VERTICAL_LINEAR_LAYOUT;

public final class Utils {


    public static void setUpRecyclerView(Context context, RecyclerView recyclerView, int layoutType) {
        switch (layoutType) {
            case VERTICAL_LINEAR_LAYOUT:
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                break;
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
