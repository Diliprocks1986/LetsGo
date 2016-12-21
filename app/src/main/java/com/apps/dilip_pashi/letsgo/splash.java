package com.apps.dilip_pashi.letsgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle welcome) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // TODO Auto-generated method stub
        super.onCreate(welcome);
        setContentView(R.layout.splash);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent openStartingPoint = new Intent(splash.this, TravelActivity.class);
                    openStartingPoint.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(openStartingPoint);
                }
            }
        };
        timer.start();

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
 