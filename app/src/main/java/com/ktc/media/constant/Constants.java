package com.ktc.media.constant;

public class Constants {

    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_PICTURE = "picture";
    public static final String TYPE_MUSIC = "music";

    public static final int FILE_LIMIT = 999;

    public static final int FILE_TYPE_FILE = 0x1;
    public static final int FILE_TYPE_PICTURE = 0x2;
    public static final int FILE_TYPE_MUSIC = 0x3;
    public static final int FILE_TYPE_VIDEO = 0x4;
    public static final int FILE_TYPE_DIR = 0x5;
    public static final int FILE_TYPE_DOC = 0x6;
    public static final int FILE_TYPE_PDF = 0x7;
    public static final int FILE_TYPE_PPT = 0x8;
    public static final int FILE_TYPE_TXT = 0x9;
    public static final int FILE_TYPE_XLS = 0xA;
    public static final int FILE_TYPE_APK = 0xB;
    public static final int FILE_TYPE_UNKNOW = 0xC;

    public static final String VIDEO_REFRESH_ACTION = "com.ktc.media.video.refresh";
    public static final String MUSIC_REFRESH_ACTION = "com.ktc.media.music.refresh";
    public static final String PICTURE_REFRESH_ACTION = "com.ktc.media.picture.refresh";
    public static final String ALL_REFRESH_ACTION = "com.ktc.media.file.refresh";
    public static final String PATH_DELETE_ACTION = "com.ktc.media.file.delete";

    public static final int REFRESH_COUNT = 10;

    public static final String BUNDLE_PATH_KEY = "com.ktc.path";
    public static final String FROM_FOLDER = "com.ktc.from_folder";

    //for Image player
    public static boolean bPhotoSeamlessEnable = false;
    public static boolean bSupportPhotoScale = true;
    public static boolean isExit = true;
    public static final String MPO = "mpo";
    public static final String GIF = "gif";
    public static boolean bReleasingPlayer = false;

    //for video player
    public static final int SEEK_TIME = 19;
    public static final int HANDLE_MESSAGE_PLAYER_EXIT = 20;
    public static final int HANDLE_MESSAGE_SKIP_BREAKPOINT = 21;
    public static final int ShowController = 22;
    public static final int CHECK_IS_SUPPORTED = 23;
    public static final int DOLBY_SHOW_VISION_LOGO = 24;
    public static final int DOLBY_HIDE_VISION_LOGO = 25;
    public static final int HIDE_PLAYER_CONTROL = 26;

    public static final int RefreshCurrentPositionStatusUI = 300;

}
