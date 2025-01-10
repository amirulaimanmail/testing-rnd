package com.example.rndproject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class actv_video_exo extends AppCompatActivity {

    private ExoPlayer exoPlayer;
    private PlayerView playerView;

    private FrameLayout videoContainer;
    private FrameLayout fullscreenVideoContainer;

    // Custom controls
    private ImageButton btnPlayPause;
    private ImageButton btnFullscreen;
    private SeekBar seekBar;

    private boolean isFullscreen = false;
    private boolean controllerDisabled = false;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_video_exo);

        // Initialize PlayerView
        playerView = findViewById(R.id.actv_video_playerview);
        playerView.setUseController(false); // This prevents the controller from showing on startup

        // Initialize ExoPlayer
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        // Set media item
        MediaItem mediaItem = MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        exoPlayer.setMediaItem(mediaItem);

        // Prepare the player
        exoPlayer.prepare();

        // Find custom controls
        btnPlayPause = playerView.findViewById(R.id.media_playpause_btn);
        btnFullscreen = playerView.findViewById(R.id.media_fullscreen_btn);
        seekBar = playerView.findViewById(R.id.media_seekbar);

        // Find views
        videoContainer = findViewById(R.id.actv_video_container);
        fullscreenVideoContainer = findViewById(R.id.actv_video_fullscreen_container);

        // Set click listeners for custom controls
        btnPlayPause.setOnClickListener(v -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
            } else {
                exoPlayer.play();
            }
        });

        btnFullscreen.setOnClickListener(v -> {
            toggleFullscreen();
            updateMediaScreenState();
        });

        // SeekBar listener to update media progress
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Listen for player
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    seekBar.setMax((int) exoPlayer.getDuration());
                }

                if (playbackState == Player.STATE_ENDED) {
                    if(controllerDisabled){
                        playerView.setUseController(true);
                        controllerDisabled = false;
                    }
                    exoPlayer.seekTo(0);
                    exoPlayer.play();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    playerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (exoPlayer != null) {
                                // Update SeekBar progress
                                seekBar.setProgress((int) exoPlayer.getCurrentPosition());

                                // Check if we're within the last second of the video
                                if (exoPlayer.getCurrentPosition() >= exoPlayer.getDuration() - 200 && !controllerDisabled && exoPlayer.isPlaying()) {
                                    playerView.setUseController(false);  // Disable the controller
                                    controllerDisabled = true;  // Set the flag to true so it doesn't happen again
                                }

                                // Continue updating the progress every second
                                playerView.postDelayed(this, 100);
                            }
                        }
                    });
                }

                updateMediaPlayState(); // Update play/pause button whenever the playing state changes
            }
        });

        // Back press handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    toggleFullscreen();
                } else {
                    finish();
                }
            }
        });

        playerView.setUseController(true);
        playerView.setControllerShowTimeoutMs(1500);

        exoPlayer.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void updateMediaPlayState(){
        if(exoPlayer.isPlaying()){
            btnPlayPause.setImageResource(R.drawable.ic_pause_button);
        }
        else{
            btnPlayPause.setImageResource(R.drawable.ic_play_button);
        }
    }

    private void updateMediaScreenState(){
        if(isFullscreen){
            btnFullscreen.setImageResource(R.drawable.ic_exit_fullscreen);
        }
        else{
            btnFullscreen.setImageResource(R.drawable.ic_enter_fullscreen);
        }
    }

    private void enterFullscreen() {
        // Hide system UI for immersive experience
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Show the action bar
        }

        // Move VideoView to fullscreen container
        removeFromParent(playerView);
        fullscreenVideoContainer.addView(playerView);

        // Adjust layout
        playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.requestLayout();

        fullscreenVideoContainer.setVisibility(View.VISIBLE);
        isFullscreen = true;
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void exitFullscreen() {
        // Restore system UI
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show(); // Show the action bar
        }

        // Move VideoView back to original container
        removeFromParent(playerView);
        videoContainer.addView(playerView);

        // Reset layout
        playerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        playerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.requestLayout();

        fullscreenVideoContainer.setVisibility(View.GONE);
        isFullscreen = false;
    }

    private void removeFromParent(View view) {
        // Remove the view from its current parent, if any
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }
}
