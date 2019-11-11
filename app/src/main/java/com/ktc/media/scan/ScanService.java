package com.ktc.media.scan;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.util.ExecutorUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件扫描服务
 */
public class ScanService extends IntentService {

    private static final String TAG = ScanService.class.getSimpleName();
    private boolean needClearAllData = false;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public ScanService() {
        super("ScanService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        try {
            final String diskPath = intent.getStringExtra("diskPath");
            if (diskPath == null) return;
            needClearAllData = intent.getBooleanExtra("needClearAll", false);
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    clearBeforeData(diskPath);
                    FileScannerJni.scanFiles(diskPath);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearBeforeData(String mPath) {
        if (!needClearAllData) {
            clearData(mPath);
        } else {
            clearAllData();
        }
    }

    //插入之前先清除
    private void clearData(String path) {
        DatabaseUtil.getInstance(this).deletePathData(path, false);
    }

    private void clearAllData() {
        DatabaseUtil.getInstance(this).deleteAllData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExecutorUtil.shutdownAndAwaitTermination(mExecutorService);
    }
}
