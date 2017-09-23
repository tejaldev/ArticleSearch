package com.ny.search.article;

import android.content.Context;
import android.os.Bundle;
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

import com.ny.search.article.adapters.ArticlesAdapter;
import com.ny.search.article.browser.ContentWebViewFragment;
import com.ny.search.article.models.Article;
import com.ny.search.article.models.Filter;
import com.ny.search.article.network.ArticleAPI;
import com.ny.search.article.settings.SettingsDialogFragment;
import com.ny.search.article.storage.FilterSettingsStorage;
import com.ny.search.article.utils.ArticleResponseParser;
import com.ny.search.article.views.CustomSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements ArticlesAdapter.ArticleItemClickListener {
    public static String TAG = SearchActivity.class.getSimpleName();
    public static String FILTER_DIALOG_TAG = "FILTER_DIALOG";

    private RecyclerView searchRecyclerView;
    private ArticlesAdapter articlesAdapter;
    private OkHttpClient clientInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        clientInstance = new OkHttpClient();
        searchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(layoutManager);
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
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchArticle(context, query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchArticle(context, newText);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        searchArticle(context, searchView.getSearchQuery());
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
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

    private HashMap<String, String> loadSettingsFromSharedPreference(Context context) {
        HashMap<String, String> params = null;
        Filter filter = FilterSettingsStorage.getInstance(context).retrieveFilter();
        if (filter.isFilterSet()) {
            params = new HashMap<>();
            params.put("begin_date", filter.getFormattedDate());
            params.put("sort", filter.getSortOrder());
            params.put("fq", ArticleAPI.buildNewsDeskQueryParam(filter.getNewsDeskMap()));
        }
        return params;
    }

    private void searchArticle(Context context, String lookupString) {
        HashMap<String, String> params = loadSettingsFromSharedPreference(context);
        if (params == null) {
            params = new HashMap<>();
        }
        if (lookupString != null && !lookupString.isEmpty()) {
            params.put("q", lookupString);
        }
        clientInstance.newCall(ArticleAPI.buildRequestWithQueryParams(ArticleAPI.BASE_URL, params))
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Article search failed: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }
                        try {
                            final ArrayList<Article> articles = ArticleResponseParser.parseArticleInfoFromJson(new JSONObject(response.body().string()));
                            SearchActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setupAdapter(articles);
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        }
                    }
                });
    }

    private void setupAdapter(ArrayList<Article> articles) {
        articlesAdapter = new ArticlesAdapter(articles, this);
        searchRecyclerView.setAdapter(articlesAdapter);
    }

    private void showSettingsDialog() {
        SettingsDialogFragment filterSettings = SettingsDialogFragment.newInstance("Filter options");
        filterSettings.show(getFragmentManager(), FILTER_DIALOG_TAG);
    }
}
