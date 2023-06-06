package com.example.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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