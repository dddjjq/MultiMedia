package com.ktc.media.util;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;

public class FileIconUtil {

    private static int[] resId = new int[]{R.drawable.file_apk, R.drawable.file_dir, R.drawable.file_doc
            , R.drawable.file_music, R.drawable.file_pdf, R.drawable.file_picture, R.drawable.file_ppt
            , R.drawable.file_txt, R.drawable.file_video, R.drawable.file_xls, R.drawable.file_unknow};

    public static int getFileTypeIcon(int type) {
        switch (type) {
            case Constants.FILE_TYPE_APK:
                return resId[0];
            case Constants.FILE_TYPE_DIR:
                return resId[1];
            case Constants.FILE_TYPE_DOC:
                return resId[2];
            case Constants.FILE_TYPE_MUSIC:
                return resId[3];
            case Constants.FILE_TYPE_PDF:
                return resId[4];
            case Constants.FILE_TYPE_PICTURE:
                return resId[5];
            case Constants.FILE_TYPE_PPT:
                return resId[6];
            case Constants.FILE_TYPE_TXT:
                return resId[7];
            case Constants.FILE_TYPE_VIDEO:
                return resId[8];
            case Constants.FILE_TYPE_XLS:
                return resId[9];
            case Constants.FILE_TYPE_UNKNOWN:
                return resId[10];
        }
        return resId[10];
    }
}
