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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class calendar extends AppCompatActivity {

    public String webhosting = "https://ortodox.cgesoft.it";
    public String webCalendar = "/Ortodox/calendar/";
    public String webListe = "/Ortodox/liste/";


    public String listaCalendar="";
    public String linkCalendar="";

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navAudio:
                    //destroymPlayer();
                    startActivity(new Intent(calendar.this, playlist_audio.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                //case R.id.navJurnal:
                //destroymPlayer();
                //  startActivity(new Intent(calendar.this, jurnal.class));
                // finish();
                // return true;
                case R.id.navFavorite:
                    //destroymPlayer();
                    startActivity(new Intent(calendar.this, playlist_preferiti.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.navRadio:
                    //destroymPlayer();
                    startActivity(new Intent(calendar.this, radio.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.navTv:
                    //destroymPlayer();
                    startActivity(new Intent(calendar.this, tv.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                //case R.id.navCalendar:
                //   destroymPlayer();
                //  startActivity(new Intent(calendar.this, calendar.class));
                //  finish();
                //  return true;
            }
            return false;
        }
    };

    int fineHTTP2 = 0;

    private List<Grocery> groceryList = new ArrayList<>();
    private RecyclerView groceryRecyclerView;
    private RecyclerViewHorizontalListAdapter groceryAdapter;

    public  String[] linksCat;
    public int positonCat;

    public String[] parts;
    public String[] data ;
    public String[] links ;



    public  String categoriaName;
    public  int idAudioRilevato;

    boolean connected = false;

    private InterstitialAd mInterstitialAd;
    private String appId ;
    private String appUnitId;

    public WebView htmlWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
            startActivity(new Intent(calendar.this, networkState.class));
            finish();
        }

        //FIREBASE INSTANCE
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //CLOUD NOTIFICATION FIREBASE

        // Get token
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("NOTIFICATION: ", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
                String msg = token;
                Log.d("NOTIFICATION", msg);
                //Toast.makeText(playlist_audio.this, msg, Toast.LENGTH_SHORT).show();
            }
        });


        setTitle("Calendar");

        navigation.getMenu().findItem(R.id.navJurnal).setChecked(true);

        groceryRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);

        // add a divider after each item for more clarity
        groceryRecyclerView.addItemDecoration(new DividerItemDecoration(calendar.this, LinearLayoutManager.HORIZONTAL));
        groceryAdapter = new RecyclerViewHorizontalListAdapter(groceryList, getApplicationContext());

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(calendar.this, LinearLayoutManager.HORIZONTAL, false);
        groceryRecyclerView.setLayoutManager(horizontalLayoutManager);
        groceryRecyclerView.setAdapter(groceryAdapter);

        if (connected) {
            populategroceryList();

            htmlWebView = (WebView)findViewById(R.id.webViewCalendar);
            htmlWebView.setWebViewClient(new CustomWebViewClient());
            WebSettings webSetting = htmlWebView.getSettings();
            webSetting.setJavaScriptEnabled(true);
            webSetting.setAppCacheEnabled(false);
            webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH);

            if (month+1 == 12)
            {

                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Decembrie.html");


            }else if (month+1 == 11)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Noiembrie.html");

            }else if (month+1 == 10)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Octombrie.html");

            }
            else if (month+1 == 9)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Septembrie.html");
            }
            else if (month+1 == 8)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/August.html");
            }
            else if (month+1 == 7)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Iulie.html");
            }
            else if (month+1 == 6)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Iunie.html");
            }
            else if (month+1 == 5)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Mai.html");
            }
            else if (month+1 == 4)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Aprilie.html");
            }
            else if (month+1 == 3)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Martie.html");
            }
            else if (month+1 == 2)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Februarie.html");
            }
            else if (month+1 == 1)
            {
                htmlWebView.loadUrl("https://ortodox.cgesoft.it/Ortodox/calendar/Ianuarie.html");
            }


            //linkCalendar;

        }

    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public String[] populategroceryList(){

        listaCalendar = "";

        ReadCategorieList tsk = new ReadCategorieList();
        tsk.execute(webhosting+webCalendar+"calendar.lst");

        return linksCat;


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
                    listaCalendar += line;

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

          try{


            while(fineHTTP2==0) {
                //Log.d("LISTA CATEGORIE: ", listaCategorie);
            }

              String separatore = ">";
              if (listaCalendar.toLowerCase().contains(separatore.toLowerCase())) {


                  String[] categories = listaCalendar.split(">");
                  int size = categories.length;

                  if(size>0) {

                      final String[] dataCat = new String[size / 2];
                      linksCat = new String[size / 2];
                      int n = 0;
                      int l = 0;

                      for (int i = 0; i < size; i++) {

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

                      for (int i = 0; i < sizeCat; i++) {


                          Grocery categorie = new Grocery(dataCat[i], R.drawable.imgcalendar);
                          groceryList.add(categorie);

                      }

                      groceryAdapter.notifyDataSetChanged();

                      fineHTTP2 = 0;
                  }
              }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(calendar.this, "Avem o mica problema, redeschideti aplicatia. " , Toast.LENGTH_LONG).show();

        }

        }



        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            fineHTTP2 = 1;
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(calendar.this, playlist_audio.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
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
                    linkCalendar = linksCat[position];

                    htmlWebView.loadUrl(linkCalendar);

                    //PUBLICITA
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.adMobTestDevice)).build());
                    //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Log.d("PUBLICITA", "AD LOADED");

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
