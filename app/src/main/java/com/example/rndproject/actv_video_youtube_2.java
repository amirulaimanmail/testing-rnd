package com.example.rndproject;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class actv_video_youtube_2 extends AppCompatActivity {

    private WebView youtubeWebView;
    private FrameLayout fullscreenLayout;
    private boolean isFullScreen = false; // Track fullscreen state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_video_youtube2);  // Your layout file

        youtubeWebView = findViewById(R.id.actv_video_youtube_webview);
        fullscreenLayout = findViewById(R.id.actv_video_youtube_fullscreen_container);

        // Enable JavaScript and DOM storage for better YouTube support
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);  // Enable DOM storage for embedded videos

        // Set WebViewClient to ensure links open inside the WebView
        youtubeWebView.setWebViewClient(new WebViewClient());

        // Set WebChromeClient to detect fullscreen toggle
        youtubeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(android.view.View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                // Handle fullscreen button press, toggle state
                isFullScreen = true;
                // Change the layout to fullscreen mode (optional)
                setFullScreenLayout(view);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                // Handle exiting fullscreen
                isFullScreen = false;
                // Reset the layout back to normal mode (optional)
                resetNormalLayout();
            }
        });

        // YouTube video ID
        String url = "DJHWsxs22jg"; // Video ID

        // Create the HTML embed string with autoplay and mute parameters
        String htmlString = "<html><body style=\"margin:0;padding:0;\">" +
                "<iframe id=\"youtubePlayer\" width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"
                + url
                + "?enablejsapi=1&autoplay=1&playsinline=1&mute=1\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>" +
                "<script>" +
                "var player;" +
                "function onYouTubePlayerAPIReady() {" +
                "    player = new YT.Player('youtubePlayer');" +
                "}" +
                "</script>" +
                "<script src=\"https://www.youtube.com/iframe_api\"></script>" +
                "</body></html>";

        // Load the HTML string into the WebView
        youtubeWebView.loadData(htmlString, "text/html", "utf-8");

        // Simulate unmute after 2 seconds
        youtubeWebView.postDelayed(this::simulateUnmute, 3000);
    }

    // Method to set the WebView layout to fullscreen
    private void setFullScreenLayout(android.view.View customView) {
        // Replace the current layout with the fullscreen view (without resetting the entire activity)
        fullscreenLayout.removeAllViews();
        fullscreenLayout.addView(customView);
        fullscreenLayout.setVisibility(View.VISIBLE);
        youtubeWebView.setVisibility(View.INVISIBLE);  // Hide WebView while fullscreen is active
    }

    // Method to reset the layout back to normal
    private void resetNormalLayout() {
        // Reset the layout without calling setContentView
        fullscreenLayout.setVisibility(View.GONE);
        youtubeWebView.setVisibility(View.VISIBLE);  // Show WebView again
    }

    private void simulateUnmute() {
        // Unmute the video
        String unmuteCode = "if (player) { player.unMute(); }";
        youtubeWebView.evaluateJavascript(unmuteCode, null);

        // Add a delay before playing the video to ensure it's ready
        String playCode = "if (player) { player.playVideo(); }";
        youtubeWebView.postDelayed(() -> {
            youtubeWebView.evaluateJavascript(playCode, null);
        }, 1000); // 1 second delay to ensure the unmute is processed
    }


}
