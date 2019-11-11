package com.ktc.media.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import com.ktc.media.IFileScanManager;
import com.ktc.media.IFileScanUpdateListener;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
import com.ktc.media.scan.FileScanUpdateService;
import com.ktc.media.util.StorageUtil;

import java.util.List;

public abstract class BaseActivity extends Activity {

    private boolean isInit = false;
    private long lastDownTime;
    private long lastUpTime;
    private int keyRepeatDelay;
    private IFileScanManager mIFileScanManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        registDiskReceiver();
        startBind();
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
        keyRepeatDelay = ViewConfiguration.getKeyRepeatDelay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistDiskReceiver();
        if (mIFileScanManager != null && mIFileScanManager.asBinder().isBinderAlive()) {
            try {
                mIFileScanManager.unregisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
    }

    private void startBind() {
        Intent intent = new Intent(this, FileScanUpdateService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                long currentTime = System.currentTimeMillis();
                long duration = currentTime - lastDownTime;
                if (duration < keyRepeatDelay + 5) {
                    blockFocus();
                } else {
                    releaseFocus();
                }
                lastDownTime = currentTime;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                long currentTime = System.currentTimeMillis();
                long duration = currentTime - lastUpTime;
                if (duration < keyRepeatDelay + 5) {
                    blockFocus();
                } else {
                    releaseFocus();
                }
                lastUpTime = currentTime;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            releaseFocus();
        }
        return super.dispatchKeyEvent(event);
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public abstract void initFocus();

    public abstract void addListener();

    public abstract void handleDiskIntent(Intent intent);

    public abstract void handleUpdate(String type);

    public abstract void blockFocus();

    public abstract void releaseFocus();

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

    private BroadcastReceiver diskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleDiskIntent(intent);
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

    private IFileScanUpdateListener mListener = new IFileScanUpdateListener.Stub() {
        @Override
        public void updateFile(String type) throws RemoteException {
            handleUpdate(type);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mIFileScanManager = IFileScanManager.Stub.asInterface(service);
                mIFileScanManager.registerListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIFileScanManager = null;
        }
    };
}
