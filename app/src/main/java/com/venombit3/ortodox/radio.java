package com.venombit3.ortodox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class radio extends AppCompatActivity {

    private TextView mTextMessage;

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navAudio:
                    destroymPlayer();
                    startActivity(new Intent(radio.this, playlist_audio.class));
                    finish();
                    return true;
                case R.id.navJurnal:
                    destroymPlayer();
                    startActivity(new Intent(radio.this, calendar.class));
                    finish();
                    return true;
                case R.id.navFavorite:
                    destroymPlayer();
                    startActivity(new Intent(radio.this, playlist_preferiti.class));
                    finish();
                    return true;
                //case R.id.navRadio:
                    //destroymPlayer();
                    //startActivity(new Intent(radio.this, radio.class));
                    //finish();
                    //return true;
                case R.id.navTv:
                    destroymPlayer();
                    startActivity(new Intent(radio.this, tv.class));
                    finish();
                    return true;
                //case R.id.navCalendar:
                //    destroymPlayer();
                //    startActivity(new Intent(playlist_preferiti.this, calendar.class));
                //    finish();
                //    return true;
            }
            return false;
        }
    };

    public String webhosting = "http://venombit.com";
    public String webListe = "/Ortodox/liste/";

    public String listaMedia="";

    //TODO: RESOLVE THIS SHIT EMI
    public String linkListaMedia = webhosting + webListe +"radio.lst";

    MediaPlayer mPlayer;

    Boolean played = false;
    String selectedLink = "";
    String selectedName = "";
    int positionLink;

    int fineHTTP = 0;
    int fineHTTP2 = 0;

    private TextView lblRiproduzzione;

    public String[] parts;
    public String[] data ;
    public String[] links ;

    public String  ultimoLink;

    public  boolean skiped = false;

    public String audioUrl;

    public ListView listView;

    public int listCount;

    private FloatingActionButton mButtonPlay;
    private FloatingActionButton mButtonSx ;
    private FloatingActionButton mButtonDx ;

    private Handler mHandler;
    private Runnable mRunnable;

    boolean connected = false;

    private InterstitialAd mInterstitialAd;
    private String appId ;
    private String appUnitId;
    private String appBannerUnitId;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().findItem(R.id.navRadio).setChecked(true);


        appId = getString(R.string.adMobID);
        appUnitId = getString(R.string.adMobUnitID);
        appBannerUnitId = getString(R.string.adMobBannerUnitID);

        //FIREBASE INSTANCE
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //fullscreen ads
        Log.d("PUBLICITA", "ON CREATE:");
        //Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, appId);
        Log.d("PUBLICITA", "INIZIALIZZO APP ID: " + appId);
        //banner ads
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        Log.d("PUBLICITA", "BANNER OK " + appBannerUnitId );
        //getString(R.string.adMobID)
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(appUnitId);
        Log.d("PUBLICITA", "APP UNIT ID: " +  appUnitId);
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("23441BE60D3215786403931AB7F74983").build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });


        setTitle("Radio");

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
            startActivity(new Intent(radio.this, networkState.class));
            finish();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);

        mButtonPlay = findViewById(R.id.btnplay);
        mButtonSx = findViewById(R.id.btnsx2);
        mButtonDx = findViewById(R.id.btndx2);

        mButtonSx.setImageResource(R.drawable.butoninapoi);
        mButtonDx.setImageResource(R.drawable.butoninainte);
        mButtonPlay.setImageResource(R.drawable.play);

        mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.btnDefault));

        lblRiproduzzione = findViewById(R.id.lblRiproduzzione);

        // Initialize the handler
        mHandler = new Handler();

        listView = findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    mPlayer.stop();
                    if(mHandler!=null){
                        mHandler.removeCallbacks(mRunnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mPlayer != null) {
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                }else
                {

                }

                mButtonPlay.setImageResource(R.drawable.play);
                //mButtonPlay.setImageResource(R.drawable.playdefault);
                mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.btnDefault));

                played = false;


                selectedLink = links[+position];
                positionLink = position;
                selectedName = data[+position];
                skiped = false;

                //checkFavorite();

                mButtonPlay.performClick();

            }
        });

        mButtonSx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (+positionLink < listView.getCount()) {

                    try {
                        mPlayer.stop();
                        if(mHandler!=null){
                            mHandler.removeCallbacks(mRunnable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mPlayer != null) {
                        mPlayer.reset();
                        mPlayer.release();
                        mPlayer = null;
                    } else {

                    }


                    played = false;

                    if (positionLink > 0) {
                        positionLink = +positionLink - 1;
                        selectedLink = links[positionLink];
                        selectedName = data[+positionLink];
                    }

                    selectedLink = links[positionLink];
                    selectedName = data[+positionLink];
                    listView.setItemChecked(positionLink, true);
                    skiped = true;
                    mButtonPlay.performClick();

                }

            }
        });


        mButtonDx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (+positionLink < listView.getCount()) {


                    try {
                        mPlayer.stop();
                        if(mHandler!=null){
                            mHandler.removeCallbacks(mRunnable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mPlayer != null) {
                        mPlayer.reset();
                        mPlayer.release();
                        mPlayer = null;
                    } else {

                    }


                    played = false;

                    listCount = listView.getAdapter().getCount();

                    if (positionLink < listCount - 1) {
                        positionLink = +positionLink + 1;
                        selectedLink = links[positionLink];
                        selectedName = data[+positionLink];
                    }


                    selectedLink = links[positionLink];
                    selectedName = data[+positionLink];
                    listView.setItemChecked(positionLink, true);
                    skiped = true;
                    mButtonPlay.performClick();

                }
            }
        });

        mButtonPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(played == false ) {

                    if ((ultimoLink != selectedLink) || (skiped == true))
                    {

                        // Initialize a new media player instance
                        mPlayer = new MediaPlayer();
                        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

                        mPlayer.setScreenOnWhilePlaying(true);

                        WifiManager.WifiLock wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

                        wifiLock.acquire();

                        new AsyncTask<String, Integer, String>() {
                            protected void onPreExecute() {

                                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.adMobTestDevice)).build());
                                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                Log.d("PUBLICITA", "AD LOADED");

                                // do pre execute stuff...
                                skiped = false;
                                progressDialog.setTitle("Radio");
                                progressDialog.setMessage("Se incarca...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                // The audio url to play
                                audioUrl = selectedLink;
                                // Set the media player audio stream type
                                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            }

                            protected String doInBackground(String... params) {
                                // do background stuff...
                                //Try to play music/audio from url
                                try{
                                    mPlayer.setDataSource(audioUrl.replaceAll(" ","%20"));
                                    // Set the audio data source
                                    //mPlayer.setDataSource(audioUrl);
                                    // Prepare the media player
                                    mPlayer.prepare();

                                }catch (IOException e){
                                    // Catch the exception
                                    e.printStackTrace();
                                    //loadingDialog.dismiss();
                                }catch (IllegalArgumentException e){
                                    e.printStackTrace();
                                    //loadingDialog.dismiss();
                                }catch (SecurityException e){
                                    e.printStackTrace();
                                    //loadingDialog.dismiss();
                                }catch (IllegalStateException e){
                                    e.printStackTrace();
                                    //loadingDialog.dismiss();
                                }
                                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        //Toast.makeText(mContext,"End",Toast.LENGTH_SHORT).show();
                                        mButtonPlay.setImageResource(R.drawable.play);
                                        played = false;

                                        //mButtonDx.performClick();
                                    }
                                });
                                return "";
                            }
                            protected void onPostExecute(String result) {
                                // do post execute stuff...
                                if (progressDialog != null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                //barraAudio.setVisibility(View.VISIBLE);
                                mPlayer.start();
                                // Get the current audio stats
                                getAudioStats();
                                // Initialize the seek bar
                                //initializeSeekBar();
                                mButtonPlay.setImageResource(R.drawable.pause);
                                //mButtonPlay.setImageResource(R.drawable.playdefault);
                                mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                                ultimoLink = selectedLink;
                                lblRiproduzzione.setText(selectedName);
                                played = true;
                                skiped = false;


                                //PUBLICITA

                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                    Log.d("PUBLICITA", "AD STARTED");
                                } else {
                                    Log.d("PUBLICITA", "The interstitial wasn't loaded yet.");
                                }


                            }
                        }.execute();

                    }else
                    {

                        if (mPlayer == null){
                            ultimoLink ="";
                            mButtonPlay.performClick();
                        }else
                        {
                            mPlayer.start();
                            //barraAudio.setVisibility(View.VISIBLE);
                        }

                    }

                    mButtonPlay.setImageResource(R.drawable.pause);
                    //mButtonPlay.setImageResource(R.drawable.playdefault);
                    mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                    ultimoLink = selectedLink;
                    played = true;
                    skiped = false;

                }else
                {
                    mPlayer.pause();
                    mButtonPlay.setImageResource(R.drawable.play);
                    //mButtonPlay.setImageResource(R.drawable.playdefault);
                    mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.btnDefault));

                    played = false;
                    skiped = false;
                }

            }
        });

        if (connected) {
            caricamentoListaAudio();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroymPlayer();
    }

    @Override
    public void onBackPressed() {
        destroymPlayer();
        startActivity(new Intent(radio.this, playlist_audio.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void destroymPlayer()
    {
        try {
            mPlayer.stop();
            if(mHandler!=null){
                mHandler.removeCallbacks(mRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }else
        {

        }
    }

    protected void getAudioStats(){
        int duration  = mPlayer.getDuration()/1000; // In milliseconds
        int due = (mPlayer.getDuration() - mPlayer.getCurrentPosition())/1000;
        int pass = duration - due;

        //mPass.setText("" + pass + " seconds");
        //mDuration.setText("" + duration + " seconds");
        //mDue.setText("" + due + " seconds");

    }

    public void caricamentoListaAudio()
    {


        //if (isNetworkConnected()) {
        ReadAudioList tsk = new ReadAudioList();
        tsk.execute(linkListaMedia);

    }

    private class ReadAudioList extends AsyncTask<String,Integer,String> {

        protected String doInBackground(String... params) {
            URL url;
            try {
                //create url object to point to the file location on internet
                url = new URL(params[0]);
                //make a request to server
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //get InputStream instance
                InputStream is = con.getInputStream();
                //create BufferedReader object
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                //read content of the file line by line
                while ((line = br.readLine()) != null) {
                    listaMedia += line;

                }

                br.close();

            } catch (Exception e) {
                e.printStackTrace();
                //close dialog if error occurs
            }

            fineHTTP = 1;
            return "Ok";

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            fineHTTP = 1;

            parts = listaMedia.split(">");

            int size = parts.length;
            data = new String[size/2];
            links = new String[size/2];

            int n = 0;
            int l = 0;

            for (int i=0; i<size; i++) {

                int p = 2;

                int resto = i % p;

                if (resto == 0) {
                    data[n] = parts[i];
                    n++;
                } else {
                    links[l] = parts[i];
                    l++;
                }

            }

            //String[] data=new String[]{"Torino","Roma","Milano","Napoli","Firenze"};
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(radio.this, R.layout.single_row_radio, R.id.textView, data);

            listView.setAdapter(adapter);

            fineHTTP = 0;

            //Toast.makeText(playlist_audio.this, "Data->" + sharedLink + "<-",Toast.LENGTH_LONG).show();

            listaMedia= "";

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            fineHTTP = 1;
        }
    }


}
