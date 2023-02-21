package com.adnuntius.webview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class WebViewActivity extends Activity implements View.OnClickListener {
    private WebView webView;
    private EditText urlString;

    public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.webview);
        webView = findViewById(R.id.webView);

        findViewById(R.id.goButton).setOnClickListener(this);

        urlString = findViewById(R.id.urlString);
	}

    @Override
    public void onClick(View view) {
        switch ((view).getId()) {
            case R.id.goButton:
                final String url = urlString.getText().toString();
                webView.loadUrl(url);
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (webView != null) {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (webView != null) {
            webView.saveState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
