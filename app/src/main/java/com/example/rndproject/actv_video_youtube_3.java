package com.example.rndproject;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class actv_video_youtube_3 extends AppCompatActivity {

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
        youtubeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Page has finished loading, you can perform actions now
                youtubeWebView.postDelayed(() -> simulateTouchAtCenter(), 150);
            }
        });

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
                + "?enablejsapi=1&autoplay=0&mute=0\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>" +
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
    }

    // Method to check the fullscreen state if needed
    public boolean isFullScreen() {
        return isFullScreen;
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

    private void simulateTouchAtCenter() {
        // Get the WebView's width and height
        int width = youtubeWebView.getWidth();
        int height = youtubeWebView.getHeight();

        // Calculate the center of the WebView
        float centerX = width / 2f;
        float centerY = height / 2f;

        // Create the MotionEvent for a touch event at the center
        long downTime = System.currentTimeMillis();
        long eventTime = System.currentTimeMillis() + 100;
        MotionEvent motionEventDown = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, centerX, centerY, 0);
        MotionEvent motionEventUp = MotionEvent.obtain(downTime, eventTime + 100, MotionEvent.ACTION_UP, centerX, centerY, 0);

        // Dispatch the touch events to the WebView
        youtubeWebView.dispatchTouchEvent(motionEventDown);
        youtubeWebView.dispatchTouchEvent(motionEventUp);
    }


}
