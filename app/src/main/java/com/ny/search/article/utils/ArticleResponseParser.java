package com.ny.search.article.utils;

import android.util.Log;

import com.ny.search.article.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author tejalpar
 */
public class ArticleResponseParser {
    public static String TAG = ArticleResponseParser.class.getSimpleName();

    public static ArrayList<Article> parseArticleInfoFromJson(JSONObject response) {
        ArrayList<Article> articleList = new ArrayList<>();
        try {
            JSONObject resObject = response.getJSONObject("response");
            JSONArray resultsJson = resObject.getJSONArray("docs");

            for (int i = 0; i < resultsJson.length(); i++) {
                Article article = new Article();
                JSONObject item = resultsJson.getJSONObject(i);

                article.snippet = item.optString("snippet");
                article.webUrl = item.optString("web_url");
                article.articleId = item.optString("_id");
                article.newDesk = item.optString("new_desk");

                JSONObject headlineObj = item.getJSONObject("headline");
                article.headline = headlineObj.optString("main");

                JSONArray multimediaList = item.getJSONArray("multimedia");
                for (int j = 0; j < multimediaList.length(); j++) {
                    JSONObject media = multimediaList.getJSONObject(j);

                    if (media.optString("subtype").equals("wide")) {
                        article.wideUrl = media.optString("url", "");

                    }
                    if (media.optString("subtype").equals("xlarge")) {
                        article.xlargeUrl = media.optString("url", "");

                    }
                    if (media.optString("subtype").equals("thumbnail")) {
                        article.thumbnailUrl = media.optString("url", "");
                    }
                }
                articleList.add(article);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing article info from json: " + e.getMessage());
        }
        return articleList;
    }

    public static int parseOffsetInfo(JSONObject response) {
        try {
            JSONObject resObject = response.getJSONObject("response");
            JSONObject metaObject = resObject.getJSONObject("meta");

            return metaObject.optInt("offset");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing article info from json: " + e.getMessage());
        }
        return -1;
    }
}
