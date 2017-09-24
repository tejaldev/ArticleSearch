package com.ny.search.article.utils;

import android.util.Log;

import com.ny.search.article.models.Article;
import com.ny.search.article.network.response.models.Doc;
import com.ny.search.article.network.response.models.Multimedium;
import com.ny.search.article.network.response.models.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tejalpar
 */
public class ArticleResponseParser {
    public static String TAG = ArticleResponseParser.class.getSimpleName();

    public static ArrayList<Article> parseArticleInfoFromResponseObject(Response response) {
        ArrayList<Article> articleList = new ArrayList<>();
        try {
            List<Doc> docs = response.getResponse().getDocs();

            for (int i = 0; i < docs.size(); i++) {

                Article article = new Article();
                article.snippet = docs.get(i).getSnippet();
                article.webUrl = docs.get(i).getWebUrl();
                article.articleId = docs.get(i).getId();
                article.newDesk = docs.get(i).getNewDesk();

                if (docs.get(i).getHeadline() != null) {
                    article.headline = docs.get(i).getHeadline().getMain();
                }

                List<Multimedium> multimediaList = docs.get(i).getMultimedia();
                if (multimediaList != null) {
                    for (int j = 0; j < multimediaList.size(); j++) {

                        if (multimediaList.get(j).getSubtype().equals("wide")) {
                            article.wideUrl = multimediaList.get(j).getUrl();

                        }
                        if (multimediaList.get(j).getSubtype().equals("xlarge")) {
                            article.xlargeUrl = multimediaList.get(j).getUrl();

                        }
                        if (multimediaList.get(j).getSubtype().equals("thumbnail")) {
                            article.thumbnailUrl = multimediaList.get(j).getUrl();
                        }
                    }
                }
                articleList.add(article);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing article response from object: " + e.getMessage());
        }
        return articleList;
    }
}
