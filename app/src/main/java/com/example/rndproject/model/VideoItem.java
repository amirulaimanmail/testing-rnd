package com.example.rndproject.model;

public class VideoItem {

    private String video_link;
    private String video_type;

    public VideoItem(String video_link){
        this.video_link = video_link;
        setVideoType();
    }

    public String getVideoLink() {
        return video_link;
    }

    public String getVideoType() {
        return video_type;
    }

    // Method to determine and set video type
    private void setVideoType() {
        if (video_link == null || video_link.isEmpty()) {
            video_type = "unknown";
        } else if (video_link.contains("youtube.com") || video_link.contains("youtu.be")) {
            video_type = "YouTube";
        } else if (video_link.endsWith(".mp4")) {
            video_type = "MP4";
        } else {
            video_type = "unknown";
        }
    }
}
