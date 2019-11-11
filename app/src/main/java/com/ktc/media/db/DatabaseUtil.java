package com.ktc.media.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ktc.media.model.BaseData;
import com.ktc.media.model.FileData;
import com.ktc.media.model.MusicData;
import com.ktc.media.model.VideoData;
import com.ktc.media.scan.observe.FileObserverInstance;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    private SQLiteDatabase sqLiteDatabase;
    private volatile static DatabaseUtil instance;
    private Context mContext;

    private DatabaseUtil(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        mContext = context;
    }

    public static DatabaseUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseUtil.class) {
                if (instance == null) {
                    instance = new DatabaseUtil(context);
                }
            }
        }
        return instance;
    }

    public void insertData(ContentValues contentValues, String tableName) {
        sqLiteDatabase.insert(tableName, null, contentValues);
    }

    public void insertVideoData(VideoData videoData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, videoData.getName());
        contentValues.put(DBHelper.PATH_KEY, videoData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, videoData.getType());
        sqLiteDatabase.insert(DBHelper.TB_VIDEO, null, contentValues);
    }

    public List<VideoData> getAllVideoData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_VIDEO, null, null,
                null, null, null, null);
        ArrayList<VideoData> mainDatas = new ArrayList<>();
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            WeakReference<VideoData> videoDataWeakReference = new WeakReference<>(new VideoData());
            videoDataWeakReference.get().setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            videoDataWeakReference.get().setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            videoDataWeakReference.get().setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            mainDatas.add(videoDataWeakReference.get());
        }
        cursor.close();
        return mainDatas;
    }

    public void insertMusicData(MusicData musicData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, musicData.getName());
        contentValues.put(DBHelper.PATH_KEY, musicData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, musicData.getType());
        sqLiteDatabase.insert(DBHelper.TB_MUSIC, null, contentValues);
    }

    public List<MusicData> getAllMusicData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_MUSIC, null, null,
                null, null, null, null);
        ArrayList<MusicData> mainDatas = new ArrayList<>();
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            WeakReference<MusicData> musicDataWeakReference = new WeakReference<>(new MusicData());
            musicDataWeakReference.get().setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            musicDataWeakReference.get().setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            musicDataWeakReference.get().setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            mainDatas.add(musicDataWeakReference.get());
        }
        cursor.close();
        return mainDatas;
    }

    public void insertPictureData(FileData fileData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, fileData.getName());
        contentValues.put(DBHelper.PATH_KEY, fileData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, fileData.getType());
        sqLiteDatabase.insert(DBHelper.TB_PICTURE, null, contentValues);
    }

    public List<FileData> getAllPictureData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_PICTURE, null, null,
                null, null, null, null);
        ArrayList<FileData> mainDatas = new ArrayList<>();
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            WeakReference<FileData> pictureDataWeakReference = new WeakReference<>(new FileData());
            pictureDataWeakReference.get().setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            pictureDataWeakReference.get().setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            pictureDataWeakReference.get().setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            mainDatas.add(pictureDataWeakReference.get());
        }
        cursor.close();
        return mainDatas;
    }

    public void insertData(String tbName, BaseData data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, data.getName());
        contentValues.put(DBHelper.PATH_KEY, data.getPath());
        contentValues.put(DBHelper.TYPE_KEY, data.getType());
        sqLiteDatabase.insert(tbName, null, contentValues);
    }

    public List<BaseData> getAllData(String tableName) {
        Cursor cursor = sqLiteDatabase.query(tableName, null, null,
                null, null, null, null);
        ArrayList<BaseData> mainDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            BaseData baseData = new BaseData();
            baseData.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            baseData.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            baseData.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            mainDatas.add(baseData);
        }
        cursor.close();
        return mainDatas;
    }

    public void deletePathData(String path, boolean needBroadcast) {
        sqLiteDatabase.delete(DBHelper.TB_VIDEO, DBHelper.PATH_KEY + " like ? ",
                new String[]{path + "%"});
        sqLiteDatabase.delete(DBHelper.TB_MUSIC, DBHelper.PATH_KEY + " like ? ",
                new String[]{path + "%"});
        sqLiteDatabase.delete(DBHelper.TB_PICTURE, DBHelper.PATH_KEY + " like ? ",
                new String[]{path + "%"});
        if (needBroadcast) {
            FileObserverInstance.getInstance().notifyDeleteAction();
        }
    }

    public void deleteAllData() {
        sqLiteDatabase.delete(DBHelper.TB_VIDEO, null, null);
        sqLiteDatabase.delete(DBHelper.TB_MUSIC, null, null);
        sqLiteDatabase.delete(DBHelper.TB_PICTURE, null, null);
        FileObserverInstance.getInstance().notifyDeleteAction();
    }

    public int rawQueryCount(String tableName) {
        try {
            int count = 0;
            Cursor curCount = sqLiteDatabase.rawQuery("select count (_id) from " + tableName, null);
            while (curCount != null && curCount.moveToNext()) {
                count = curCount.getInt(0);
            }
            curCount.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getVideoCount() {
        return rawQueryCount(DBHelper.TB_VIDEO);
    }

    public int getMusicCount() {
        return rawQueryCount(DBHelper.TB_MUSIC);
    }

    public int getPictureCount() {
        return rawQueryCount(DBHelper.TB_PICTURE);
    }

    public void closeDatabase() {
        sqLiteDatabase.close();
    }
}
