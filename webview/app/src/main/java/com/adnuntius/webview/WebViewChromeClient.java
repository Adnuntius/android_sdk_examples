package com.adnuntius.webview;

import android.content.Context;
import android.graphics.Bitmap;

public class WebViewChromeClient extends android.webkit.WebChromeClient {
    private final Context context;

    public WebViewChromeClient(final Context context) {
        this.context = context;
    }

    @Override
    public void onProgressChanged(final android.webkit.WebView view, final int progress) {
    }
}
