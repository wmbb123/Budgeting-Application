package com.e.coursework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH = 7000;
    Animation animation , animation2;
    private ImageView imageView;
    private TextView appName;
    MediaPlayer media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_splash_screen);

        animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.anim2);
        imageView = findViewById(R.id.imageView);
        appName = findViewById(R.id.appName);
        media = MediaPlayer.create(SplashScreenActivity.this, R.raw.splashersound);
        media.start();

        imageView.setAnimation(animation);
        appName.setAnimation(animation2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                media.release();
                finish();
            }
        }, SPLASH);
    }
}