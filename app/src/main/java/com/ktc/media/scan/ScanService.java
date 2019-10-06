package com.ktc.media.scan;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;
import com.ktc.media.db.DatabaseUtil;
import com.ktc.media.model.FileData;
import com.ktc.media.model.MusicData;
import com.ktc.media.model.VideoData;
import com.ktc.media.util.FileSizeUtil;
import com.ktc.media.util.Tools;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件扫描服务
 */
public class ScanService extends IntentService {

    private static final String TAG = ScanService.class.getSimpleName();

    private List<Integer> countList;

    public ScanService() {
        super("ScanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        try {
            String diskPath = intent.getStringExtra("diskPath");
            ScanThread scanThread = new ScanThread(diskPath);
            scanThread.start();
            countList = new ArrayList<>();
            countList.add(0);
            countList.add(0);
            countList.add(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ScanThread extends Thread {

        String mPath;

        ScanThread(String path) {
            mPath = path;
        }

        @Override
        public void run() {
            try {
                File folder = new File(mPath);
                if (folder.exists()) {
                    clearData(mPath);
                    for (final File file : folder.listFiles()) {
                        if (file.exists()) {
                            startScan(file);
                        }
                    }
                    sendBroadcast(new Intent(Constants.ALL_REFRESH_ACTION));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startScan(File folder) {
        try {
            if (folder.exists() && folder.isDirectory()) {
                if (folder.list() != null && folder.list().length > 0) {
                    for (final File file : folder.listFiles()) {
                        if (file.isDirectory()) {
                            startScan(file);
                        } else {
                            scanDirectory(file);
                        }
                    }
                }
            } else if (folder.exists() && folder.isFile()) {
                scanDirectory(folder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanDirectory(final File file) {
        int fileType = getFileType(file);
        insertData(fileType, file);
        sendRefreshAction(fileType);
    }

    private int getFileType(File file) {
        String[] videoTypes = getResources().getStringArray(R.array.video_filter);
        String[] pictureTypes = getResources().getStringArray(R.array.picture_filter);
        String[] musicTypes = getResources().getStringArray(R.array.audio_filter);
        List<String[]> typeList = new ArrayList<>();
        typeList.add(videoTypes);
        typeList.add(pictureTypes);
        typeList.add(musicTypes);
        List<Integer> types = new ArrayList<>();
        types.add(Constants.FILE_TYPE_VIDEO);
        types.add(Constants.FILE_TYPE_PICTURE);
        types.add(Constants.FILE_TYPE_MUSIC);
        for (int i = 0; i < typeList.size(); i++) {
            for (String s : typeList.get(i)) {
                if (file.getName().toLowerCase().contains(s)) {
                    return types.get(i);
                }
            }
        }
        if (!file.isDirectory()) {
            return Constants.FILE_TYPE_FILE;
        }
        return Constants.FILE_TYPE_DIR;
    }

    //插入之前先清除
    private void clearData(String path) {
        DatabaseUtil.getInstance(getApplicationContext()).deletePathData(path, false);
    }

    private void insertData(int fileType, File file) {
        try {
            switch (fileType) {
                case Constants.FILE_TYPE_VIDEO:
                    VideoData videoData = new VideoData();
                    videoData.setPath(file.getAbsolutePath());
                    videoData.setType(Constants.FILE_TYPE_VIDEO);
                    videoData.setName(file.getName());
                    videoData.setDurationString(Tools.getDurationString(file.getAbsolutePath()));
                    DatabaseUtil.getInstance(getApplicationContext()).insertVideoData(videoData);
                    countList.set(0, countList.get(0) + 1);
                    break;
                case Constants.FILE_TYPE_PICTURE:
                    BigInteger size = BigInteger.valueOf(file.length());
                    if (size.compareTo(BigInteger.valueOf(1024 * 10)) > 0) {//只扫描大于10KB的图片
                        FileData fileData = new FileData();
                        fileData.setPath(file.getAbsolutePath());
                        fileData.setType(Constants.FILE_TYPE_PICTURE);
                        fileData.setName(file.getName());
                        fileData.setSizeDescription(FileSizeUtil.getFileSizeDescription(file.getAbsolutePath()));
                        DatabaseUtil.getInstance(getApplicationContext()).insertPictureData(fileData);
                        countList.set(1, countList.get(1) + 1);
                    }
                    break;
                case Constants.FILE_TYPE_MUSIC:
                    MusicData musicData = new MusicData();
                    musicData.setPath(file.getAbsolutePath());
                    musicData.setType(Constants.FILE_TYPE_MUSIC);
                    musicData.setName(file.getName());
                    musicData.setSongName(getSongName(file.getAbsolutePath()));
                    musicData.setDurationString(Tools.getDurationString(file.getAbsolutePath()));
                    DatabaseUtil.getInstance(getApplicationContext()).insertMusicData(musicData);
                    countList.set(2, countList.get(2) + 1);
                    break;
                case Constants.FILE_TYPE_FILE:
                    FileData fileData = new FileData();
                    fileData.setPath(file.getAbsolutePath());
                    fileData.setType(Constants.FILE_TYPE_FILE);
                    fileData.setName(file.getName());
                    fileData.setSizeDescription(FileSizeUtil.getFileSizeDescription(file.getAbsolutePath()));
                    DatabaseUtil.getInstance(getApplicationContext()).insertFileData(fileData);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendRefreshAction(int fileType) {
        switch (fileType) {
            case Constants.FILE_TYPE_VIDEO:
                int videoCount = countList.get(0);
                if (videoCount % Constants.REFRESH_COUNT == 0) {
                    sendBroadcast(new Intent(Constants.VIDEO_REFRESH_ACTION));
                }
                break;
            case Constants.FILE_TYPE_PICTURE:
                int pictureCount = countList.get(1);
                if (pictureCount % Constants.REFRESH_COUNT == 0) {
                    sendBroadcast(new Intent(Constants.PICTURE_REFRESH_ACTION));
                }
                break;
            case Constants.FILE_TYPE_MUSIC:
                int musicCount = countList.get(2);
                if (musicCount % Constants.REFRESH_COUNT == 0) {
                    sendBroadcast(new Intent(Constants.MUSIC_REFRESH_ACTION));
                }
                break;
        }
    }

    private String getSongName(String path) {
        Cursor cursor = null;
        String where = MediaStore.MediaColumns.DATA + "=?";
        String result = null;
        try {
            cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.TITLE,},
                    where, new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
}
