package com.georgesirbu.ortodox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class jurnal extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation2:
                    startActivity(new Intent(jurnal.this, playlist_audio.class));
                    finish();
                    return true;
                case R.id.navigation1:
                   //startActivity(new Intent(jurnal.this, jurnal.class));
                    //finish();
                    //return true;
                case R.id.navigation3:
                    startActivity(new Intent(jurnal.this, playlist_preferiti.class));
                    finish();
                    return true;
                case R.id.navigation4:
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

        navigation.getMenu().findItem(R.id.navigation1).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation2).setChecked(true);
        //navigation.getMenu().findItem(R.id.navigation3).setChecked(false);
        setTitle("Jurnal");

    }

}
