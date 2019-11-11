package com.ktc.media.scan;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ktc.media.IFileScanManager;
import com.ktc.media.IFileScanUpdateListener;
import com.ktc.media.scan.observe.FileObserverInstance;
import com.ktc.media.scan.observe.FileScanObserver;

public class FileScanUpdateService extends Service implements FileScanObserver {

    private RemoteCallbackList<IFileScanUpdateListener> mListenerList = new RemoteCallbackList<>();

    public FileScanUpdateService() {
        FileObserverInstance.getInstance().addFileScanObserver(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileObserverInstance.getInstance().removeFileScanObserver(this);
    }

    private Binder mBinder = new IFileScanManager.Stub() {
        @Override
        public void registerListener(IFileScanUpdateListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IFileScanUpdateListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }
    };

    @Override
    public void update(String type) {
        synchronized (FileScanUpdateService.class) {
            try {
                int size = mListenerList.beginBroadcast();
                for (int i = 0; i < size; i++) {
                    IFileScanUpdateListener fileScanUpdateListener = mListenerList.getBroadcastItem(i);
                    if (fileScanUpdateListener != null) {
                        try {
                            fileScanUpdateListener.updateFile(type);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                mListenerList.finishBroadcast();
            }
        }
    }
}
