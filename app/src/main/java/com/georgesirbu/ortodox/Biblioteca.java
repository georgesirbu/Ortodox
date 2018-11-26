package com.georgesirbu.ortodox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Biblioteca extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation2:
                    startActivity(new Intent(Biblioteca.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation1:
                    startActivity(new Intent(Biblioteca.this, Personal.class));
                    finish();
                    return true;
                case R.id.navigation3:
                    startActivity(new Intent(Biblioteca.this, Biblioteca.class));
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //navigation.getMenu().findItem(R.id.navigation1).setChecked(false);
        //navigation.getMenu().findItem(R.id.navigation2).setChecked(false);
        navigation.getMenu().findItem(R.id.navigation3).setChecked(true);
    }

}
