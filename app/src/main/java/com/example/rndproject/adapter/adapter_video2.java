package com.example.rndproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rndproject.R;
import com.example.rndproject.actv_video_pager;
import com.example.rndproject.model.VideoItem;
import com.example.rndproject.utils.YoutubeUtils;

import java.util.List;

public class adapter_video2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VideoItem> videoList;
    private static ExoPlayer exoPlayer;
    private RecyclerView.ViewHolder newViewHolder;

    private static boolean initializeFirstPlayer;

    private youtube_viewholder currentYoutubePlayer;

    public adapter_video2(Context context, List<VideoItem> videoList) {
        this.videoList = videoList;
        this.videoList.removeIf(video -> {
            String type = video.getVideoType();
            return !"MP4".equals(type) && !"YouTube".equals(type);
        });

        exoPlayer = new ExoPlayer.Builder(context).build();
        initializeFirstPlayer = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == 1){
            View view = inflater.inflate(R.layout.recycler_item_mp4_video, parent, false);
            return new mp4_viewholder(view);
        } else{
            View view = inflater.inflate(R.layout.recycler_item_youtube_video, parent, false);
            return new youtube_viewholder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        VideoItem videoItem = videoList.get(position);

        if(videoItem.getVideoType().equals("MP4")){
            return 1;
        } else{
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoItem videoItem = videoList.get(position);
        int currentViewType = getItemViewType(position);

        if(currentViewType == 1){
            mp4_viewholder mp4ViewHolder = (mp4_viewholder) holder;
            mp4ViewHolder.bind(videoItem.getVideoLink());
        } else if (currentViewType == 2){
            youtube_viewholder youtubeViewHolder = (youtube_viewholder) holder;
            youtubeViewHolder.bind(videoItem.getVideoLink());
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class mp4_viewholder extends RecyclerView.ViewHolder{
        private final PlayerView playerView;

        private String videoUrl;

        private final ImageButton btnPlayPause;
        private final ImageButton btnFullscreen;
        private final SeekBar seekBar;

        private boolean isFullscreen = false;
        private ViewGroup originalParent;
        private int originalIndex;

        private boolean controllerDisabled = false;

        public mp4_viewholder(@NonNull View itemView){
            super(itemView);
            playerView = itemView.findViewById(R.id.actv_video_playerview);
            btnPlayPause = playerView.findViewById(R.id.media_playpause_btn);
            btnFullscreen = playerView.findViewById(R.id.media_fullscreen_btn);
            seekBar = playerView.findViewById(R.id.media_seekbar);
        }

        public void releaseExoPlayerFromPlayerView(){
            playerView.setPlayer(null);
        }

        @OptIn(markerClass = UnstableApi.class)
        public void initializeExoPlayer(){
            playerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();

            // Set click listeners for custom controls
            btnPlayPause.setOnClickListener(v -> {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                } else {
                    exoPlayer.play();
                }
            });

            btnFullscreen.setOnClickListener(v -> {
                FrameLayout fullscreenContainer = ((actv_video_pager) itemView.getContext()).getFullscreenContainer();
                toggleFullscreen(fullscreenContainer);
                updateMediaScreenState();
            });

            // SeekBar listener to update media progress
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        exoPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // Listen for player
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        seekBar.setMax((int) exoPlayer.getDuration());
                    }

                    if (playbackState == Player.STATE_ENDED) {
                        if(controllerDisabled){
                            playerView.setUseController(true);
                            controllerDisabled = false;
                        }
                        exoPlayer.seekTo(0);
                        exoPlayer.play();
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        playerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (exoPlayer != null) {
                                    // Update SeekBar progress
                                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());

                                    // Check if we're within the last second of the video
                                    if (exoPlayer.getCurrentPosition() >= exoPlayer.getDuration() - 200 && !controllerDisabled && exoPlayer.isPlaying()) {
                                        playerView.setUseController(false);  // Disable the controller
                                        controllerDisabled = true;  // Set the flag to true so it doesn't happen again
                                    }

                                    // Continue updating the progress every second
                                    playerView.postDelayed(this, 100);
                                }
                            }
                        });
                    }

                    updateMediaPlayState(); // Update play/pause button whenever the playing state changes
                }
            });

            playerView.setUseController(true);
            playerView.setControllerShowTimeoutMs(1500);
        }

        public void bind(String videoUrl) {
            this.videoUrl = videoUrl;

            if(initializeFirstPlayer) {
                initializeFirstPlayer = false;
                initializeExoPlayer();
            }
        }

        private void updateMediaPlayState(){
            if(exoPlayer.isPlaying()){
                btnPlayPause.setImageResource(R.drawable.ic_pause_button);
            }
            else{
                btnPlayPause.setImageResource(R.drawable.ic_play_button);
            }
        }

        private void updateMediaScreenState(){
            if(isFullscreen){
                btnFullscreen.setImageResource(R.drawable.ic_exit_fullscreen);
            }
            else{
                btnFullscreen.setImageResource(R.drawable.ic_enter_fullscreen);
            }
        }

        public void toggleFullscreen(FrameLayout fullscreenContainer) {
            if (isFullscreen) {
                // Exit fullscreen
                if (originalParent != null) {
                    fullscreenContainer.removeAllViews();
                    originalParent.addView(playerView, originalIndex);
                }
                fullscreenContainer.setVisibility(View.GONE);
                ((actv_video_pager) itemView.getContext()).showSystemUI();
            } else {
                // Enter fullscreen
                originalParent = (ViewGroup) playerView.getParent();
                originalIndex = originalParent.indexOfChild(playerView);

                // Remove from original parent and add to fullscreen container
                originalParent.removeView(playerView);
                fullscreenContainer.addView(playerView);
                fullscreenContainer.setVisibility(View.VISIBLE);
                ((actv_video_pager) itemView.getContext()).hideSystemUI();
            }
            isFullscreen = !isFullscreen;
        }
    }

    public static class youtube_viewholder extends RecyclerView.ViewHolder implements adapter_video.VideoPlayerHolder {
        private final WebView webView;
        FrameLayout fullscreenContainer;
        String url, htmlString;

        public youtube_viewholder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.actv_video_youtube_webview);
            webView.setBackgroundColor(Color.BLACK);
            fullscreenContainer = ((actv_video_pager) itemView.getContext()).getFullscreenContainer();
        }

        @SuppressLint("SetJavaScriptEnabled")
        public void bind(String videoUrl) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onShowCustomView(android.view.View view, CustomViewCallback callback) {
                    super.onShowCustomView(view, callback);
                    setFullScreenLayout(view);
                }

                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();
                    resetNormalLayout();
                }
            });

            url = YoutubeUtils.getYouTubeVideoId(videoUrl);
            // Create the HTML embed string with autoplay and mute parameters
            htmlString = "<html><body style=\"margin:0;padding:0;\">" +
                    "<iframe id=\"youtubePlayer\" width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"
                    + url
                    + "?enablejsapi=1&autoplay=1&mute=1&controls=1\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>" +
                    "<script>" +
                    "var player;" +
                    "function onYouTubePlayerAPIReady() {" +
                    "    player = new YT.Player('youtubePlayer');" +
                    "}" +
                    "</script>" +
                    "<script src=\"https://www.youtube.com/iframe_api\"></script>" +
                    "</body></html>";

            if(initializeFirstPlayer) {
                initializeFirstPlayer = false;
                playPlayer();
            }
        }

        public void releasePlayer() {
            if (webView != null) {
                webView.loadUrl("about:blank");
                webView.clearHistory();
                webView.stopLoading();
            }
        }

        public void pausePlayer() {
            webView.loadUrl("about:blank");
        }

        public void playPlayer() {
            webView.loadData(htmlString, "text/html", "utf-8");
        }

        private void setFullScreenLayout(android.view.View customView) {
            fullscreenContainer.addView(customView);
            fullscreenContainer.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            ((actv_video_pager) itemView.getContext()).hideSystemUI();
        }

        // Method to reset the layout back to normal
        private void resetNormalLayout() {
            fullscreenContainer.removeAllViews();
            fullscreenContainer.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            ((actv_video_pager) itemView.getContext()).showSystemUI();
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d("TAG", "onViewRecycled: ");
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if(holder instanceof mp4_viewholder){
            ((mp4_viewholder) holder).playerView.setUseController(false);
            releaseExoPlayer();

        } else if(holder instanceof youtube_viewholder){
            ((youtube_viewholder) holder).pausePlayer();
        }

        if(newViewHolder != holder && newViewHolder != null){
            if(newViewHolder instanceof mp4_viewholder){
                ((mp4_viewholder) newViewHolder).releaseExoPlayerFromPlayerView();
                ((mp4_viewholder) newViewHolder).initializeExoPlayer();
                ((mp4_viewholder) newViewHolder).playerView.setUseController(true);

            } else if(newViewHolder instanceof youtube_viewholder){
                currentYoutubePlayer = (youtube_viewholder) newViewHolder;
                ((youtube_viewholder) newViewHolder).playPlayer();
            }
        }
    }


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        newViewHolder = holder;
    }

    public void releasePlayer() {
        releaseExoPlayer();
        if(currentYoutubePlayer != null) {
            currentYoutubePlayer.releasePlayer();
        }
    }

    public void pausePlayer() {
        exoPlayer.pause();
        if(currentYoutubePlayer != null){
            currentYoutubePlayer.pausePlayer();
        }
    }

    public void resumePlayer() {
        exoPlayer.play();
        if(currentYoutubePlayer != null){
            currentYoutubePlayer.playPlayer();
        }
    }

    public void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
        }
    }
}
