package com.adnuntius.simple;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adnuntius.android.sdk.AdRequest;
import com.adnuntius.android.sdk.AdnuntiusAdWebView;
import com.adnuntius.android.sdk.CompletionHandler;


public class MainActivity extends AppCompatActivity {
    private AdnuntiusAdWebView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.adView = findViewById(R.id.adView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adView.loadBlankPage();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Click Ok to load the Ad");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loadAd();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadAd() {
        AdRequest request = new AdRequest("000000000006f450")
                .setWidth(320)
                .setHeight(480)
                .noCookies()
                //.livePreview("7pmy5r9rj62fyhjm", "9198pft3cvktmg8d")
                .addKeyValue("version", "interstitial2")
                ;

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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adView != null) {
            adView.saveState(outState);
        }
    }
}
