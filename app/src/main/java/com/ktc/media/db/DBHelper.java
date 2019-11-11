package com.ktc.media.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private String TAG = DBHelper.class.getSimpleName();

    public static final String DB_NAME = "media.db";

    public static final String TB_VIDEO = "video";
    public static final String TB_PICTURE = "picture";
    public static final String TB_MUSIC = "music";

    public static final String PATH_KEY = "path";
    public static final String TYPE_KEY = "type";
    public static final String NAME_KEY = "name";

    private volatile static DBHelper instance = null;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VIDEO = "CREATE TABLE " + TB_VIDEO + "(_id INTEGER PRIMARY key autoincrement,"
                + "path text," + "type Integer," + " name text)";
        String CREATE_PHOTO = "CREATE TABLE " + TB_PICTURE + "(_id INTEGER PRIMARY key autoincrement,"
                + "path text," + "type Integer," + " name text)";
        String CREATE_MUSIC = "CREATE TABLE " + TB_MUSIC + "(_id INTEGER PRIMARY key autoincrement,"
                + "path text," + "type Integer," + " name text)";

        db.execSQL(CREATE_MUSIC);
        db.execSQL(CREATE_VIDEO);
        db.execSQL(CREATE_PHOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
