package com.adnuntius.jsonexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.adnuntius.sdk.lib.AdView;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdView.jsonConfig = "{\"siteId\": \"wsxcqfrnmxd95lm2\", \"adUnits\": [{\"auId\": \"0000000000023ae5\"}]}";
        setContentView(R.layout.activity_main);
    }
}
