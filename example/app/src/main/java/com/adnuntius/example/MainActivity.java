package com.adnuntius.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.adnuntius.android.sdk.AdConfig;
import com.adnuntius.android.sdk.AdnuntiusAdWebView;
import com.adnuntius.android.sdk.CompletionHandler;
import com.adnuntius.android.sdk.CompletionHandlerAdaptor;


public class MainActivity extends AppCompatActivity {
    private AdnuntiusAdWebView adView;
    private AdnuntiusAdWebView adView2;
    private AdnuntiusAdWebView adView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.adView = findViewById(R.id.adView);
        this.adView2 = findViewById(R.id.adView2);
        this.adView3 = findViewById(R.id.adView3);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adView.loadFromScript("<html>\n" +
                "<head>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn.adnuntius.com/adn.js\" async></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "   <div id=\"adn-000000000006f450\" style=\"display:none\"></div>\n" +
                "   <script type=\"text/javascript\">\n" +
                "       window.adn = window.adn || {}; adn.calls = adn.calls || [];\n" +
                "           adn.calls.push(function() {\n" +
                "               adn.request({ adUnits: [\n" +
                "                   {auId: '000000000006f450', auW: 300, auH: 200, kv: [{'version':'2.2.3'}] } " +
                        "]}); " +
                "   });</script>\n" +
                "</body>\n" +
                "</html>",
                new CompletionHandler() {
                    @Override
                    public void onComplete(int adCount) {
                        Log.d("MainActivity.adView", "Completed ad: " + adCount);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("MainActivity.adView", "Failure: " + error);
                    }
                });

        AdConfig config = new AdConfig("000000000006f450")
                .setWidth(300)
                .setHeight(200)
                .addKeyValue("version", "4.3");

        adView2.loadFromConfig(config,
                new CompletionHandler() {
                    @Override
                    public void onComplete(int adCount) {
                        Log.d("MainActivity.adView2", "Completed ad: " + adCount);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("MainActivity.adView2", "Failure: " + error);
                    }
                });

        adView3.loadFromApi("{\"adUnits\": [{\"auId\": \"000000000006f450\", \"kv\": [{\"version\":\"10\"}]}]}",
                new CompletionHandler() {
                    @Override
                    public void onComplete(int adCount) {
                        Log.d("MainActivity.adView3", "Completed ad: " + adCount);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("MainActivity.adView3", "Failure: " + error);
                    }
                });

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (adView != null) {
            adView.restoreState(savedInstanceState);
        }

        if (adView2 != null) {
            adView2.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adView != null) {
            adView.destroy();
        }

        if (adView2 != null) {
            adView2.destroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adView != null) {
            adView.saveState(outState);
        }

        if (adView2 != null) {
            adView2.saveState(outState);
        }
    }
}
