package com.venombit3.ortodox;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

public class tv extends AppCompatActivity {

    private TextView mTextMessage;
    private FirebaseAnalytics mFirebaseAnalytics;

    boolean connected = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navAudio:
                    startActivity(new Intent(tv.this, playlist_audio.class));
                    finish();
                    return true;
                case R.id.navJurnal:
                   startActivity(new Intent(tv.this, calendar.class));
                    finish();
                    return true;
                case R.id.navFavorite:
                    startActivity(new Intent(tv.this, playlist_preferiti.class));
                    finish();
                    return true;
                case R.id.navRadio:
                    //destroymPlayer();
                    startActivity(new Intent(tv.this, radio.class));
                    finish();
                    return true;
                case R.id.navTv:
                   // startActivity(new Intent(tv.this, tv.class));
                   // finish();
                   // return true;
                    // case R.id.navCalendar:
                    //    destroymPlayer();
                    //    startActivity(new Intent(playlist_preferiti.this, calendar.class));
                    //    finish();
                    //    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tv);

        getSupportActionBar().hide();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().findItem(R.id.navTv).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation2).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation3).setChecked(false);
        setTitle("Trinitas TV");

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
            startActivity(new Intent(tv.this, networkState.class));
            finish();
        }

        //FIREBASE INSTANCE
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        WebView htmlWebView = (WebView)findViewById(R.id.webview);
        htmlWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = htmlWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAppCacheEnabled(false);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        htmlWebView.loadUrl("http://venombit.com/Ortodox/tv/index.html");
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(tv.this, playlist_audio.class));
        finish();
    }


}

