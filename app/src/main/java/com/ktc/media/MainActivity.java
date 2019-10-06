package com.ktc.media;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.activity.FileListActivity;
import com.ktc.media.activity.MusicListActivity;
import com.ktc.media.activity.PictureListActivity;
import com.ktc.media.activity.VideoListActivity;
import com.ktc.media.base.BaseActivity;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
import com.ktc.media.model.FileData;
import com.ktc.media.util.StorageUtil;
import com.ktc.media.view.DiskCardView;
import com.ktc.media.view.DiskContainer;
import com.ktc.media.view.MusicCardView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.PictureCardView;
import com.ktc.media.view.VideoCardView;

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
        mVideoCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_VIDEO));
        mMusicCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_MUSIC));
        mPictureCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_PICTURE));
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
    public void handleDataIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        refreshCountUI(action);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void refreshCountUI(String type) {
        switch (type) {
            case Constants.VIDEO_REFRESH_ACTION:
                mVideoCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_VIDEO));
                break;
            case Constants.MUSIC_REFRESH_ACTION:
                mMusicCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_MUSIC));
                break;
            case Constants.PICTURE_REFRESH_ACTION:
                mPictureCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_PICTURE));
                break;
            case Constants.ALL_REFRESH_ACTION:
                mVideoCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_VIDEO));
                mMusicCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_MUSIC));
                mPictureCardView.setCountText(FileDataManager.getInstance(this).getCountByType(Constants.FILE_TYPE_PICTURE));
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
                videoIntent.putParcelableArrayListExtra(Constants.TYPE_VIDEO, fileDataMap.get(Constants.TYPE_VIDEO));
                startActivity(videoIntent);
                break;
            case R.id.main_music_card_view:
                Intent musicIntent = new Intent(this, MusicListActivity.class);
                musicIntent.putParcelableArrayListExtra(Constants.TYPE_VIDEO, fileDataMap.get(Constants.TYPE_MUSIC));
                startActivity(musicIntent);
                break;
            case R.id.main_picture_card_view:
                Intent pictureIntent = new Intent(this, PictureListActivity.class);
                pictureIntent.putParcelableArrayListExtra(Constants.TYPE_VIDEO, fileDataMap.get(Constants.TYPE_PICTURE));
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

    private void prepareMediaData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileDataMap.put(Constants.TYPE_VIDEO, (ArrayList<? extends BaseData>) FileDataManager
                        .getInstance(MainActivity.this).getAllVideoData());
                fileDataMap.put(Constants.TYPE_MUSIC, (ArrayList<? extends BaseData>) FileDataManager
                        .getInstance(MainActivity.this).getAllMusicData());
                fileDataMap.put(Constants.TYPE_PICTURE, (ArrayList<? extends BaseData>) FileDataManager
                        .getInstance(MainActivity.this).getAllPictureData());
            }
        }).start();
    }

    private void prepareDiskData(String path) {
        fileDataMap.put(path, (ArrayList<FileData>) FileDataManager.getInstance(this).getPathFileData(path));
    }

    private void removeDiskData(String path) {
        if (fileDataMap.containsKey(path)) {
            fileDataMap.remove(path);
        }
    }
}
