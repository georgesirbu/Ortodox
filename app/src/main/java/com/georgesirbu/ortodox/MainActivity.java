package com.georgesirbu.ortodox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {


    public String listaMedia="";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };


    private Context mContext;
    private Activity mActivity;

    private LinearLayout mRootLayout;
    private Button mButtonPlay;

    MediaPlayer mPlayer;

    Boolean played = false;

    String selectedLink = "";
    int positionLink;

    int fineHTTP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Muzica");
        progressDialog.setMessage("Se incarca...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        final FloatingActionButton mButtonPlay = findViewById(R.id.btnplay);
        final FloatingActionButton mButtonSx = findViewById(R.id.btnsx);
        final FloatingActionButton mButtonDx = findViewById(R.id.btndx);

        mButtonSx.setImageResource(R.drawable.fastbackward);
        mButtonDx.setImageResource(R.drawable.fastforward);
        mButtonPlay.setImageResource(R.drawable.playarrow);


        final ListView listView = findViewById(R.id.listview);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;




        //if (isNetworkConnected()) {
        ReadFileTask tsk = new ReadFileTask();
        tsk.execute("https://georgesirbu.com/Ortodox/liste/media.lst");

        while(fineHTTP==0) {
            Log.d("LISTA: ", listaMedia);
        }

        String[] parts = listaMedia.split(">");
        int size = parts.length;
        String[] data = new String[size/2];
        final String[] links = new String[size/2];
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
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.single_row, R.id.textView, data);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub

                try {
                    mPlayer.stop();
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


                mButtonPlay.setImageResource(R.drawable.playarrow);
                played = false;


                selectedLink = links[+position];
                positionLink = position;
                mButtonPlay.performClick();

            }
        });


                mButtonSx.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {


                        try {
                            mPlayer.stop();
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


                        played = false;

                        if(positionLink>0) {
                            positionLink = +positionLink-1;
                            selectedLink = links[positionLink];
                        }

                        listView.setItemChecked(positionLink,true);

                        mButtonPlay.performClick();

                    }
                });


        mButtonDx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                try {
                    mPlayer.stop();
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


                played = false;

                int listCount = listView.getAdapter().getCount();

                if( positionLink < listCount-1){
                    positionLink = +positionLink+1;
                    selectedLink = links[positionLink];
                }

                

                listView.setItemChecked(positionLink,true);

                mButtonPlay.performClick();

            }
        });


        mButtonPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(played == false ) {


                    progressDialog.show();

                    // Initialize a new media player instance
                    mPlayer = new MediaPlayer();

                    mButtonPlay.setImageResource(R.drawable.pausebutton);

                    // The audio url to play
                    String audioUrl = selectedLink;



                    // Set the media player audio stream type
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    //Try to play music/audio from url

                    try{


                        // Set the audio data source
                        mPlayer.setDataSource(audioUrl);


                        // Prepare the media player
                        mPlayer.prepare();



                        // Start playing audio from http url
                        mPlayer.start();

                        played = true;



                        // Inform user for audio streaming
                        //Toast.makeText(mContext,"Playing",Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(mContext,"End",Toast.LENGTH_SHORT).show();
                            mButtonPlay.setImageResource(R.drawable.playarrow);
                            played = false;
                        }
                    });



                    mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            if (progressDialog != null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }

                        }


                    });





                }else
                {
                    mPlayer.stop();
                    mButtonPlay.setImageResource(R.drawable.playarrow);
                    played = false;
                }

            }
        });

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

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            fineHTTP = 1;
        }
    }
}
