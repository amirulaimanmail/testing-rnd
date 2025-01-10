package com.example.rndproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class actv_main extends AppCompatActivity {

    private LinearLayout buttonContainer;
    private LinearLayout buttonContainer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Get reference to the button container
        buttonContainer = findViewById(R.id.actv_main_button_container);
        buttonContainer2 = findViewById(R.id.actv_main_button_container2);

        addButton("Video no controller", buttonContainer, v -> {
            Intent intent = new Intent(this, actv_video_no_controller.class);
            startActivity(intent);
        });

        addButton("VideoView with custom controls", buttonContainer, v -> {
            Intent intent = new Intent(this, actv_video_with_controller.class);
            startActivity(intent);
        });

        addButton("VideoView with default controls", buttonContainer, v -> {
            Intent intent = new Intent(this, actv_video_with_controller_2.class);
            startActivity(intent);
        });

        addButton("ExoPlayer with custom controls", buttonContainer, v -> {
            Intent intent = new Intent(this, actv_video_exo.class);
            startActivity(intent);
        });

        addButton("Video Pager", buttonContainer, v -> {
            Intent intent = new Intent(this, actv_video_pager.class);
            startActivity(intent);
        });

        addButton("Youtube Default", buttonContainer2, v -> {
            Intent intent = new Intent(this, actv_video_youtube_2.class);
            startActivity(intent);
        });

        addButton("Youtube Simulate Play", buttonContainer2, v -> {
            Intent intent = new Intent(this, actv_video_youtube_3.class);
            startActivity(intent);
        });

        addButton("Youtube Library Custom", buttonContainer2, v -> {
            Intent intent = new Intent(this, actv_video_youtube_4.class);
            startActivity(intent);
        });
    }

    private void addButton(String text, LinearLayout ll, View.OnClickListener onClickListener) {
        Button button = new Button(this);
        button.setText(text);
        button.setOnClickListener(onClickListener);
        // Set layout parameters for the button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 16); // Add margins
        button.setLayoutParams(params);
        // Add the button to the container
        ll.addView(button);
    }
}
