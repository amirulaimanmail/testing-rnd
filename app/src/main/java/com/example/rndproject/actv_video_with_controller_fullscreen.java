package com.example.rndproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rndproject.utils.CustomMediaController;

public class actv_video_with_controller_fullscreen extends AppCompatActivity {

    private VideoView vv;
    private CustomMediaController mc;
    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.actv_video);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vv = findViewById(R.id.actv_video_wv);

        String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
        vv.setVideoURI(Uri.parse(videoUrl));

        mc = new CustomMediaController(this);
        mc.setFullscreenToggleListener(isFullscreen -> toggleFullscreen(isFullscreen));
        vv.setMediaController(mc);
        mc.setAnchorView(vv);

        vv.setOnPreparedListener(mp -> mp.setOnVideoSizeChangedListener((mp1, width, height) -> {
            mc.setAnchorView(vv);
        }));

        vv.start();
        vv.setOnCompletionListener(mp -> vv.start());
    }

    private void toggleFullscreen(boolean isFullscreen) {
        if (isFullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getSupportActionBar().hide();
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getSupportActionBar().show();
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) vv.getLayoutParams();
        params.width = isFullscreen ? FrameLayout.LayoutParams.MATCH_PARENT : FrameLayout.LayoutParams.WRAP_CONTENT;
        params.height = isFullscreen ? FrameLayout.LayoutParams.MATCH_PARENT : FrameLayout.LayoutParams.WRAP_CONTENT;
        vv.setLayoutParams(params);
    }
}

