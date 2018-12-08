package com.venombit3.ortodox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class playlist_audio extends AppCompatActivity {

    public String webhosting = "http://venombit.com";
    public String webCategorii = "/Ortodox/categorii/";
    public String webListe = "/Ortodox/liste/";
    public String webMedia = "";
    public String listaMedia="";

    View updateview;// above oncreate method

    //TODO: RESOLVE THIS SHIT EMI
    public String linkListaMedia = webhosting + webListe +"colinde.lst";

    public String listaCategorie="";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //case R.id.navAudio:
                //destroymPlayer();
                //startActivity(new Intent(playlist_audio.this, playlist_audio.class));
                //finish();
                //return true;
                case R.id.navJurnal:
                    destroymPlayer();
                    startActivity(new Intent(playlist_audio.this, jurnal.class));
                    finish();
                    return true;
                case R.id.navFavorite:
                    destroymPlayer();
                    startActivity(new Intent(playlist_audio.this, playlist_preferiti.class));
                    finish();
                    return true;
                case R.id.navRadio:
                    destroymPlayer();
                    startActivity(new Intent(playlist_audio.this, radio.class));
                    finish();
                    return true;
            }
            return false;
        }
    };

    public void destroymPlayer()
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

    private Context mContext;
    private Activity mActivity;

    private LinearLayout mRootLayout;
    private FloatingActionButton mButtonPlay;
    private FloatingActionButton mButtonSx ;
    private FloatingActionButton mButtonDx ;
    private FloatingActionButton mButtonFavorite;
    private FloatingActionButton mButtonShare ;

    public MediaPlayer mPlayer;

    Boolean played = false;

    String selectedLink = "";
    String selectedName = "";
    int positionLink;

    int fineHTTP = 0;
    int fineHTTP2 = 0;

    private List<Grocery> groceryList = new ArrayList<>();
    private RecyclerView groceryRecyclerView;
    private RecyclerViewHorizontalListAdapter groceryAdapter;

    private SeekBar barraAudio;
    private TextView lblRiproduzzione;

    public  String[] linksCat;
    public int positonCat;

    public String[] parts;
    public String[] data ;
    public String[] links ;

    public ListView listView;

    public String  ultimoLink;

    public int listCount;

    public boolean skiped = false;

    public String audioUrl;

    public  String categoriaName;
    private Handler mHandler;
    private Runnable mRunnable;

    public String sharedLink;
    public  int idAudioRilevato;

    boolean connected = false;

    private InterstitialAd mInterstitialAd;
    private String appId ;
    private String appUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.CAMERA
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        appId = getString(R.string.adMobID);
        appUnitId = getString(R.string.adMobUnitID);

        Log.d("PUBLICITA", "ON CREATE:");
        //Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, appId);
        Log.d("PUBLICITA", "APP ID: " + appId);
        //getString(R.string.adMobID)
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(appUnitId);
        Log.d("PUBLICITA", "APP UNIT ID: " +  appUnitId);
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("23441BE60D3215786403931AB7F74983").build());

        //-> TESST ADS
        //MediationTestSuite.launch(playlist_audio.this, appId);

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


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
            startActivity(new Intent(playlist_audio.this, networkState.class));
            finish();
        }

        setTitle("Audio");
        //getActionBar().setIcon(R.drawable.preferitimenu);

        navigation.getMenu().findItem(R.id.navAudio).setChecked(true);

        groceryRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);

        // add a divider after each item for more clarity
        groceryRecyclerView.addItemDecoration(new DividerItemDecoration(playlist_audio.this, LinearLayoutManager.HORIZONTAL));
        groceryAdapter = new RecyclerViewHorizontalListAdapter(groceryList, getApplicationContext());

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(playlist_audio.this, LinearLayoutManager.HORIZONTAL, false);
        groceryRecyclerView.setLayoutManager(horizontalLayoutManager);
        groceryRecyclerView.setAdapter(groceryAdapter);

        if (connected) {
            populategroceryList();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);

        mButtonPlay = findViewById(R.id.btnplay);
        mButtonSx = findViewById(R.id.btnsx);
        mButtonDx = findViewById(R.id.btndx);
        mButtonFavorite = findViewById(R.id.btnFavorite);
        mButtonShare = findViewById(R.id.btnShare);

        barraAudio = findViewById(R.id.barRiproduzione);

        barraAudio.setVisibility(View.INVISIBLE);

        lblRiproduzzione = findViewById(R.id.lblRiproduzzione);

        mButtonSx.setImageResource(R.drawable.butoninapoi);
        mButtonDx.setImageResource(R.drawable.butoninainte);
        mButtonPlay.setImageResource(R.drawable.play);

        mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.btnDefault));

        //get deep link
        //getDeepLink();

        // Get the application context
        mContext = getApplicationContext();
        mActivity = playlist_audio.this;

        // Initialize the handler
        mHandler = new Handler();

        // Set a change listener for seek bar
        barraAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar barraAudio, int i, boolean b) {
                if(mPlayer!=null && b){

                    mPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar barraAudio) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar barraAudio) {

            }

        });

        listView = findViewById(R.id.listview);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

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
                mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.btnDefault));

                //mButtonPlay.setImageResource(R.drawable.playdefault);
                //mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

                played = false;

                selectedLink = links[+position];
                positionLink = position;
                selectedName = data[+position];
                skiped = false;

                checkFavorite();

                mButtonPlay.performClick();

            }
        });

        mButtonShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String linkToShare = selectedLink;
                linkToShare = linkToShare.replaceAll(" ", "%20");

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                //intent://www.venombit.com/Ortodox#Intent;scheme=http;package=com.georgesirbu.ortodox;S.namestring="+linkToShare+";end;
                String shareBody = "http://venombit.com/Ortodox/index.php?#Intent;scheme=http;package=com.venombit3.ortodox;S.namestring="+linkToShare+";end;";//selectedLink;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Trimite audio");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Trimite cu .."));

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
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listView.setItemChecked(positionLink, true);
                    skiped = true;
                    mButtonPlay.performClick();

                }
            }
        });

        mButtonFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                String dirPath = getFilesDir().getAbsolutePath() + File.separator + "ortodox";

                String line = "";
                String oldFavorites = "";

                //Get the text file
                File file = new File(dirPath, "favoriteList.lst");

                //Read text from file
                StringBuilder text = new StringBuilder();


                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        //text.append('\n');
                    }
                    br.close();
                } catch (IOException e) {
                    //You'll need to add proper error handling here
                }

                oldFavorites = text.toString();

                File projDir = new File(dirPath);
                if (!projDir.exists()) {
                    projDir.mkdirs();
                }

                if (checkFavorite() == false) {


                    try {

                        File gpxfile = new File(dirPath, "favoriteList.lst");
                        FileWriter writer = new FileWriter(gpxfile);

                        if (oldFavorites.isEmpty()) {
                            writer.append(selectedName + ">" + selectedLink + ">");
                        } else {
                            writer.append(oldFavorites + selectedName + ">" + selectedLink + ">");
                        }

                        writer.flush();
                        writer.close();

                        Toast.makeText(playlist_audio.this, "Adaugat la Favorite.", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else
                {

                    oldFavorites = oldFavorites.replace(selectedName + ">" + selectedLink + ">","");

                    try {

                        File gpxfile = new File(dirPath, "favoriteList.lst");
                        FileWriter writer = new FileWriter(gpxfile);

                        writer.append(oldFavorites);//testo

                        writer.flush();
                        writer.close();

                        Toast.makeText(playlist_audio.this, "Scos de la Favorite.", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                checkFavorite();

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

                        new AsyncTask<String, Integer, String>() {
                            protected void onPreExecute() {

                                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.adMobTestDevice)).build());
                                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                Log.d("PUBLICITA", "AD LOADED");

                                // do pre execute stuff...
                                skiped = false;
                                progressDialog.setTitle(categoriaName);
                                progressDialog.setMessage("Se incarca...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                // The audio url to play
                                audioUrl = selectedLink;

                            }

                            protected String doInBackground(String... params) {
                                // do background stuff...
                                //Try to play music/audio from url
                                try{
                                    // Set the media player audio stream type
                                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    //String encodedPath = URLEncoder.encode(audioUrl, "UTF-8");
                                    // Set the audio data source
                                    mPlayer.setDataSource(audioUrl.replaceAll(" ","%20"));
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
                                        mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.btnDefault));

                                        played = false;

                                        if (mInterstitialAd.isLoaded()) {
                                            mInterstitialAd.show();
                                            Log.d("PUBLICITA", "AD STARTED");
                                        } else {
                                            Log.d("PUBLICITA", "The interstitial wasn't loaded yet.");
                                        }

                                        mButtonDx.performClick();
                                    }
                                });
                                return "";
                            }
                            protected void onPostExecute(String result) {
                                // do post execute stuff...
                                if (progressDialog != null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                barraAudio.setVisibility(View.VISIBLE);
                                mPlayer.start();
                                // Get the current audio stats
                                getAudioStats();
                                // Initialize the seek bar
                                initializeSeekBar();
                                mButtonPlay.setImageResource(R.drawable.pause);
                                //mButtonPlay.setImageResource(R.drawable.playdefault);
                                mButtonPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                                ultimoLink = selectedLink;
                                lblRiproduzzione.setText(selectedName);
                                played = true;
                                skiped = false;

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
                            barraAudio.setVisibility(View.VISIBLE);
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

        if (connected){
            caricamentoListaAudio();
        }

        idAudioRilevato = -1;

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (connected){

            getDeepLink();

            if (sharedLink!=null ) {

                if (sharedLink != "") {
                    caricamentoListaAudio();
                }
            }

        }

    }

    /**
     * To get the latest intent object.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void getDeepLink()
    {

        Intent intent = getIntent();

        if(intent != null) {
            Uri uriData = intent.getData();

            if (uriData != null) {

                //To get scheme.
                String scheme = uriData.getScheme();
                //To get server name.
                String host = uriData.getHost();
                //To get parameter value from the URI.
                sharedLink = uriData.toString();

            }
        }

        if (sharedLink!=null ) {

            if (sharedLink != ""){

                try {
                    String[] separazioneSharedLink = sharedLink.split("namestring=");
                    separazioneSharedLink = separazioneSharedLink[1].split(";end;");

                    if (separazioneSharedLink[0] == "null")
                    {
                        //Toast.makeText(playlist_audio.this, "" + sharedLink + "\n", Toast.LENGTH_LONG).show();
                    }else {

                        sharedLink = separazioneSharedLink[0];
                        sharedLink = sharedLink.replaceAll("%20", " ");

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean checkFavorite()
    {

        if(selectedLink != "") {

            Boolean rest;

            String dirPath = getFilesDir().getAbsolutePath() + File.separator + "ortodox";

            String line = "";
            String oldFavorites = "";

            //Get the text file
            File file = new File(dirPath, "favoriteList.lst");

            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    //text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

            oldFavorites = text.toString();

            if (oldFavorites.toLowerCase().contains(selectedLink.toLowerCase())) {

                mButtonFavorite.setImageResource(R.drawable.like);
                rest = true;

            } else {
                mButtonFavorite.setImageResource(R.drawable.likedefault);
                rest = false;
            }

            return rest;
        }else
        {
            return false;
        }

    }

    protected void initializeSeekBar(){
        barraAudio.setMax(mPlayer.getDuration()/1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mPlayer!=null){
                    int mCurrentPosition = mPlayer.getCurrentPosition()/1000; // In milliseconds
                    barraAudio.setProgress(mCurrentPosition);
                    getAudioStats();
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
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

    public String[] populategroceryList(){

        listaCategorie = "";

        ReadCategorieList tsk = new ReadCategorieList();
        tsk.execute(webhosting+webCategorii+"categorie.cat");

        return linksCat;


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

            if (listaMedia != "") {

                parts = listaMedia.split(">");

                int size = parts.length;
                data = new String[size / 2];
                links = new String[size / 2];

                int n = 0;
                int l = 0;

                for (int i = 0; i < size; i++) {

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

            }
            //String[] data=new String[]{"Torino","Roma","Milano","Napoli","Firenze"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(playlist_audio.this, R.layout.single_row, R.id.textView, data);

            listView.setAdapter(adapter);

            fineHTTP = 0;


            //Toast.makeText(playlist_audio.this, "Data->" + sharedLink + "<-",Toast.LENGTH_LONG).show();

            if (sharedLink!=null)
            {

                //Toast.makeText(playlist_audio.this, "->"+sharedLink+"<-", Toast.LENGTH_LONG).show();

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


                //mButtonPlay.setImageResource(R.drawable.butonplay);
                played = false;

                for (int i = 0; i < links.length; i++) {
                    String thisString = links[i];

                    if (thisString.equals(sharedLink)) {
                        idAudioRilevato = i;
                        break;
                    }
                }

                if (idAudioRilevato > (-1)) {

                    selectedLink = links[+idAudioRilevato];
                    positionLink = idAudioRilevato;
                    selectedName = data[+idAudioRilevato];
                    skiped = false;

                    //mButtonPlay.performClick();

                    mButtonPlay.post(new Runnable() {
                        @Override
                        public void run() {
                            mButtonPlay.performClick();
                        }
                    });

                    sharedLink = null;

                } else {

                    try{
                        positonCat = positonCat + 1;
                        linkListaMedia = linksCat[positonCat];
                        caricamentoListaAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Toast.makeText(playlist_audio.this, "->FINE LOOP CATEGORIE<-", Toast.LENGTH_LONG).show();
                        sharedLink = null;

                    }
                }


            }

            listaMedia= "";

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            fineHTTP = 1;
        }
    }

    private class ReadCategorieList extends AsyncTask<String,Integer,String> {

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
                    listaCategorie += line;

                }

                br.close();

            } catch (Exception e) {
                e.printStackTrace();
                //close dialog if error occurs
            }

            fineHTTP2 = 1;
            return "Ok";

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            fineHTTP2 = 1;

            while(fineHTTP2==0) {
                //Log.d("LISTA CATEGORIE: ", listaCategorie);
            }

            String[] categories = listaCategorie.split(">");
            int size = categories.length;


            final String[] dataCat = new String[size/2];
            linksCat = new String[size/2];
            int n = 0;
            int l = 0;

            for (int i=0; i<size; i++) {

                int p = 2;

                int resto = i % p;

                if (resto == 0) {
                    dataCat[n] = categories[i];
                    n++;
                } else {
                    linksCat[l] = categories[i];
                    l++;
                }

            }

            int sizeCat = dataCat.length;

            for (int i=0; i<sizeCat; i++)
            {

                if (i == 0)
                {
                    Grocery categorie = new Grocery(dataCat[i], R.drawable.colinde);
                    groceryList.add(categorie);
                }else if (i == 1)
                {
                    Grocery categorie = new Grocery(dataCat[i], R.drawable.acatiste);
                    groceryList.add(categorie);
                }else if (i == 2)
                {
                    Grocery categorie = new Grocery(dataCat[i], R.drawable.acatiste);
                    groceryList.add(categorie);
                }else if (i == 3)
                {
                    Grocery categorie = new Grocery(dataCat[i], R.drawable.bible);
                    groceryList.add(categorie);
                }else
                {
                    Grocery categorie = new Grocery(dataCat[i], R.drawable.acatiste);
                    groceryList.add(categorie);
                }



            }

            groceryAdapter.notifyDataSetChanged();

            fineHTTP2 = 0;

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            fineHTTP2 = 1;
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public class RecyclerViewHorizontalListAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalListAdapter.GroceryViewHolder>{
        private List<Grocery> horizontalGrocderyList;
        Context context;

        public RecyclerViewHorizontalListAdapter(List<Grocery> horizontalGrocderyList, Context context){
            this.horizontalGrocderyList= horizontalGrocderyList;
            this.context = context;
        }

        @Override
        public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate the layout file
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_list_grocery_item, parent, false);
            GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(GroceryViewHolder holder, final int position) {
            holder.imageView.setImageResource(horizontalGrocderyList.get(position).getProductImage());
            holder.txtview.setText(horizontalGrocderyList.get(position).getProductName());
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoriaName = horizontalGrocderyList.get(position).getProductName().toString();
                    //Toast.makeText(context, productName + " is selected " + position, Toast.LENGTH_SHORT).show();

                    positonCat = position;
                    linkListaMedia = linksCat[position];
                    caricamentoListaAudio();

                    //PUBLICITA

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        Log.d("PUBLICITA", "AD STARTED");
                    } else {
                        Log.d("PUBLICITA", "The interstitial wasn't loaded yet.");
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalGrocderyList.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView txtview;
            public GroceryViewHolder(View view) {
                super(view);
                imageView=view.findViewById(R.id.idProductImage);
                txtview=view.findViewById(R.id.idProductName);
            }
        }
    }

}
