package com.example.finalhackathon;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashActivity extends AwesomeSplash {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    */

    @Override
    public void initSplash(ConfigSplash configSplash) {

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Background animation

        configSplash.setBackgroundColor(R.color.myColor);
        configSplash.setAnimCircularRevealDuration(3000);
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);


        //Logo
        configSplash.setLogoSplash(R.drawable.tripadder);
        configSplash.setAnimLogoSplashDuration(5000);
        configSplash.setAnimLogoSplashTechnique(Techniques.RollIn);



        //Title
        configSplash.setTitleSplash(getString(R.string.y));
        configSplash.setTitleTextColor(R.color.myColor2);
        configSplash.setTitleTextSize(30f);
        configSplash.setAnimTitleDuration(5000);
        configSplash.setAnimTitleTechnique(Techniques.BounceInDown);


    }

    @Override
    public void animationsFinished() {
        Intent intent=new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
