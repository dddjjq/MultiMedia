package com.ktc.media.util;

import java.io.File;

public class FileSizeUtil {

    public static String getFileSizeDescription(String path) {
        return StorageUtil.getFileSizeDescription(getFileSize(path));
    }

    public static String getFolderSizeDescription(String path) {
        return StorageUtil.getFileSizeDescription(getFolderSize(path));
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            return file.length();
        }
        return 0;
    }

    public static long getFolderSize(String path) {
        long size = 0;
        try {
            File file = new File(path);
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    size += getFolderSize(f.getAbsolutePath());
                } else {
                    size += f.length();
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return size;
    }
}
