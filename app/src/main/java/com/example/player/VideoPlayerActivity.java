package com.example.player;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView videoView;
    Button chooseButton;
    EditText videoLink;
    FrameLayout frameLayout;

    int stopPosition;
    String internetVideoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_5MB.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        videoView = findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);

        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        chooseButton = findViewById(R.id.choose_video);
        chooseButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            videoActivityResultLauncher.launch(Intent.createChooser(intent, "Select Video"));
        });

        videoLink = findViewById(R.id.video_link);
        frameLayout = findViewById(R.id.frame_layout);
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
        frameLayout.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse( "android.resource://" + getPackageName() + "/" + RVideo);
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
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            stopPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.seekTo(stopPosition);
            videoView.start();
        }
    }

    public void showLinkVideo(View view) {
        String videoUrl = videoLink.getText().toString();

        if (videoUrl.equals("")) {
            videoUrl = internetVideoUrl;
        }

        if (!videoUrl.endsWith(".mp4")) {
            Toast.makeText(VideoPlayerActivity.this,
                    "Not a video!", Toast.LENGTH_SHORT).show();
            return;
        }

        frameLayout.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);

        videoView.start();
    }
}