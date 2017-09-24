package com.ny.search.article;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ny.search.article.adapters.ArticlesAdapter;
import com.ny.search.article.browser.ContentWebViewFragment;
import com.ny.search.article.listeners.EndlessRecyclerViewScrollListener;
import com.ny.search.article.models.Article;
import com.ny.search.article.models.Filter;
import com.ny.search.article.network.response.models.Response;
import com.ny.search.article.network.RetrofitArticleAPI;
import com.ny.search.article.settings.SettingsDialogFragment;
import com.ny.search.article.storage.FilterSettingsStorage;
import com.ny.search.article.utils.ArticleResponseParser;
import com.ny.search.article.views.CustomSearchView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements ArticlesAdapter.ArticleItemClickListener, RetrofitArticleAPI.OnResultListener {
    public static String TAG = SearchActivity.class.getSimpleName();
    public static String FILTER_DIALOG_TAG = "FILTER_DIALOG";

    private Handler handler;
    private RetrofitArticleAPI retrofitArticleAPI;
    private String recentSearchQuery;
    private RecyclerView searchRecyclerView;
    private ArticlesAdapter articlesAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;

        handler = new Handler();
        retrofitArticleAPI = new RetrofitArticleAPI(this);
        searchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // triggers when new data needs to be appended
                makeDelayedNextPageRequests(context, page);
            }
        };
        // Adds the scroll listener to RecyclerView
        searchRecyclerView.addOnScrollListener(scrollListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        final Context context = this;
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final CustomSearchView searchView = (CustomSearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                makeDelayedSearchRequests(context, query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchArticle(context, newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showSettingsDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onArticleItemClickListener(View view, Article selectedArticle) {
        Bundle bundle = new Bundle();
        bundle.putString("pageUrl", selectedArticle.webUrl);
        ContentWebViewFragment fragment = new ContentWebViewFragment();
        fragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void OnSearchResponse(retrofit2.Call<Response> call, retrofit2.Response<Response> response) {
        if (!response.isSuccessful()) {
            Toast.makeText(getBaseContext(), "Search request failed.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Search request failed with error code: " + response.isSuccessful());
        }
        try {
            final ArrayList<Article> articles = ArticleResponseParser.parseArticleInfoFromResponseObject(response.body());
            setupAdapter(articles);

        } catch (Exception e) {
            Log.e(TAG, "Error parsing search response: " + e.getMessage());
        }
    }

    @Override
    public void OnNextPageResponse(retrofit2.Call<Response> call, retrofit2.Response<Response> response) {

        if (!response.isSuccessful()) {
            Toast.makeText(getBaseContext(), "Next page request failed.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Search request failed with error code: " + response.isSuccessful());
        }

        try {
            insertNewPageData(ArticleResponseParser.parseArticleInfoFromResponseObject(response.body()));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing next page fetch response: " + e.getMessage());
        }
    }

    @Override
    public void onFailure(retrofit2.Call<Response> call, Throwable t) {
        Toast.makeText(getBaseContext(), "Rest request failed.", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Article search failed: " + t.getMessage());
    }

    private void makeDelayedSearchRequests(final Context context, final String searchQuery) {
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // runs below code on main thread
                resetBeforeSearch();

                recentSearchQuery = searchQuery;
                Filter filter = loadFilterFromSharedPreference(context);
                retrofitArticleAPI.searchArticle(filter, searchQuery);
            }
        };
        handler.postDelayed(runnableCode, 2000);
    }

    private void makeDelayedNextPageRequests(final Context context, final int page) {
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // runs below code on main thread
                Filter filter = loadFilterFromSharedPreference(context);
                retrofitArticleAPI.fetchSearchNextPage(filter, recentSearchQuery, page);
            }
        };
        // Run the above code block on the main thread after 2 seconds
        handler.postDelayed(runnableCode, 2000);
    }

    private Filter loadFilterFromSharedPreference(Context context) {
        return FilterSettingsStorage.getInstance(context).retrieveFilter();
    }

    private void setupAdapter(final ArrayList<Article> articles) {
        final ArticlesAdapter.ArticleItemClickListener listener = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articlesAdapter = new ArticlesAdapter(articles, listener);
                searchRecyclerView.setAdapter(articlesAdapter);
                articlesAdapter.notifyDataSetChanged();
                searchRecyclerView.invalidate();
                searchRecyclerView.requestLayout();
            }
        });
    }

    private void insertNewPageData(final ArrayList<Article> articles) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articlesAdapter.setMoreData(articles);
                articlesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showSettingsDialog() {
        SettingsDialogFragment filterSettings = SettingsDialogFragment.newInstance("Filter options");
        filterSettings.show(getFragmentManager(), FILTER_DIALOG_TAG);
    }

    private void resetBeforeSearch() {
        recentSearchQuery = null;
        scrollListener.resetState();
        searchRecyclerView.setAdapter(null);
        articlesAdapter = null;
    }
}
