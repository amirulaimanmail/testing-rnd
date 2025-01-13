package com.example.rndproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class adapter_video extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VideoItem> videoList;
    private final Set<VideoPlayerHolder> activeVideoPlayers = new HashSet<>();


    public adapter_video(List<VideoItem> videoList) {
        this.videoList = videoList;

        this.videoList.removeIf(video -> {
            String type = video.getVideoType();
            return !"MP4".equals(type) && !"YouTube".equals(type);
        });

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 1) {
            View view = inflater.inflate(R.layout.recycler_item_mp4_video, parent, false);
            return new mp4_viewholder(view);
        }
        else {
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
            mp4_viewholder mp4Holder = (mp4_viewholder) holder;
            mp4Holder.bind(videoItem.getVideoLink());
        } else{
            youtube_viewholder youtubeHolder = (youtube_viewholder) holder;
            youtubeHolder.bind(videoItem.getVideoLink());
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class mp4_viewholder extends RecyclerView.ViewHolder implements VideoPlayerHolder{
        private PlayerView playerView;
        private ExoPlayer exoPlayer;

        private ImageButton btnPlayPause;
        private ImageButton btnFullscreen;
        private SeekBar seekBar;

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

        @OptIn(markerClass = UnstableApi.class)
        public void bind(String videoUrl) {
            exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(exoPlayer);

            // Set the media item and prepare the player
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();

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

            //exoPlayer.play();
        }

        @Override
        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
            }
        }

        @Override
        public void pausePlayer() {
            if (exoPlayer != null) {
                exoPlayer.pause();
            }
        }

        @Override
        public void playPlayer() {
            if (exoPlayer != null) {
                exoPlayer.play();
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

    public static class youtube_viewholder extends RecyclerView.ViewHolder implements VideoPlayerHolder{
        private WebView webView;
        FrameLayout fullscreenContainer;
        String url, htmlString;

        public youtube_viewholder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.actv_video_youtube_webview);
            fullscreenContainer = ((actv_video_pager) itemView.getContext()).getFullscreenContainer();
        }

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
        }

        @Override
        public void releasePlayer() {
            if (webView != null) {
                webView.loadUrl("about:blank");
                webView.clearHistory();
                webView.stopLoading();
            }
        }

        @Override
        public void pausePlayer() {
            webView.loadUrl("about:blank");
        }

        @Override
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
        Toast.makeText(holder.itemView.getContext(), "Recycled item ", Toast.LENGTH_SHORT).show();
        //((VideoPlayerHolder) holder).releasePlayer();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.d("TAG", "Detach");
        if (holder instanceof VideoPlayerHolder) {
            ((VideoPlayerHolder) holder).pausePlayer();
            removeVideoHolder((VideoPlayerHolder) holder);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Log.d("TAG", "Attach");
        if (holder instanceof VideoPlayerHolder) {
            addVideoHolder((VideoPlayerHolder) holder);
            ((VideoPlayerHolder) holder).playPlayer();

        }
    }

    public void addVideoHolder(VideoPlayerHolder holder) {
        activeVideoPlayers.add(holder);
    }

    public void removeVideoHolder(VideoPlayerHolder holder) {
        activeVideoPlayers.remove(holder);
    }

    public void releaseVideos() {
        for (VideoPlayerHolder holder : activeVideoPlayers) {
            holder.releasePlayer();
        }
        activeVideoPlayers.clear();
    }

    public void pauseVideos() {
        for (VideoPlayerHolder holder : activeVideoPlayers) {
            holder.pausePlayer();
        }
    }

    public void resumeVideos() {
        for (VideoPlayerHolder holder : activeVideoPlayers) {
            holder.playPlayer();
        }
    }

    public interface VideoPlayerHolder {
        void releasePlayer();
        void pausePlayer();
        void playPlayer();
    }
}
