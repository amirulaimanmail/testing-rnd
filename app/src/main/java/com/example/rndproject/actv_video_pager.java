package com.example.rndproject;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rndproject.adapter.adapter_video2;
import com.example.rndproject.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class actv_video_pager extends AppCompatActivity {

    RecyclerView rv;
    adapter_video2 adapter;
    FrameLayout fl_fullscreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_video_pager);

        rv = findViewById(R.id.actv_video_pager_rv);
        fl_fullscreen = findViewById(R.id.actv_video_fullscreen_container);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);

        // Create video data
        List<VideoItem> videoList = new ArrayList<>();
        videoList.add(new VideoItem("https://www.youtube.com/watch?v=Mf_nGEPIsQ8"));
        videoList.add(new VideoItem("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"));
        videoList.add(new VideoItem("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        videoList.add(new VideoItem("https://www.youtube.com/watch?v=WHfVVF0gUf0"));
        videoList.add(new VideoItem("https://www.youtube.com/watch?v=YfhfYcQV54c"));
        videoList.add(new VideoItem("https://www.youtube.com/watch?v=QqgQkhBF9jU&t"));

        Runnable scrollToNextItemRunnable = this::scrollToNextItem;

        // Set Adapter
        adapter = new adapter_video2(this, videoList, scrollToNextItemRunnable);
        rv.setAdapter(adapter);

        // Enable snapping to one item at a time
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.resumePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.pausePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.releasePlayer();
        }
    }

    public void scrollToNextItem() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        int currentPosition = layoutManager.findFirstVisibleItemPosition();

        if (currentPosition != RecyclerView.NO_POSITION && currentPosition < adapter.getItemCount() - 1) {
            // Get the width of the first visible item
            View firstVisibleView = layoutManager.findViewByPosition(currentPosition);
            int itemWidth = firstVisibleView != null ? firstVisibleView.getWidth() : 0;

            // Smooth scroll by the width of one item, ensuring no overshooting
            rv.smoothScrollBy(itemWidth, 0);
        }
    }





    public FrameLayout getFullscreenContainer() {
        return fl_fullscreen;
    }

    // Method to hide system UI (status bar and navigation bar)
    public void hideSystemUI() {
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
    public void showSystemUI() {
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
