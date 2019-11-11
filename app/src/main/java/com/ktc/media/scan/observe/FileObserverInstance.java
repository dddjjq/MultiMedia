package com.ktc.media.scan.observe;

public class FileObserverInstance {

    private volatile static FileObserverInstance instance;
    private FileObservation mFileObservation;

    private FileObserverInstance() {
        mFileObservation = new FileObservation();
    }

    public static FileObserverInstance getInstance() {
        if (instance == null) {
            synchronized (FileObserverInstance.class) {
                if (instance == null) {
                    instance = new FileObserverInstance();
                }
            }
        }
        return instance;
    }

    public void addFileScanObserver(FileScanObserver fileScanObserver) {
        mFileObservation.addObserver(fileScanObserver);
    }

    public void removeFileScanObserver(FileScanObserver fileScanObserver) {
        mFileObservation.removeObserver(fileScanObserver);
    }

    public void notifyVideoAction() {
        mFileObservation.notifyVideoUpdate();
    }

    public void notifyMusicAction() {
        mFileObservation.notifyMusicUpdate();
    }

    public void notifyPictureAction() {
        mFileObservation.notifyPictureUpdate();
    }

    public void notifyDeleteAction() {
        mFileObservation.notifyDelete();
    }

    public void notifyAllAction() {
        mFileObservation.notifyAllUpdate();
    }
}

