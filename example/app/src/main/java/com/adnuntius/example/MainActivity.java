package com.adnuntius.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adnuntius.android.sdk.AdRequest;
import com.adnuntius.android.sdk.AdnuntiusAdWebView;
import com.adnuntius.android.sdk.AdnuntiusEnvironment;
import com.adnuntius.android.sdk.CompletionHandler;
import com.adnuntius.android.sdk.data.DataClient;
import com.adnuntius.android.sdk.data.DataResponseHandler;
import com.adnuntius.android.sdk.data.Page;
import com.adnuntius.android.sdk.data.Sync;
import com.adnuntius.android.sdk.data.profile.Instant;
import com.adnuntius.android.sdk.data.profile.LocalDate;
import com.adnuntius.android.sdk.data.profile.Profile;
import com.adnuntius.android.sdk.data.profile.ProfileFields;
import com.adnuntius.android.sdk.http.ErrorResponse;
import com.adnuntius.android.sdk.http.HttpClient;
import com.adnuntius.android.sdk.http.volley.VolleyHttpClient;


public class MainActivity extends AppCompatActivity {
    private AdnuntiusAdWebView adView;
    private AdnuntiusAdWebView adView2;
    private DataClient dataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.adView = findViewById(R.id.adView);
        this.adView2 = findViewById(R.id.adView2);

        final HttpClient client = new VolleyHttpClient(getApplicationContext());
        dataClient = new DataClient(AdnuntiusEnvironment.dev, client);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Profile profile = new Profile();
        profile.setExternalSystemIdentifier("asdasd", "asdasdasd");
        profile.setFolderId("000000000000009d");
        profile.setBrowserId("23123123132123123213213");
        profile.setProfileValue(ProfileFields.company, "Adnuntius");
        profile.setProfileValue(ProfileFields.country, "Norway");
        profile.setProfileValue(ProfileFields.dateOfBirth, LocalDate.of(1994, 2, 14));
        profile.setProfileValue(ProfileFields.createdAt, Instant.now());
        dataClient.profile(profile, new DataResponseHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Data Client Profile Update Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ErrorResponse response) {
                Log.d("dataClient", "Data Client Profile Update Failure: " + response.getStatusCode());
            }
        });

        final Sync sync = new Sync();
        sync.setFolderId("000000000000009d");
        sync.setBrowserId("23123123132123123213213");
        sync.setExternalSystemIdentifier("SOME SYSTEM", "adsdasdasd");
        dataClient.sync(sync, new DataResponseHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Data Client Sync Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ErrorResponse response) {
                Log.d("dataClient", "Data Client Profile Sync Failure: " + response.getStatusCode());
            }
        });

        final Page page = new Page();
        page.setFolderId("000000000000009d");
        page.setBrowserId("23123123132123123213213");
        page.setDomainName("adnuntius.com");
        page.addCategories("minecraft", "doom");
        page.addKeywords("wood", "pla");
        dataClient.page(page, new DataResponseHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Data Client Page Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ErrorResponse response) {
                Log.d("dataClient", "Data Client Profile Page Failure: " + response.getStatusCode());
            }
        });

        AdRequest request = new AdRequest("000000000006f450")
                .setWidth(300)
                .setHeight(200)
                .noCookies()
                .addKeyValue("version", "interstitial2");

        adView.loadAd(request,
                new CompletionHandler() {
                    @Override
                    public void onClose() {
                        finish();
                    }

                    @Override
                    public void onComplete(int adCount) {
                        Toast.makeText(getApplicationContext(),"adView loadAd Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("MainActivity.adView", "adView loadAd Failure: " + error);
                    }
                });

        AdRequest request2 = new AdRequest("000000000006f450")
                .setWidth(300)
                .setHeight(200)
                .noCookies()
                .addKeyValue("version", "4.3");

        adView2.loadAd(request2,
                new CompletionHandler() {
                    @Override
                    public void onComplete(int adCount) {
                        Toast.makeText(getApplicationContext(),"adView2 loadAd Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d("MainActivity.adView2", "adView2 loadAd Failure: " + error);
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
        if (adView != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
            adView.removeAllViews();
            adView.destroy();
        }

        if (adView2 != null) {
            ((ViewGroup) adView2.getParent()).removeView(adView2);
            adView2.removeAllViews();
            adView2.destroy();
        }

        super.onDestroy();
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
