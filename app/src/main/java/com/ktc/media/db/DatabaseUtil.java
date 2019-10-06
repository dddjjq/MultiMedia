package com.ktc.media.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ktc.media.constant.Constants;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.FileData;
import com.ktc.media.model.MusicData;
import com.ktc.media.model.VideoData;

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

    public void insertVideoData(VideoData videoData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, videoData.getName());
        contentValues.put(DBHelper.PATH_KEY, videoData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, videoData.getType());
        contentValues.put(DBHelper.DURATION_KEY, videoData.getDurationString());
        sqLiteDatabase.insert(DBHelper.TB_VIDEO, null, contentValues);
    }

    public List<VideoData> getAllVideoData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_VIDEO, null, null,
                null, null, null, null);
        ArrayList<VideoData> mainDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            VideoData videoData = new VideoData();
            videoData.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            videoData.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            videoData.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            videoData.setDurationString(cursor.getString(cursor.getColumnIndex(DBHelper.DURATION_KEY)));
            mainDatas.add(videoData);
        }
        cursor.close();
        return mainDatas;
    }

    public void insertMusicData(MusicData musicData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, musicData.getName());
        contentValues.put(DBHelper.PATH_KEY, musicData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, musicData.getType());
        contentValues.put(DBHelper.DURATION_KEY, musicData.getDurationString());
        contentValues.put(DBHelper.MUSIC_NAME_KEY, musicData.getSongName());
        sqLiteDatabase.insert(DBHelper.TB_MUSIC, null, contentValues);
    }

    public List<MusicData> getAllMusicData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_MUSIC, null, null,
                null, null, null, null);
        ArrayList<MusicData> mainDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData();
            musicData.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            musicData.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            musicData.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            musicData.setDurationString(cursor.getString(cursor.getColumnIndex(DBHelper.DURATION_KEY)));
            musicData.setSongName(cursor.getString(cursor.getColumnIndex(DBHelper.MUSIC_NAME_KEY)));
            mainDatas.add(musicData);
        }
        cursor.close();
        return mainDatas;
    }

    public void insertPictureData(FileData fileData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, fileData.getName());
        contentValues.put(DBHelper.PATH_KEY, fileData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, fileData.getType());
        contentValues.put(DBHelper.SIZE_KEY, fileData.getSizeDescription());
        sqLiteDatabase.insert(DBHelper.TB_PICTURE, null, contentValues);
    }

    public List<FileData> getAllPictureData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_PICTURE, null, null,
                null, null, null, null);
        ArrayList<FileData> mainDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            FileData fileData = new FileData();
            fileData.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            fileData.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            fileData.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            fileData.setSizeDescription(cursor.getString(cursor.getColumnIndex(DBHelper.SIZE_KEY)));
            mainDatas.add(fileData);
        }
        cursor.close();
        return mainDatas;
    }

    public void insertFileData(FileData fileData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_KEY, fileData.getName());
        contentValues.put(DBHelper.PATH_KEY, fileData.getPath());
        contentValues.put(DBHelper.TYPE_KEY, fileData.getType());
        contentValues.put(DBHelper.SIZE_KEY, fileData.getSizeDescription());
        sqLiteDatabase.insert(DBHelper.TB_FILE, null, contentValues);
    }

    public List<FileData> getAllFileData() {
        Cursor cursor = sqLiteDatabase.query(DBHelper.TB_FILE, null, null,
                null, null, null, null);
        ArrayList<FileData> mainDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            FileData fileData = new FileData();
            fileData.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_KEY)));
            fileData.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.PATH_KEY)));
            fileData.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.TYPE_KEY)));
            fileData.setSizeDescription(cursor.getString(cursor.getColumnIndex(DBHelper.SIZE_KEY)));
            mainDatas.add(fileData);
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
        sqLiteDatabase.delete(DBHelper.TB_FILE, DBHelper.PATH_KEY + " like ? ",
                new String[]{path + "%"});
        if (needBroadcast) {
            mContext.sendBroadcast(new Intent(Constants.PATH_DELETE_ACTION));
        }
    }

    public void closeDatabase() {
        sqLiteDatabase.close();
    }
}
