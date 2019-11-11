package com.ktc.media;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.activity.FileListActivity;
import com.ktc.media.activity.MusicListActivity;
import com.ktc.media.activity.PictureListActivity;
import com.ktc.media.activity.VideoListActivity;
import com.ktc.media.base.BaseActivity;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.data.ThreadPoolManager;
import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
import com.ktc.media.model.FileData;
import com.ktc.media.scan.FileScannerJni;
import com.ktc.media.util.StorageUtil;
import com.ktc.media.view.DiskCardView;
import com.ktc.media.view.DiskContainer;
import com.ktc.media.view.MusicCardView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.PictureCardView;
import com.ktc.media.view.VideoCardView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements OnItemClickListener, DiskContainer.OnDiskItemClickListener {

    private VideoCardView mVideoCardView;
    private MusicCardView mMusicCardView;
    private PictureCardView mPictureCardView;
    private DiskContainer mDiskContainer;
    private View currentFocusView = null;
    private HashMap<String, ArrayList<? extends BaseData>> fileDataMap;
    private UpdateHandler mUpdateHandler;
    private static final int UPDATE_VIDEO = 0x01;
    private static final int UPDATE_MUSIC = 0x02;
    private static final int UPDATE_PICTURE = 0x03;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mVideoCardView = (VideoCardView) findViewById(R.id.main_video_card_view);
        mMusicCardView = (MusicCardView) findViewById(R.id.main_music_card_view);
        mPictureCardView = (PictureCardView) findViewById(R.id.main_picture_card_view);
        mDiskContainer = (DiskContainer) findViewById(R.id.main_disk_container);
    }

    @Override
    public void initData() {
        fileDataMap = new HashMap<>();
        mUpdateHandler = new UpdateHandler(this);
        mVideoCardView.setCountText(DatabaseUtil.getInstance(this).getVideoCount());
        mMusicCardView.setCountText(DatabaseUtil.getInstance(this).getMusicCount());
        mPictureCardView.setCountText(DatabaseUtil.getInstance(this).getPictureCount());
    }

    @Override
    public void initFocus() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDisk();
        prepareMediaData();
        refocus();
    }

    //返回之后重新获取焦点
    private void refocus() {
        if (currentFocusView != null) {
            if (currentFocusView instanceof DiskCardView) {
                DiskCardView diskCardView = getCurrentFocusDiskCardView((DiskCardView) currentFocusView);
                if (diskCardView != null) {
                    diskCardView.requestFocus();
                }
            } else {
                currentFocusView.requestFocus();
            }
        }
    }

    private DiskCardView getCurrentFocusDiskCardView(DiskCardView diskCardView) {
        ViewGroup viewGroup = (ViewGroup) mDiskContainer.getChildAt(0);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof DiskCardView) {
                if (((DiskCardView) viewGroup.getChildAt(i)).getDiskData()
                        .equals(diskCardView.getDiskData())) {
                    return (DiskCardView) viewGroup.getChildAt(i);
                }
            }
        }
        return null;
    }

    private void initDisk() {
        if (mDiskContainer.getChildCount() > 0) mDiskContainer.removeAllDiskViews();
        List<DiskData> diskData = StorageUtil.getMountedDisksList(this);
        for (DiskData data : diskData) {
            mDiskContainer.addDiskView(data);
            prepareDiskData(data.getPath());
        }
    }

    private void addDisk(DiskData diskData) {
        List<DiskData> diskDataList = StorageUtil.getMountedDisksList(this);
        for (DiskData data : diskDataList) {
            if (diskData.equals(data)) {
                mDiskContainer.addDiskView(data);
            }
        }
    }

    private void removeDisk(DiskData diskData) {
        mDiskContainer.removeDiskView(diskData);
    }

    @Override
    public void addListener() {
        mVideoCardView.setOnItemClickListener(this);
        mMusicCardView.setOnItemClickListener(this);
        mPictureCardView.setOnItemClickListener(this);
        mDiskContainer.setOnDiskItemClickListener(this);
    }

    @Override
    public void handleDiskIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        Uri uri = intent.getData();
        if (uri == null) return;
        String usbPath = uri.getPath();
        DiskData diskData = new DiskData();
        diskData.setPath(usbPath);
        if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                || action.equals(Intent.ACTION_MEDIA_EJECT)) {
            refreshCountUI(Constants.ALL_REFRESH_ACTION);
            removeDisk(diskData);
            removeDiskData(diskData.getPath());
        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            addDisk(diskData);
            prepareDiskData(diskData.getPath());
        }
    }

    @Override
    public void handleUpdate(String type) {
        refreshCountUI(type);
    }

    @Override
    public void blockFocus() {

    }

    @Override
    public void releaseFocus() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUpdateHandler.removeCallbacksAndMessages(null);
    }

    private void refreshCountUI(String type) {
        switch (type) {
            case Constants.VIDEO_REFRESH_ACTION:
                updateVideoCount();
                break;
            case Constants.MUSIC_REFRESH_ACTION:
                updateMusicCount();
                break;
            case Constants.PICTURE_REFRESH_ACTION:
                updatePictureCount();
                break;
            case Constants.ALL_REFRESH_ACTION:
                updateVideoCount();
                updateMusicCount();
                updatePictureCount();
                prepareMediaData();
                break;
            case Constants.PATH_DELETE_ACTION:
                updateVideoCount();
                updateMusicCount();
                updatePictureCount();
                prepareMediaData();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        currentFocusView = view;
        switch (view.getId()) {
            case R.id.main_video_card_view:
                Intent videoIntent = new Intent(this, VideoListActivity.class);
                //videoIntent.putParcelableArrayListExtra(Constants.TYPE_VIDEO, fileDataMap.get(Constants.TYPE_VIDEO));
                startActivity(videoIntent);
                break;
            case R.id.main_music_card_view:
                Intent musicIntent = new Intent(this, MusicListActivity.class);
               // musicIntent.putParcelableArrayListExtra(Constants.TYPE_MUSIC, fileDataMap.get(Constants.TYPE_MUSIC));
                startActivity(musicIntent);
                break;
            case R.id.main_picture_card_view:
                Intent pictureIntent = new Intent(this, PictureListActivity.class);
                //pictureIntent.putParcelableArrayListExtra(Constants.TYPE_PICTURE, fileDataMap.get(Constants.TYPE_PICTURE));
                startActivity(pictureIntent);
                break;
        }
    }

    @Override
    public void onDiskItemClick(DiskCardView diskCardView, DiskData diskData) {
        currentFocusView = diskCardView;
        Intent diskIntent = new Intent(this, FileListActivity.class);
        diskIntent.putExtra("disk", diskData);
        diskIntent.putParcelableArrayListExtra(diskData.getPath(), fileDataMap.get(diskData.getPath()));
        startActivity(diskIntent);
    }

    private void updateVideoCount() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int videoCount = DatabaseUtil.getInstance(MainActivity.this).getVideoCount();
                Message message = mUpdateHandler.obtainMessage();
                message.what = UPDATE_VIDEO;
                message.obj = videoCount;
                mUpdateHandler.sendMessage(message);
            }
        });
    }

    private void updateMusicCount() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int musicCount = DatabaseUtil.getInstance(MainActivity.this).getMusicCount();
                Message message = mUpdateHandler.obtainMessage();
                message.what = UPDATE_MUSIC;
                message.obj = musicCount;
                mUpdateHandler.sendMessage(message);
            }
        });
    }

    private void updatePictureCount() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int pictureCount = DatabaseUtil.getInstance(MainActivity.this).getPictureCount();
                Message message = mUpdateHandler.obtainMessage();
                message.what = UPDATE_PICTURE;
                message.obj = pictureCount;
                mUpdateHandler.sendMessage(message);
            }
        });
    }


    private void prepareMediaData() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                fileDataMap.put(Constants.TYPE_VIDEO, (ArrayList<? extends BaseData>) FileDataManager
                        .getInstance(MainActivity.this).getAllVideoData());
                fileDataMap.put(Constants.TYPE_MUSIC, (ArrayList<? extends BaseData>) FileDataManager
                        .getInstance(MainActivity.this).getAllMusicData());
                fileDataMap.put(Constants.TYPE_PICTURE, (ArrayList<? extends BaseData>) FileDataManager
                        .getInstance(MainActivity.this).getAllPictureData());
            }
        });
    }

    private void prepareDiskData(final String path) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                fileDataMap.put(path, (ArrayList<FileData>) FileDataManager.getInstance(MainActivity.this)
                        .getPathFileDataWithOutSize(path));
            }
        });
    }

    @SuppressWarnings("all")
    private void removeDiskData(final String path) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (fileDataMap.containsKey(path)) {
                    fileDataMap.remove(path);
                }
            }
        });
    }

    private static class UpdateHandler extends Handler {

        WeakReference<MainActivity> mMainActivityWeakReference;
        MainActivity mMainActivity;

        UpdateHandler(MainActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
            mMainActivity = mMainActivityWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VIDEO:
                    mMainActivity.mVideoCardView.setCountText((int) msg.obj);
                    break;
                case UPDATE_MUSIC:
                    mMainActivity.mMusicCardView.setCountText((int) msg.obj);
                    break;
                case UPDATE_PICTURE:
                    mMainActivity.mPictureCardView.setCountText((int) msg.obj);
                    break;
            }
        }
    }
}
