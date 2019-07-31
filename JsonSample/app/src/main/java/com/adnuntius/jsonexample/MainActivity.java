package com.adnuntius.jsonexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.adnuntius.android.sdk.AdView;

public class MainActivity extends AppCompatActivity {
    private AdView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.webView = findViewById(R.id.adView);
    }

    @Override
    protected void onResume() {
       super.onResume();

       webView.loadAdFromJson("{\"siteId\": \"wsxcqfrnmxd95lm2\", \"adUnits\": [{\"auId\": \"0000000000023ae5\"}]}");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (webView != null) {
            webView.saveState(outState);
        }
    }
}
