package com.ktc.media.scan.observe;


import com.ktc.media.constant.Constants;

import java.util.ArrayList;

public class FileObservation {

    private ArrayList<FileScanObserver> mFileScanObservers;

    public FileObservation() {
        mFileScanObservers = new ArrayList<>();
    }

    public void addObserver(FileScanObserver fileScanObserver) {
        mFileScanObservers.add(fileScanObserver);
    }

    public void removeObserver(FileScanObserver fileScanObserver) {
        mFileScanObservers.remove(fileScanObserver);
    }

    public void notifyVideoUpdate() {
        for (FileScanObserver fileScanObserver : mFileScanObservers) {
            if (fileScanObserver != null) {
                fileScanObserver.update(Constants.VIDEO_REFRESH_ACTION);
            }
        }
    }

    public void notifyMusicUpdate() {
        for (FileScanObserver fileScanObserver : mFileScanObservers) {
            if (fileScanObserver != null) {
                fileScanObserver.update(Constants.MUSIC_REFRESH_ACTION);
            }
        }
    }

    public void notifyPictureUpdate() {
        for (FileScanObserver fileScanObserver : mFileScanObservers) {
            if (fileScanObserver != null) {
                fileScanObserver.update(Constants.PICTURE_REFRESH_ACTION);
            }
        }
    }

    public void notifyDelete() {
        for (FileScanObserver fileScanObserver : mFileScanObservers) {
            if (fileScanObserver != null) {
                fileScanObserver.update(Constants.PATH_DELETE_ACTION);
            }
        }
    }

    public void notifyAllUpdate() {
        for (FileScanObserver fileScanObserver : mFileScanObservers) {
            if (fileScanObserver != null) {
                fileScanObserver.update(Constants.ALL_REFRESH_ACTION);
            }
        }
    }
}
