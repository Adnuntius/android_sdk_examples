package com.adnuntius.simple;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.adnuntius.android.sdk.AdRequest;
import com.adnuntius.android.sdk.AdnuntiusAdWebView;
import com.adnuntius.android.sdk.AdnuntiusEnvironment;
import com.adnuntius.android.sdk.LoadAdHandler;

import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AdnuntiusAdWebView adView;
    private AdnuntiusEnvironment env = AdnuntiusEnvironment.production;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.adView = findViewById(R.id.adView);

        this.adView.setEnvironment(env);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        final String globalUserId = sharedPreferences.getString("globalUserId", null);
        if (globalUserId == null) {
            Log.d(TAG, "No User ID, generating!");
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("globalUserId", UUID.randomUUID().toString());
            editor.commit();
        }

        adView.logger.debug = true;
        adView.setEnvironment(AdnuntiusEnvironment.andemu);
        adView.loadBlankPage();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Click Ok to load the Ad");
        builder.setPositiveButton("Ok", (dialog, id) -> loadAd());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadAd() {
        final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        final String globalUserId = sharedPreferences.getString("globalUserId", null);
        final String sessionId = UUID.randomUUID().toString();
        Log.d(TAG, "Global User ID " + globalUserId);

        final AdRequest request = new AdRequest("5cd")
                .setWidth(1002)
                //.setHeight(160)
                .useCookies(false)
                .userId(globalUserId) // a null value will be ignored
                .sessionId(sessionId)
                .consentString("some consent string")
                .parentParameter("gdpr", "1")
                //.livePreview("7pmy5r9rj62fyhjm", "9198pft3cvktmg8d")
                .addKeyValue("version", "responsive");

//        final HttpClient client = new VolleyHttpClient(getApplicationContext());
//        AdClient adClient = new AdClient(AdnuntiusEnvironment.production, client);
//        adClient.request(request, new AdResponseHandler() {
//            @Override
//            public void onSuccess(AdResponse response) {
//                final String baseUrl = HttpUtils.getDeliveryUrl(env, "/i", null);
//                adView.loadDataWithBaseURL(baseUrl, response.getHtml(),"text/html", "UTF-8", null);
//            }
//
//            @Override
//            public void onFailure(ErrorResponse response) {
//                Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        adView.loadAd(request,
                false,
                new LoadAdHandler() {
                    @Override
                    public void onAdResponse(final AdnuntiusAdWebView view, final AdResponseInfo info) {
                        Toast.makeText(getApplicationContext(), "adView loadAd Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNoAdResponse(final AdnuntiusAdWebView view) {
                        Toast.makeText(getApplicationContext(), "adView loadAd no ad", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(final AdnuntiusAdWebView view, final String error) {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLayoutCloseView(final AdnuntiusAdWebView view) {
                        finish();
                    }
                });
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (adView != null) {
            adView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            adView.removeAllViews();
            adView.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adView != null) {
            adView.saveState(outState);
        }
    }
}
