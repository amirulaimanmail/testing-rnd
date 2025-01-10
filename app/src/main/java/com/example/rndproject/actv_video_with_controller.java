package com.example.rndproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rndproject.utils.CustomMediaController;

public class actv_video_with_controller extends AppCompatActivity {
    private VideoView vv;
    private CustomMediaController mc;
    private FrameLayout fvc;
    private FrameLayout vc;

    private boolean isFullscreen = false;
    private boolean isPlaying = true;
    private int currentVideoPosition = 0;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_video);

        // Initialize components
        vv = findViewById(R.id.actv_video_vv);
        mc = new CustomMediaController(this, vv);
        vc = findViewById(R.id.actv_video_container);
        fvc = findViewById(R.id.actv_video_fullscreen_container);

        fvc.setVisibility(View.GONE);

        // Video setup
        String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
        vv.setVideoURI(Uri.parse(videoUrl));

        // Back press handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    exitFullscreen();
                } else {
                    finish();
                }
            }
        });

        vv.setOnPreparedListener(mp -> {
            vv.start();

            vv.post(() -> {
                vv.setMediaController(mc);
                mc.initializeSeekBar();
                mc.setFullscreenState(isFullscreen);

                if(firstTime){
                    firstTime = false;
                }
                else{
                    mc.show();
                }
            });
        });

        vv.setOnTouchListener((v, event) -> {
            if (isFullscreen) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vv.onTouchEvent(event);
                    return true;
                }
            }
            return false;
        });

        fvc.setOnTouchListener((v, event) -> {
            if (isFullscreen) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Show or hide the media controller based on current state
                    if (mc.isShowing()) {
                        mc.hide();
                    } else {
                        mc.show();
                    }
                    return true;  // Consume the event to prevent further propagation
                }
            }
            return false;  // If not fullscreen, let VideoView handle the touch events
        });

        vv.setOnCompletionListener(mp -> vv.start());
        mc.setFullscreenSetListener(this::setFullscreen);
        mc.setPlayToggleListener(this::setPlay);
    }

    private void setFullscreen(boolean isFullscreen) {
        if (isFullscreen) {
            enterFullscreen();
        } else {
            exitFullscreen();
        }
    }

    private void setPlay(boolean isPlaying) {
        this.isPlaying = isPlaying;
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
        currentVideoPosition = vv.getCurrentPosition();
        removeFromParent(vv);
        fvc.addView(vv);
        vv.seekTo(currentVideoPosition);
        if(isPlaying){
            vv.start();
        }

        fvc.setVisibility(View.VISIBLE);
        isFullscreen = true;
    }

    private void exitFullscreen() {
        // Restore system UI
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show(); // Show the action bar
        }

        // Move VideoView back to original container
        currentVideoPosition = vv.getCurrentPosition();
        removeFromParent(vv);
        vc.addView(vv);
        vv.seekTo(currentVideoPosition);
        if(isPlaying){
            vv.start();
        }

        fvc.setVisibility(View.GONE);
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
