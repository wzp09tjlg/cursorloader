package com.example.wuzp.cursorloaderdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wuzp on 2017/5/16.
 */
public class LoadDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "reader.db";//数据库的名字
    public static final int    DB_VERSION = 1;       //数据库的版本
    public static final String TABLE_NAME_READER = "reader";//表的名字

    public static final String READER_COLUMN_ID = "_id";//系统自带的id
    public static final String READER_COLUMN_UID = "uid";
    public static final String READER_COLUMN_SUMMARY = "summary";
    public static final String READER_COLUMN_DESCRIPTION = "description";

    private static final String TABLE_READER_CREATE = "create table "
            + TABLE_NAME_READER
            + "("
            + READER_COLUMN_ID + " integer primary key autoincrement, "
            + READER_COLUMN_UID + " text not null,"
            + READER_COLUMN_SUMMARY + " text not null,"
            + READER_COLUMN_DESCRIPTION
            + " text not null"
            + ");";

    private static final String TABLE_READER_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME_READER;

    public LoadDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(TABLE_READER_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL(TABLE_READER_DROP);
       onCreate(db);
    }
}
