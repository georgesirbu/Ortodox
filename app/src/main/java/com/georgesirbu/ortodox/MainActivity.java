package com.georgesirbu.ortodox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    public String webhosting = "http://venombit.com";
    public String webCategorii = "/Ortodox/categorii/";
    public String webListe = "/Ortodox/liste/";
    public String webMedia = "";
    public String listaMedia="";
    public String linkListaMedia = webhosting + webListe +"acatiste.lst";

    public String listaCategorie="";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation2:
                    destroymPlayer();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation1:
                    destroymPlayer();
                    startActivity(new Intent(MainActivity.this, Personal.class));
                    finish();
                    return true;
                case R.id.navigation3:
                    destroymPlayer();
                    startActivity(new Intent(MainActivity.this, Biblioteca.class));
                    finish();
                    return true;
            }
            return false;
        }
    };

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

    private Context mContext;
    private Activity mActivity;

    private LinearLayout mRootLayout;
    private Button mButtonPlay;

    MediaPlayer mPlayer;

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

    public String[] parts;
    public String[] data ;
    public String[] links ;

    public ListView listView;

    public String  ultimoLink;

    public int listCount;

    public  boolean skiped = false;

    public String audioUrl;

    public  String categoriaName;
    private Handler mHandler;
    private Runnable mRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
            startActivity(new Intent(MainActivity.this, networkState.class));
            finish();
        }

        setTitle("Ortodox");
        //getActionBar().setIcon(R.drawable.preferitimenu);

        //navigation.getMenu().findItem(R.id.navigation1).setChecked(true);
        navigation.getMenu().findItem(R.id.navigation2).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation3).setChecked(false);

        groceryRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);

        // add a divider after each item for more clarity
        groceryRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.HORIZONTAL));
        groceryAdapter = new RecyclerViewHorizontalListAdapter(groceryList, getApplicationContext());


        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        groceryRecyclerView.setLayoutManager(horizontalLayoutManager);
        groceryRecyclerView.setAdapter(groceryAdapter);

        if (connected) {
            populategroceryList();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);

        final FloatingActionButton mButtonPlay = findViewById(R.id.btnplay);
        final FloatingActionButton mButtonSx = findViewById(R.id.btnsx);
        final FloatingActionButton mButtonDx = findViewById(R.id.btndx);
        final FloatingActionButton mButtonFavorite = findViewById(R.id.btnFavorite);


        barraAudio = findViewById(R.id.barRiproduzione);

        lblRiproduzzione = findViewById(R.id.lblRiproduzzione);

        mButtonSx.setImageResource(R.drawable.butoninapoi);
        mButtonDx.setImageResource(R.drawable.butoninainte);
        mButtonPlay.setImageResource(R.drawable.butonplay);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;

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

        if (connected){
        caricamentoListaAudio();
        }

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


                mButtonPlay.setImageResource(R.drawable.butonplay);
                played = false;


                selectedLink = links[+position];
                positionLink = position;
                selectedName = data[+position];
                skiped = false;
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

        mButtonFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String dirPath = getFilesDir().getAbsolutePath() + File.separator + "ortodox";

                String line="";
                String oldFavorites="";

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
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }

                oldFavorites = text.toString();

                File projDir = new File(dirPath);
                if (!projDir.exists()) {
                    projDir.mkdirs();
                }

                try {

                    File gpxfile = new File(dirPath, "favoriteList.lst");
                    FileWriter writer = new FileWriter(gpxfile);

                    if (oldFavorites.isEmpty())
                    {
                        writer.append(selectedName + ">" + selectedLink);
                    }else
                        {
                            writer.append(oldFavorites + ">"+selectedName + ">" + selectedLink);
                        }

                    writer.flush();
                    writer.close();

                    Toast.makeText(MainActivity.this, "Adaugat la Favorite.", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
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

                        new AsyncTask<String, Integer, String>() {
                            protected void onPreExecute() {
                                // do pre execute stuff...
                                skiped = false;
                                progressDialog.setTitle(categoriaName);
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
                                    // Set the audio data source
                                    mPlayer.setDataSource(audioUrl);
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
                                        mButtonPlay.setImageResource(R.drawable.butonplay);
                                        played = false;
                                        mButtonSx.performClick();
                                    }
                                });
                                return "";
                            }
                            protected void onPostExecute(String result) {
                                // do post execute stuff...
                                if (progressDialog != null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                mPlayer.start();
                                // Get the current audio stats
                                getAudioStats();
                                // Initialize the seek bar
                                initializeSeekBar();
                                mButtonPlay.setImageResource(R.drawable.butonpausa);
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
                            }

                    }

                    mButtonPlay.setImageResource(R.drawable.butonpausa);
                    ultimoLink = selectedLink;
                    played = true;
                    skiped = false;

                }else
                {
                    mPlayer.pause();
                    mButtonPlay.setImageResource(R.drawable.butonplay);
                    played = false;
                    skiped = false;
                }

            }
        });

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
        listaMedia= "";

        //if (isNetworkConnected()) {
        ReadFileTask tsk = new ReadFileTask();
        tsk.execute(linkListaMedia);

    }

    public String[] populategroceryList(){

        listaCategorie = "";

        ReadCategorieFileTask tsk = new ReadCategorieFileTask();
        tsk.execute(webhosting+webCategorii+"categorie.cat");



        return linksCat;


    }

    private class ReadFileTask extends AsyncTask<String,Integer,String> {

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
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, R.layout.single_row, R.id.textView, data);

            listView.setAdapter(adapter);

            fineHTTP = 0;

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            fineHTTP = 1;
        }
    }

    private class ReadCategorieFileTask extends AsyncTask<String,Integer,String> {

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
                Log.d("LISTA CATEGORIE: ", listaCategorie);
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

                Grocery categorie = new Grocery(dataCat[i], R.drawable.playlist);
                groceryList.add(categorie);

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

                    linkListaMedia = linksCat[position];
                    caricamentoListaAudio();

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
