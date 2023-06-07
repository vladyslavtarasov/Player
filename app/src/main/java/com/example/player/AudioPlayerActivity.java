package com.example.player;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class AudioPlayerActivity extends AppCompatActivity {

    private Button chooseMusicButton, startButton;
    private TextView songNameTextView, startTimeTextView, endTimeTextView;
    private MediaPlayer mediaPlayer;
    private SeekBar progressControl, volumeControl;
    AudioManager audioManager;
    private ConstraintLayout songContainer;
    private double startTime, endTime;
    public static int TIME_DURATION_FLAG = 0;
    private final Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        songContainer = findViewById(R.id.song_container);
        chooseMusicButton = findViewById(R.id.choose_audio);
        startButton = findViewById(R.id.play);
        songNameTextView = findViewById(R.id.song_name);
        startTimeTextView = findViewById(R.id.start_time);
        endTimeTextView = findViewById(R.id.end_time);
        mediaPlayer = new MediaPlayer();

        progressControl = findViewById(R.id.progress_sb);
        progressControl.setClickable(false);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl = findViewById(R.id.volume_control);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curValue);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        chooseMusicButton.setOnClickListener(view -> {
            mediaPlayer.pause();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            audioActivityResultLauncher.launch(intent);
        });

        startButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    startButton.setText(R.string.play);
                } else {
                    progressControl.setProgress(mediaPlayer.getCurrentPosition());

                    mediaPlayer.start();
                    startButton.setText(R.string.pause);

                    endTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();

                    if (TIME_DURATION_FLAG == 0) {
                        progressControl.setMax((int) endTime);
                        TIME_DURATION_FLAG = 1;
                    }

                    updateEndTime();
                    updateStartTime();

                    progressControl.setProgress((int) startTime);
                    myHandler.postDelayed(UpdateSongTime, 100);
                }
            }
        });

        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateStartTime() {
        timeSet(startTime, startTimeTextView);
    }

    private void updateEndTime() {
        timeSet(endTime, endTimeTextView);
    }

    private void timeSet(double timeBound, TextView textViewSet) {
        long min = TimeUnit.MILLISECONDS.toMinutes((long) timeBound);
        long sc = TimeUnit.MILLISECONDS.toSeconds((long) timeBound) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                        timeBound));
        textViewSet.setText(String.format("%s:%s",
                min < 10 ? "0" + min : min,
                sc < 10 ? "0" + sc : sc)
        );
    }

    private final Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            updateStartTime();
            progressControl.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    private final ActivityResultLauncher<Intent> audioActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri audioUri = data.getData();
                        try (Cursor returnCursor = getContentResolver().query(audioUri, null, null, null, null)) {
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (!returnCursor.moveToFirst()) throw new Exception();
                            songNameTextView.setText(returnCursor.getString(nameIndex));
                        }
                        catch (Exception e) { e.printStackTrace();}

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), audioUri);

                        endTime = mediaPlayer.getDuration();
                        startTime = mediaPlayer.getCurrentPosition();

                        progressControl.setMax((int) endTime);

                        updateEndTime();
                        updateStartTime();
                        songContainer.setVisibility(View.VISIBLE);
                    }
                } else Toast.makeText(getApplicationContext(),"Cancelled", Toast.LENGTH_SHORT).show();
            });
    private void stopPlay(){
        mediaPlayer.stop();
        startButton.setText(R.string.play);
        try {
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            stopPlay();
        }
    }

    public void back(View view) {
        finish();
    }
}