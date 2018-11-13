package com.example.mahima.yummly.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.mahima.yummly.R;

/**
 * Implementation of App Widget functionality.
 */
public class YummlyWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yummly_widget);
        Intent intent = new Intent(context, YummlyRemoteViewsService.class);
        views.setRemoteAdapter(R.id.appwidget_list_view, intent);

        if (recipeName != null && !TextUtils.isEmpty(recipeName)) {
            views.setTextViewText(R.id.recipe_name, recipeName);
        }

        views.setEmptyView(R.id.appwidget_list_view, R.id.appwidget_empty_text);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, context.getResources().getString(R.string.recipe_ingredients));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String recipeName = intent.getStringExtra("recipe_name");

        // handle widget update action to display widget name
        if (intent.getAction() != null && intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, YummlyWidgetProvider.class));
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, recipeName);
            }
        } else {
            super.onReceive(context, intent);
        }
    }
}

