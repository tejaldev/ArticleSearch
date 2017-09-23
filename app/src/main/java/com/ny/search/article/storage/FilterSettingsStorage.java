package com.ny.search.article.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ny.search.article.models.Filter;

import java.util.HashMap;

/**
 * Helper class to store to SharedPreference
 * @author tejalpar
 */
public class FilterSettingsStorage {
    public static final String SHARED_PREF_FILE = "FilterPreference";
    public static final String BEGIN_DATE_KEY = "BEGIN_DATE";
    public static final String SORT_ORDER_KEY = "SORT_ORDER";
    public static final String NEWS_DESK_KEY = "NEWS_DESK";

    private static FilterSettingsStorage instance;
    private SharedPreferences sharedPreferences;

    public static FilterSettingsStorage getInstance(Context context) {
        if (instance == null) {
            instance = new FilterSettingsStorage(context);
        }
        return instance;
    }

    private FilterSettingsStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE,Context.MODE_PRIVATE);
    }

    public void saveFilter(Filter filter) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String newsDesk = gson.toJson(filter.getNewsDeskMap());

        prefsEditor.putLong(BEGIN_DATE_KEY, filter.getDateInMillis());
        prefsEditor.putString(SORT_ORDER_KEY, filter.getSortOrder());
        prefsEditor.putString(NEWS_DESK_KEY, newsDesk);
        prefsEditor.apply();
    }

    public Filter retrieveFilter() {
        Filter filter = new Filter();
        if (sharedPreferences != null) {
            filter.setDateInMillis(sharedPreferences.getLong(BEGIN_DATE_KEY, 0));
            filter.setSortOrder(sharedPreferences.getString(SORT_ORDER_KEY, ""));

            Gson gson = new Gson();
            String newsDesk = sharedPreferences.getString(NEWS_DESK_KEY, "");
            HashMap<String, Boolean> newsDeskMap = gson.fromJson(newsDesk, new TypeToken< HashMap < String, Boolean >>() {}.getType());
            filter.setNewsDeskMap(newsDeskMap);
        }
        return filter;
    }
}
