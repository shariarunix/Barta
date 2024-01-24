package com.shariarunix.barta;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progress_bar);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                progress();
            }
        });
        thread.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                finish();
            }
        }, 1500);
    }

    private void progress() {
        for (int i = 0; i <= 100; i++) {
            try {
                Thread.sleep(15);
                progressBar.setProgress(i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}