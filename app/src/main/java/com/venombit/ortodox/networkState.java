package com.venombit.ortodox;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class networkState extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_state);

        final FloatingActionButton mButtonReload = findViewById(R.id.floatingActionButton);

        mButtonReload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(networkState.this, playlist_audio.class));
                    finish();

            }
        });



    }
}
