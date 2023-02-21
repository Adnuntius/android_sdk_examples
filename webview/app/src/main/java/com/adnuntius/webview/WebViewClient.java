package com.adnuntius.webview;

import android.content.Context;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

public class WebViewClient extends android.webkit.WebViewClient {
    private final Context context;

    public WebViewClient(final Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest url) {
        return false;
    }
}
