package com.example.rndproject.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUtils {

    public static String getYouTubeVideoId(String url) {
        String videoId = null;
        String regex = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)\\S+|.*[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            videoId = matcher.group(1);
        }

        return videoId;
    }
}

