package com.georgesirbu.ortodox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Biblioteca extends AppCompatActivity {

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
                    startActivity(new Intent(Biblioteca.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation1:
                    destroymPlayer();
                    startActivity(new Intent(Biblioteca.this, Personal.class));
                    finish();
                    return true;
                case R.id.navigation3:
                    destroymPlayer();
                    startActivity(new Intent(Biblioteca.this, Biblioteca.class));
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

    private SeekBar barraAudio;
    private TextView lblRiproduzzione;

    public  String[] linksCat;

    public String[] parts;
    public String[] data ;
    public String[] links ;
    public String[] favoritesData;
    public String[] favoritesLinks;

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
        setContentView(R.layout.activity_biblioteca);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setTitle("Favorite");
        //getActionBar().setIcon(R.drawable.preferitimenu);

        //navigation.getMenu().findItem(R.id.navigation1).setChecked(false);
        //navigation.getMenu().findItem(R.id.navigation2).setChecked(false);
        navigation.getMenu().findItem(R.id.navigation3).setChecked(true);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        final FloatingActionButton mButtonPlay = findViewById(R.id.btnplay);
        final FloatingActionButton mButtonSx = findViewById(R.id.btnsx);
        final FloatingActionButton mButtonDx = findViewById(R.id.btndx);
        final FloatingActionButton mButtonRemove = findViewById(R.id.btnFavorite);
        final FloatingActionButton mButtonShare = findViewById(R.id.btnShare);



        barraAudio = findViewById(R.id.barRiproduzione);

        lblRiproduzzione = findViewById(R.id.lblRiproduzzione);

        mButtonSx.setImageResource(R.drawable.butoninapoi);
        mButtonDx.setImageResource(R.drawable.butoninainte);
        mButtonPlay.setImageResource(R.drawable.butonplay);


        // Get the application context
        mContext = getApplicationContext();
        mActivity = Biblioteca.this;

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

        barraAudio.setVisibility(View.INVISIBLE);

        caricamentoListaAudio();

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

        mButtonRemove.setOnClickListener(new View.OnClickListener(){
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

                oldFavorites = oldFavorites.replace(selectedName + ">" + selectedLink + ">","");

                try {

                    File gpxfile = new File(dirPath, "favoriteList.lst");
                    FileWriter writer = new FileWriter(gpxfile);

                    writer.append(oldFavorites);//testo

                    writer.flush();
                    writer.close();

                    Toast.makeText(Biblioteca.this, "Scos de la Favorite.", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                caricamentoListaAudio();

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
                String shareBody = "http://venombit.com/Ortodox/index.php?#Intent;scheme=http;package=com.georgesirbu.ortodox;S.namestring="+linkToShare+";end;";//selectedLink;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Trimite audio");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Trimite cu .."));

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
                                barraAudio.setVisibility(View.VISIBLE);
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
                            barraAudio.setVisibility(View.VISIBLE  );
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

        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "ortodox";

        String line="";


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

        listaMedia = text.toString();

        if (!listaMedia.isEmpty()) {

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

            //String[] data=new String[]{"Torino","Roma","Milano","Napoli","Firenze"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Biblioteca.this, R.layout.single_row, R.id.textView, data);

            listView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            listView.setAdapter(adapter);

        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fineHTTP = 1;
    }


}




