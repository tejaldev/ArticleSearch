package com.ny.search.article.network;

import com.ny.search.article.network.response.models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit2 based service to query NY articles
 *
 * @author tejalpar
 */
public interface SearchService {
    String BASE_URL = "https://api.nytimes.com/svc/search/v2/";
    String API_KEY = "79b5edd5a30d4971a4a1eec89f3e6947";

    @GET("articlesearch.json")
    Call<Response> getResponse(
            @Query("begin_date") String beginDate,
            @Query("sort") String sortOrder,
            @Query("fq") String newsDesk,
            @Query("q") String query,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );
}
