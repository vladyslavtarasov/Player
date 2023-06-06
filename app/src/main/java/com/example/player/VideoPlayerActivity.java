package com.example.player;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView videoView;
    Button chooseButton;
    String videoUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);


        // sets the anchor view
        // anchor view for the video
        //mediaController.setAnchorView(findViewById(R.id.controllerAnchor));

        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        chooseButton = findViewById(R.id.choose_video);
        chooseButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            videoActivityResultLauncher.launch(Intent.createChooser(intent, "Select Video"));
        });
    }

    public void back(View view) {
        finish();
    }

    public void showParkVideo(View view) {
        showVideo(view, R.raw.park);
    }

    public void showPlanetVideo(View view) {
        showVideo(view, R.raw.planet);
    }

    public void showBowlingVideo(View view) {
        showVideo(view, R.raw.bowling);
    }

    public void showVideo(View view, int RVideo) {
        FrameLayout frameLayout = findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse( "android.resource://" + getPackageName() + "/" + RVideo);
        //Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);

        videoView.start();
    }

    private final ActivityResultLauncher<Intent> videoActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri videoUri = data.getData();
                                videoView.setVideoURI(videoUri);
                                FrameLayout frameLayout = findViewById(R.id.frame_layout);
                                frameLayout.setVisibility(View.VISIBLE);
                                videoView.start();
                            }
                        } else Toast.makeText(VideoPlayerActivity.this,
                                        "Cancelled", Toast.LENGTH_SHORT).show();
            });

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}