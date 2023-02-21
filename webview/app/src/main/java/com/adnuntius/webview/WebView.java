package com.adnuntius.webview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;

public class WebView extends android.webkit.WebView {
    public WebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        this.setVerticalScrollBarEnabled(true);
        this.setHorizontalScrollBarEnabled(true);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        this.getSettings().setSupportZoom(true);
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDisplayZoomControls(false);

        CookieManager.getInstance().setAcceptCookie(true);

        final WebViewClient webClient = new WebViewClient(context);
        this.setWebViewClient(webClient);
        final WebViewChromeClient chromeClient = new WebViewChromeClient(context);
        this.setWebChromeClient(chromeClient);

        // https://stackoverflow.com/a/23844693
        boolean isDebuggable = ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
        if (isDebuggable) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
}
