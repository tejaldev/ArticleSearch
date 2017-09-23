package com.ny.search.article.browser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ny.search.article.R;
import com.ny.search.article.browser.ContentWebViewClient;

/**
 * @author tejalpar
 */
public class ContentWebViewFragment extends Fragment {

    private WebView contentWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_web_view_fragment, container, false);

        String pageUrl = getArguments().getString("pageUrl");

        contentWebView = (WebView) rootView.findViewById(R.id.content_webview);
        contentWebView.getSettings().setLoadsImagesAutomatically(true);
        contentWebView.getSettings().setJavaScriptEnabled(true);
        contentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        contentWebView.setWebViewClient(new ContentWebViewClient());

        contentWebView.loadUrl(pageUrl);
        return rootView;
    }
}
