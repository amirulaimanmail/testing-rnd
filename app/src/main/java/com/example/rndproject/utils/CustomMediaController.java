package com.example.rndproject.utils;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.example.rndproject.R;

public class CustomMediaController extends MediaController {

    private ImageButton playPauseButton;
    private ImageButton fullscreenButton;
    public SeekBar seekBar;
    private boolean isFullscreen = false;
    private FullscreenToggleListener fullscreenSetListener;
    private PlayToggleListener playToggleListener;

    private final VideoView videoView;
    private final Handler handler = new Handler();
    private static final int AUTO_HIDE_DELAY = 3000; // Delay before auto-hide (in milliseconds)

    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide(); // Hide the controller after the delay
        }
    };

    public CustomMediaController(Context context, VideoView videoView) {
        super(context);
        this.videoView = videoView;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.media_controller, null);

        // Initialize buttons and seek bar
        playPauseButton = customView.findViewById(R.id.media_playpause_btn);
        fullscreenButton = customView.findViewById(R.id.media_fullscreen_btn);
        seekBar = customView.findViewById(R.id.media_seekbar);

        // Add custom layout to MediaController
        addView(customView);

        // Play/Pause Button Logic
        playPauseButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                if (playToggleListener != null) {
                    playToggleListener.onPlayToggle(false);
                }
            } else {
                videoView.start();
                if (playToggleListener != null) {
                    playToggleListener.onPlayToggle(true);
                }
            }
            updatePlayPauseIcon(); // Update button icon immediately
        });

        // Fullscreen Button Logic
        fullscreenButton.setOnClickListener(v -> {
            if (fullscreenSetListener != null) {
                isFullscreen = !isFullscreen;
                fullscreenSetListener.onFullscreenSet(isFullscreen);
                updateFullscreenIcon();
            }
        });

        // SeekBar Logic
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Periodically update the SeekBar and Play/Pause Icon
        handler.post(updateUI);
    }

    @Override
    public void hide() {
        super.hide();
        handler.removeCallbacks(updateUI); // Stop updates when the controller is hidden
    }

    @Override
    public void show() {
        super.show();
        handler.post(updateUI); // Resume updates when the controller is shown
        restartAutoHideTimer(); // Reset the hide timer whenever the controller is shown
    }

    @Override
    public void show(int timeout) {
        super.show(0);
        handler.post(updateUI); // Resume updates when the controller is shown
        restartAutoHideTimer(); // Reset the hide timer whenever the controller is shown
    }

    // Auto-hide functionality
    private void restartAutoHideTimer() {
        // Remove any previous hide callbacks
        handler.removeCallbacks(hideRunnable);

        // Schedule a new hideRunnable after the specified delay
        handler.postDelayed(hideRunnable, AUTO_HIDE_DELAY);
    }

    private final Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            if (videoView != null && seekBar != null) {
                // Update SeekBar Position
                seekBar.setProgress(videoView.getCurrentPosition());

                // Update Play/Pause Button Icon
                updatePlayPauseIcon();

                // Schedule the next update
                handler.postDelayed(this, 500); // Update every 0.5 seconds
            }
        }
    };

        private void updatePlayPauseIcon() {
            if (videoView.isPlaying()) {
                playPauseButton.setImageResource(R.drawable.ic_pause_button);
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play_button);
            }
        }

        private void updateFullscreenIcon() {
            if (isFullscreen) {
                fullscreenButton.setImageResource(R.drawable.ic_exit_fullscreen);
            } else {
                fullscreenButton.setImageResource(R.drawable.ic_enter_fullscreen);
            }
        }

    public void setPlayToggleListener(PlayToggleListener listener) {
        this.playToggleListener = listener;
    }

    public void setFullscreenSetListener(FullscreenToggleListener listener) {
        this.fullscreenSetListener = listener;
    }

    public void setFullscreenState(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        updateFullscreenIcon();  // Update the fullscreen icon immediately
    }

    public void initializeSeekBar(){
        seekBar.setMax(videoView.getDuration());
    }

    public interface FullscreenToggleListener {
        void onFullscreenSet(boolean isFullscreen);
    }

    public interface PlayToggleListener {
        void onPlayToggle(boolean isPlaying);
    }
}


