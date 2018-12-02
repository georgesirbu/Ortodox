package com.venombit.ortodox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class jurnal extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navAudio:
                    startActivity(new Intent(jurnal.this, playlist_audio.class));
                    finish();
                    return true;
                //case R.id.navJurnal:
                   //startActivity(new Intent(jurnal.this, jurnal.class));
                    //finish();
                    //return true;
                case R.id.navFavorite:
                    startActivity(new Intent(jurnal.this, playlist_preferiti.class));
                    finish();
                    return true;
                case R.id.navRadio:
                    //destroymPlayer();
                    startActivity(new Intent(jurnal.this, radio.class));
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jurnal);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().findItem(R.id.navJurnal).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation2).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation3).setChecked(false);
        setTitle("Jurnal");

        WebView htmlWebView = (WebView)findViewById(R.id.webview);
        htmlWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = htmlWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        htmlWebView.loadUrl("http://venombit.com/Ortodox/jurnal/index.html");


    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
