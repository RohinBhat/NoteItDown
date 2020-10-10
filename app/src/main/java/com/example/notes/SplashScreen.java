package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    TextView text, rohin;
    ImageView logoBackground, logoNotebook, logoPencil;
    Animation animBackground, animNotebook, animPencil, animText, animRohin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        text = findViewById(R.id.txt_app_name);
        rohin = findViewById(R.id.txt_rohin);

        text.setVisibility(View.INVISIBLE);
        rohin.setVisibility(View.INVISIBLE);

        logoBackground = findViewById(R.id.img_logo_background);
        logoNotebook = findViewById(R.id.img_logo_notebook);
        logoPencil = findViewById(R.id.img_logo_pencil);

        animBackground = AnimationUtils.loadAnimation(this, R.anim.anim_background);
        animNotebook = AnimationUtils.loadAnimation(this, R.anim.anim_notebook);
        animPencil = AnimationUtils.loadAnimation(this, R.anim.anim_pencil);
        animText = AnimationUtils.loadAnimation(this, R.anim.anim_text);
        animRohin = AnimationUtils.loadAnimation(this, R.anim.anim_rohin);

        logoBackground.setAnimation(animBackground);
        logoNotebook.setAnimation(animNotebook);
        logoPencil.setAnimation(animPencil);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                text.setAnimation(animText);
                rohin.setAnimation(animRohin);
                text.setVisibility(View.VISIBLE);
                rohin.setVisibility(View.VISIBLE);
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, WelcomeActivity.class));
                finish();
            }
        }, 2500);
    }
}
