package com.ktc.media.scan;

import android.content.ContentValues;
import android.content.Context;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;
import com.ktc.media.db.DBHelper;
import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.model.BaseData;
import com.ktc.media.scan.observe.FileObserverInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileScanManager {

    private Context mContext;
    private List<Integer> countList;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    public String[] videoTypes;
    public String[] musicTypes;
    public String[] pictureTypes;
    public int videoType = Constants.FILE_TYPE_VIDEO;
    public int musicType = Constants.FILE_TYPE_MUSIC;
    public int pictureType = Constants.FILE_TYPE_PICTURE;
    private FileObserverInstance mFileObserverInstance;

    public FileScanManager(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        countList = new ArrayList<>();
        countList.add(0);
        countList.add(0);
        countList.add(0);
        videoTypes = mContext.getResources().getStringArray(R.array.video_filter);
        musicTypes = mContext.getResources().getStringArray(R.array.audio_filter);
        pictureTypes = mContext.getResources().getStringArray(R.array.picture_filter);
        mFileObserverInstance = FileObserverInstance.getInstance();
    }

    public void insertData(final int fileType, final BaseData baseData) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues values = new ContentValues();
                    switch (fileType) {
                        case Constants.FILE_TYPE_VIDEO:
                            values.put(DBHelper.PATH_KEY, baseData.getPath());
                            values.put(DBHelper.TYPE_KEY, Constants.FILE_TYPE_VIDEO);
                            values.put(DBHelper.NAME_KEY, baseData.getName());
                            DatabaseUtil.getInstance(mContext).insertData(values, DBHelper.TB_VIDEO);
                            countList.set(0, countList.get(0) + 1);
                            break;
                        case Constants.FILE_TYPE_PICTURE:
                            values.put(DBHelper.PATH_KEY, baseData.getPath());
                            values.put(DBHelper.TYPE_KEY, Constants.FILE_TYPE_PICTURE);
                            values.put(DBHelper.NAME_KEY, baseData.getName());
                            DatabaseUtil.getInstance(mContext).insertData(values, DBHelper.TB_PICTURE);
                            countList.set(1, countList.get(1) + 1);
                            break;
                        case Constants.FILE_TYPE_MUSIC:
                            values.put(DBHelper.PATH_KEY, baseData.getPath());
                            values.put(DBHelper.TYPE_KEY, Constants.FILE_TYPE_MUSIC);
                            values.put(DBHelper.NAME_KEY, baseData.getName());
                            DatabaseUtil.getInstance(mContext).insertData(values, DBHelper.TB_MUSIC);
                            countList.set(2, countList.get(2) + 1);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendRefreshAction(fileType);
            }
        });
    }

    public void sendRefreshAction(final int fileType) {
        switch (fileType) {
            case Constants.FILE_TYPE_VIDEO:
                int videoCount = countList.get(0);
                if (videoCount % Constants.REFRESH_COUNT == 0) {
                    mFileObserverInstance.notifyVideoAction();
                }
                break;
            case Constants.FILE_TYPE_PICTURE:
                int pictureCount = countList.get(1);
                if (pictureCount % Constants.REFRESH_COUNT == 0) {
                    mFileObserverInstance.notifyPictureAction();
                }
                break;
            case Constants.FILE_TYPE_MUSIC:
                int musicCount = countList.get(2);
                if (musicCount % Constants.REFRESH_COUNT == 0) {
                    mFileObserverInstance.notifyMusicAction();
                }
                break;
        }
    }

    public void sendFinishAction() {
        mFileObserverInstance.notifyAllAction();
    }
}
