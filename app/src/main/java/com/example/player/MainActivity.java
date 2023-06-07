package com.example.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void openVideoPlayer(View view) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        startActivity(intent);
    }

    public void openAudioPlayer(View view) {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        startActivity(intent);
    }
}