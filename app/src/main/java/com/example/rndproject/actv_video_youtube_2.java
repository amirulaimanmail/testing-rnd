package com.example.rndproject;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class actv_video_youtube_2 extends AppCompatActivity {

    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private FrameLayout fullscreenViewContainer;

    private boolean isFullscreen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_video_youtube2);

        // Handling back press callback to exit fullscreen
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    // If the player is in fullscreen, exit fullscreen
                    youTubePlayer.toggleFullscreen();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        fullscreenViewContainer = findViewById(R.id.actv_video_youtube_fullscreen_container);

        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1) // Enable fullscreen button
                .build();

        youTubePlayerView.setEnableAutomaticInitialization(false);

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> function0) {
                isFullscreen = true;

                // Video will continue playing in fullscreenView
                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);

                hideSystemUI();
            }

            @Override
            public void onExitFullscreen() {
                isFullscreen = false;

                // Video will continue playing in the player
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();

                showSystemUI();
            }
        });

        // Initialize YouTubePlayer and load video
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                actv_video_youtube_2.this.youTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo("DJHWsxs22jg", 0f);
            }
        }, iFramePlayerOptions);
        getLifecycle().addObserver(youTubePlayerView);
    }

    // Method to hide system UI (status bar and navigation bar)
    private void hideSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Hide the status bar and navigation bar for immersive fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE); // Hides status bar icons as well
    }

    // Method to show system UI (status bar and navigation bar)
    private void showSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show(); // Show the action bar
        }

        // Restore the system UI (status bar, navigation bar) and show content behind it
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_VISIBLE); // Make sure UI elements are visible again
    }

}
