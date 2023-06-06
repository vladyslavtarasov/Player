package com.example.player;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

    private Button chooseMusicButton, startButton, stopButton;
    private TextView songNameTV, startTimeTV, endTimeTV;
    private MediaPlayer mediaPlayer;
    private AppCompatSeekBar seekBar;
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
        startButton = findViewById(R.id.pause_btn);
        stopButton = findViewById(R.id.stop_btn);
        songNameTV = findViewById(R.id.song_name_tv);
        startTimeTV = findViewById(R.id.start_time_tv);
        endTimeTV = findViewById(R.id.end_time_tv);
        mediaPlayer = new MediaPlayer();

        seekBar = findViewById(R.id.progress_sb);
        seekBar.setClickable(false);

        startButton.setEnabled(false);
        stopButton.setEnabled(false);

        chooseMusicButton.setOnClickListener(view -> {
            mediaPlayer.pause();
            startButton.setText("Play");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            audioActivityResultLauncher.launch(intent);
        });

        startButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    startButton.setText("Play");
                } else {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    mediaPlayer.start();
                    startButton.setText("Pause");

                    endTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();

                    if (TIME_DURATION_FLAG == 0) {
                        seekBar.setMax((int) endTime);
                        TIME_DURATION_FLAG = 1;
                    }

                    updateEndTime();
                    updateStartTime();

                    seekBar.setProgress((int) startTime);
                    myHandler.postDelayed(UpdateSongTime, 100);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        timeSet(startTime, startTimeTV);
    }

    private void updateEndTime() {
        timeSet(endTime, endTimeTV);
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
            seekBar.setProgress((int) startTime);
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
                            songNameTV.setText(returnCursor.getString(nameIndex));
                        }
                        catch (Exception e) { e.printStackTrace();}

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), audioUri);

                        endTime = mediaPlayer.getDuration();
                        startTime = mediaPlayer.getCurrentPosition();

                        seekBar.setMax((int) endTime);

                        updateEndTime();
                        updateStartTime();
                        startButton.setEnabled(true);
                        stopButton.setEnabled(true);
                        songContainer.setVisibility(View.VISIBLE);
                    }
                } else Toast.makeText(getApplicationContext(),"Cancelled", Toast.LENGTH_SHORT).show();
            });
    private void stopPlay(){
        mediaPlayer.stop();
        startButton.setText("Play");
        try {
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void stop(View view){
        stopPlay();
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