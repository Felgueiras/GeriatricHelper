package com.felgueiras.apps.geriatrichelper.Introduction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.felgueiras.apps.geriatrichelper.Main.PublicAreaActivity;
import com.felgueiras.apps.geriatrichelper.R;
import com.github.paolorotolo.appintro.AppIntro;

public class GeriatricHelperIntro extends AppIntro {
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        //adding the three slides for introduction app you can ad as many you needed
        // welcome to the App
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro_fist_screen));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro_create_cga_session));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro_fill_session));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro_review));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro_modules));

        // Show and Hide Skip and Done buttons
        showStatusBar(false);
        showSkipButton(false);

        //Add animation to the intro slider
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed() {
        // Do buildTable here when users click or tap on Skip button.
        Toast.makeText(getApplicationContext(),
                getString(R.string.app_intro_skip), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), PublicAreaActivity.class);
        startActivity(i);
    }

    @Override
    public void onNextPressed() {
        // Do buildTable here when users click or tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do buildTable here when users click or tap tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do buildTable here when slide is changed
    }
}