package com.ny.search.article.views;

import android.content.Context;
import android.support.v7.widget.SearchView;

/**
 * @author tejalpar
 */
public class CustomSearchView extends SearchView {
    private String searchQuery;

    public CustomSearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewCollapsed() {
        searchQuery = getQuery().toString();
        super.onActionViewCollapsed();
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}
