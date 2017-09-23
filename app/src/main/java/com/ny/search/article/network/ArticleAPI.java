package com.ny.search.article.network;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by tejalpar on 9/19/17.
 */
public class ArticleAPI {
    public static final String BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            //"https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=79b5edd5a30d4971a4a1eec89f3e6947";
    private static final String API_KEY = "79b5edd5a30d4971a4a1eec89f3e6947";

//    private OkHttpClient clientInstance;
//
//    public OkHttpClient getInstance() {
//        if (clientInstance == null) {
//            clientInstance = new OkHttpClient();
//
//        } else return clientInstance;
//    }

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
}
