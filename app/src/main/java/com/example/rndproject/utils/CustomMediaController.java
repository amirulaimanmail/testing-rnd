package com.example.rndproject.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.example.rndproject.R;

public class CustomMediaController extends MediaController {

    private ImageButton fullscreenButton;
    private boolean isFullscreen = false;
    private FullscreenToggleListener fullscreenToggleListener;

    public CustomMediaController(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_media_controller, this);

        fullscreenButton = view.findViewById(R.id.fullscreen_button);
        fullscreenButton.setOnClickListener(v -> {
            isFullscreen = !isFullscreen;
            fullscreenButton.setImageResource(isFullscreen ? R.drawable.ic_exit_fullscreen : R.drawable.ic_enter_fullscreen);

            if (fullscreenToggleListener != null) {
                fullscreenToggleListener.onFullscreenToggle(isFullscreen);
            }
        });
    }

    public void setFullscreenToggleListener(FullscreenToggleListener listener) {
        this.fullscreenToggleListener = listener;
    }

    public interface FullscreenToggleListener {
        void onFullscreenToggle(boolean isFullscreen);
    }
}
