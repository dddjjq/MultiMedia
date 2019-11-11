package com.ktc.media.util;

import android.media.MediaMetadataRetriever;

public class Tools {

    public static String getDurationString(String path) {
        return formatDuration(getMediaDuration(path));
    }

    public static int getMediaDuration(String videoPath) {
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            String duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration == null) return 0;
            return Integer.parseInt(duration);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatDuration(int duration) {
        int time = duration / 1000;
        if (time == 0 && duration > 0) {
            time = 1;
        }

        int second = time % 60;
        int min = time / 60;
        long hour = 0;
        if (min > 60) {
            hour = time / 3600;
            min = time / 60 % 60;
        }
        if (hour > 0)
            return String.format("%02d:%02d:%02d", hour, min, second);
        else
            return String.format("%02d:%02d", min, second);
    }
}
