package com.ny.search.article.network;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Not using this API now replaced with {@link RetrofitArticleAPI}, keeping it around just for reference
 *
 * Created by tejalpar on 9/19/17.
 */
public class ArticleAPI {
    public static final String BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            //"https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=79b5edd5a30d4971a4a1eec89f3e6947";
    private static final String API_KEY = "79b5edd5a30d4971a4a1eec89f3e6947";

    public static Request buildRequestWithQueryParams(String searchUrl, HashMap<String, String> params) {
        params.put("api-key", API_KEY);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(searchUrl).newBuilder();
        for (Map.Entry<String, String> e: params.entrySet()) {
            urlBuilder.addQueryParameter(e.getKey(), e.getValue());
        }

        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    public static String buildNewsDeskQueryParam(HashMap<String, Boolean> params) {
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

    // Call API in Activity
    //    private void searchArticle(Context context, String lookupString) {
//        recentSearchQuery = lookupString;
//        HashMap<String, String> params = loadSettingsFromSharedPreference(context);
//        if (params == null) {
//            params = new HashMap<>();
//        }
//        params.put("page", "0");
//        if (lookupString != null && !lookupString.isEmpty()) {
//            params.put("q", lookupString);
//        }
//        clientInstance.newCall(ArticleAPI.buildRequestWithQueryParams(ArticleAPI.BASE_URL, params))
//                .enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.e(TAG, "Article search failed: " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, final Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            throw new IOException("Unexpected code " + response);
//                        }
//                        try {
//                            final ArrayList<Article> articles = ArticleResponseParser.parseArticleInfoFromJson(new JSONObject(response.body().string()));
//                            SearchActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setupAdapter(articles);
//                                }
//                            });
//                        } catch (JSONException e) {
//                            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
//                        }
//                    }
//                });
//    }
//
//    public void loadArticlesSearchResultNextPage(Context context, int nextPage) {
//        HashMap<String, String> params = loadSettingsFromSharedPreference(context);
//        if (params == null) {
//            params = new HashMap<>();
//        }
//        params.put("page", String.valueOf(nextPage));
//        Log.e(TAG, "NextPage: " + nextPage);
////        if (searchQuery != null && !searchQuery.isEmpty()) {
////            params.put("q", searchQuery);
////        }
//
//        clientInstance.newCall(ArticleAPI.buildRequestWithQueryParams(ArticleAPI.BASE_URL, params))
//                .enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.e(TAG, "Article next page search failed: " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, final Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            throw new IOException("Unexpected code " + response);
//                        }
//                        try {
//                            insertNewPageData(ArticleResponseParser.parseArticleInfoFromJson(new JSONObject(response.body().string())));
//                        } catch (JSONException e) {
//                            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
//                        }
//                    }
//                });
//    }
//
//    private HashMap<String, String> loadSettingsFromSharedPreference(Context context) {
//        HashMap<String, String> params = null;
//        Filter filter = FilterSettingsStorage.getInstance(context).retrieveFilter();
//        if (filter.isFilterSet()) {
//            params = new HashMap<>();
//            params.put("begin_date", filter.getFormattedDate());
//            params.put("sort", filter.getSortOrder());
//            params.put("fq", ArticleAPI.buildNewsDeskQueryParam(filter.getNewsDeskMap()));
//        }
//        return params;
//    }


    // Article response JSON parser for response returned by ArticleAPI

//    public static ArrayList<Article> parseArticleInfoFromJson(JSONObject response) {
//        ArrayList<Article> articleList = new ArrayList<>();
//        try {
//            JSONObject resObject = response.getJSONObject("response");
//            JSONArray resultsJson = resObject.getJSONArray("docs");
//
//            for (int i = 0; i < resultsJson.length(); i++) {
//                Article article = new Article();
//                JSONObject item = resultsJson.getJSONObject(i);
//
//                article.snippet = item.optString("snippet");
//                article.webUrl = item.optString("web_url");
//                article.articleId = item.optString("_id");
//                article.newDesk = item.optString("new_desk");
//
//                JSONObject headlineObj = item.getJSONObject("headline");
//                article.headline = headlineObj.optString("main");
//
//                JSONArray multimediaList = item.getJSONArray("multimedia");
//                for (int j = 0; j < multimediaList.length(); j++) {
//                    JSONObject media = multimediaList.getJSONObject(j);
//
//                    if (media.optString("subtype").equals("wide")) {
//                        article.wideUrl = media.optString("url", "");
//
//                    }
//                    if (media.optString("subtype").equals("xlarge")) {
//                        article.xlargeUrl = media.optString("url", "");
//
//                    }
//                    if (media.optString("subtype").equals("thumbnail")) {
//                        article.thumbnailUrl = media.optString("url", "");
//                    }
//                }
//                articleList.add(article);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing article info from json: " + e.getMessage());
//        }
//        return articleList;
//    }
//
//    public static int parseOffsetInfo(JSONObject response) {
//        try {
//            JSONObject resObject = response.getJSONObject("response");
//            JSONObject metaObject = resObject.getJSONObject("meta");
//
//            return metaObject.optInt("offset");
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing article info from json: " + e.getMessage());
//        }
//        return -1;
//    }
}
