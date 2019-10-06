package com.ktc.media.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ktc.media.constant.Constants;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
import com.ktc.media.util.StorageUtil;

import java.util.List;

public abstract class BaseActivity extends Activity {

    private boolean isInit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        registDiskReceiver();
        registDataReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInit) {
            isInit = true;
            initView();
            initData();
            initFocus();
            addListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistDiskReceiver();
        unregistDataReceiver();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public abstract void initFocus();

    public abstract void addListener();

    public abstract void handleDiskIntent(Intent intent);

    public abstract void handleDataIntent(Intent intent);

    private void registDiskReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(diskReceiver, intentFilter);
    }

    private void unregistDiskReceiver() {
        unregisterReceiver(diskReceiver);
    }

    private void registDataReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.VIDEO_REFRESH_ACTION);
        intentFilter.addAction(Constants.MUSIC_REFRESH_ACTION);
        intentFilter.addAction(Constants.PICTURE_REFRESH_ACTION);
        intentFilter.addAction(Constants.ALL_REFRESH_ACTION);
        intentFilter.addAction(Constants.PATH_DELETE_ACTION);
        registerReceiver(dataReceiver, intentFilter);
    }

    private void unregistDataReceiver() {
        unregisterReceiver(dataReceiver);
    }

    private BroadcastReceiver diskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleDiskIntent(intent);
        }
    };

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleDataIntent(intent);
        }
    };

    /**
     * 获取显示路径
     */
    public String getPathDescription(BaseData data) {
        String path = data.getPath();
        List<DiskData> disks = StorageUtil.getMountedDisksList(this);
        for (DiskData diskData : disks) {
            if (path.startsWith(diskData.getPath())) {
                return path.replace(diskData.getPath(), diskData.getName());
            }
        }
        return null;
    }

    /**
     * 获取显示路径
     */
    public String getPathDescription(String path) {
        List<DiskData> disks = StorageUtil.getMountedDisksList(this);
        for (DiskData diskData : disks) {
            if (path.startsWith(diskData.getPath())) {
                return path.replace(diskData.getPath(), diskData.getName());
            }
        }
        return null;
    }

}
