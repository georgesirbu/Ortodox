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

    int fineHTTP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("Muzica");
        loadingDialog.setMessage("Se incarca...");
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);

        final FloatingActionButton mButtonPlay = findViewById(R.id.btnplay);

        ListView listView = findViewById(R.id.listview);

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


                if (selectedLink != "")
                {

                    mPlayer.stop();

                }
                selectedLink = links[+position];

                mButtonPlay.performClick();

            }
        });


        mButtonPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // click handling code

               // loadingDialog.show();

                // Disable the play button
                //mButtonPlay.setEnabled(false);
               if(played == false ) {


                   mButtonPlay.setImageResource(R.drawable.pausebutton);

                // The audio url to play
                String audioUrl = selectedLink;

                // Initialize a new media player instance
                mPlayer = new MediaPlayer();

                // Set the media player audio stream type
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //Try to play music/audio from url

                try{
                    /*
                        void setDataSource (String path)
                            Sets the data source (file-path or http/rtsp URL) to use.

                        Parameters
                            path String : the path of the file, or the http/rtsp URL of the stream you want to play

                        Throws
                            IllegalStateException : if it is called in an invalid state

                                When path refers to a local file, the file may actually be opened by a
                                process other than the calling application. This implies that the
                                pathname should be an absolute path (as any other process runs with
                                unspecified current working directory), and that the pathname should
                                reference a world-readable file. As an alternative, the application
                                could first open the file for reading, and then use the file
                                descriptor form setDataSource(FileDescriptor).

                            IOException
                            IllegalArgumentException
                            SecurityException
                    */
                    // Set the audio data source
                    mPlayer.setDataSource(audioUrl);

                    /*
                        void prepare ()
                            Prepares the player for playback, synchronously. After setting the
                            datasource and the display surface, you need to either call prepare()
                            or prepareAsync(). For files, it is OK to call prepare(), which blocks
                            until MediaPlayer is ready for playback.

                        Throws
                            IllegalStateException : if it is called in an invalid state
                            IOException
                    */
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
                        played = true;
                    }
                });
                mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

                    public void onBufferingUpdate(MediaPlayer mp, int percent)
                    {
                        //loadingDialog.dismiss();
                    }

                });


                mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {

                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                           // loadingDialog.show();
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            // dismiss progress dialog
                            //loadingDialog.dismiss();
                        }


                        return false;
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


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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
