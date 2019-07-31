package com.example.example1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.adnuntius.sdk.lib.AdView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdView.adScript = "<html>\n" +
                "        <head />\n" +
                "        <body>\n" +
                "        <div id=\"adn-0000000000042bf0\" style=\"display:none\"></div>\n" +
                "        <script type=\"text/javascript\">(function(d, s, e, t) { e = d.createElement(s); e.type = 'text/java' + s; e.async = 'async'; e.src = 'http' + ('https:' === location.protocol ? 's' : '') + '://cdn.adnuntius.com/adn.js'; t = d.getElementsByTagName(s)[0]; t.parentNode.insertBefore(e, t); })(document, 'script');window.adn = window.adn || {}; adn.calls = adn.calls || []; adn.calls.push(function() { adn.request({ adUnits: [ {auId: '0000000000042bf0', auW: 320, auH: 480 } ]}); });</script>\n" +
                "        </body>\n" +
                "        </html>";
        setContentView(R.layout.activity_main);
    }
}
