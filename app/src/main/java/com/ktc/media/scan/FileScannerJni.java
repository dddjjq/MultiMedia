package com.ktc.media.scan;

public class FileScannerJni {

    public static final String TAG = FileScannerJni.class.getSimpleName();

    static {
        try {
            System.loadLibrary("FileScanner");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static native void scanFiles(String path);

}
