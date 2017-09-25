package com.ny.search.article.browser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ny.search.article.R;

/**
 * Not using this class, replaced with Chrome Custom Tabs to share Url, keeping it around just for reference
 *
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

    /**
     * Load this fragment from Search Activity using below code to use WebViewClient for opening web urls
     */
//        Bundle bundle = new Bundle();
//        bundle.putString("pageUrl", selectedArticle.webUrl);
//        ContentWebViewFragment fragment = new ContentWebViewFragment();
//        fragment.setArguments(bundle);
//
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.add(R.id.frameLayout, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
}
