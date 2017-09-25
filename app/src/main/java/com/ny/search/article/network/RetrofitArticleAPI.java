package com.ny.search.article.network;


import android.util.Log;

import com.ny.search.article.models.Filter;
import com.ny.search.article.network.response.models.Response;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author tejalpar
 */
public class RetrofitArticleAPI {

    private SearchService searchService;
    private OnResultListener resultListener;

    public interface OnResultListener {
        void OnSearchResponse(Call<Response> call, retrofit2.Response<Response> response);
        void OnNextPageResponse(Call<Response> call, retrofit2.Response<Response> response);
        void onFailure(Call<Response> call, Throwable t);
    }

    public RetrofitArticleAPI(OnResultListener resultListener) {
        this.resultListener = resultListener;

        //1. Create rest adapter with the API class
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(SearchService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //2. Create service instance (i.e. the interface instance)
        searchService = restAdapter.create(SearchService.class);
    }

    public void searchArticle(Filter queryParams, String query) {

        Call<Response> serviceCall = buildServiceCall(queryParams, query, 0);

        // Make API call
        serviceCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.d("Success", "Search request successful.");
                resultListener.OnSearchResponse(call, response);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("Failure", "Search request failed with error: " + t.getMessage());
                resultListener.onFailure(call, t);
            }
        });
    }

    public void fetchSearchNextPage(Filter queryParams, String query, int page) {

        Call<Response> serviceCall = buildServiceCall(queryParams, query, page);

        // Then make API call
        serviceCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.d("Success", "Next page fetch successful.");
                resultListener.OnNextPageResponse(call, response);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("Failure", "Next page fetch request failed with error: " + t.getMessage());
                resultListener.onFailure(call, t);
            }
        });
    }

    private Call<Response> buildServiceCall(Filter queryParams, String query, int page) {

        //3. call api method from interface
        return searchService.getResponse(
                (queryParams.getFormattedDate().equals("0")) ? null: queryParams.getFormattedDate(),
                (queryParams.getSortOrder().equals("")) ? null: queryParams.getSortOrder(),
                buildNewsDeskQueryParam(queryParams.getNewsDeskMap()),
                query,
                page,
                SearchService.API_KEY);
    }

    private String buildNewsDeskQueryParam(HashMap<String, Boolean> params) {
        if (params == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("news_desk:(");
        for (Map.Entry<String, Boolean> e: params.entrySet()) {
            if (e.getValue()) {
                stringBuilder.append("\"");
                stringBuilder.append(e.getKey());
                stringBuilder.append("\" ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
