package com.example.rndproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rndproject.utils.CustomMediaController;

public class actv_video_with_controller_2 extends AppCompatActivity {
    private VideoView vv;
    private MediaController mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_video);

        // Initialize components
        vv = findViewById(R.id.actv_video_vv);
        mc = new MediaController(this);

        // Video setup
        String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
        vv.setVideoURI(Uri.parse(videoUrl));

        vv.setOnPreparedListener(mp -> {
            vv.start();

            vv.post(() -> {
                vv.setMediaController(mc);
            });
        });

        vv.setOnCompletionListener(mp -> vv.start());
    }
}
