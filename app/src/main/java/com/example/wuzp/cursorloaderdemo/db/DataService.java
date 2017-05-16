package com.example.wuzp.cursorloaderdemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.wuzp.cursorloaderdemo.HApplication;
import com.example.wuzp.cursorloaderdemo.bean.LoadBean;

import java.util.ArrayList;

/**
 * Created by wuzp on 2017/5/16.
 * 因为要封装一个直接获取数据库的数据，
 * 所以这里就直接持有数据库LoadDbHelper的引用，
 * 对数据进行封装，
 * 然后暴露查插删改的方法
 * 对数据库的操作 需要处理多线程的同步问题
 */
public class DataService {
    private static DataService mService;
    private LoadDbHelper loadDbHelper;
    private SQLiteDatabase mDb;
    private byte[] mLockTarget = new byte[0];
    //加锁的标的物，是针对DataService这个实例对象来讲的锁，
    //所以锁住的标的物应该是这个对象所持有

    private DataService(){
        loadDbHelper = new LoadDbHelper(HApplication.gContext);
        mDb = loadDbHelper.getWritableDatabase();
    }

    public static  DataService getInstance(){
        if(mService == null){
            mService = new DataService();
        }
        return mService;
    }

    //query
    //查询某一项： 查询的字段  查询条件 查询条件的值 排序
    public LoadBean query(@Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder){
        LoadBean bean = new LoadBean();
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getReadableDatabase();
            }
            Cursor cursor = null;
            try {
                mDb.beginTransaction();
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(LoadDbHelper.TABLE_NAME_READER);
                cursor =
                        queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);

                bean.setUid(cursor.getString(cursor.getColumnIndex(LoadDbHelper.READER_COLUMN_UID)));
                bean.setSummary(cursor.getString(cursor.getColumnIndex(LoadDbHelper.READER_COLUMN_SUMMARY)));
                bean.setDescription(cursor.getString(cursor.getColumnIndex(LoadDbHelper.READER_COLUMN_DESCRIPTION)));
                mDb.setTransactionSuccessful();
            }catch (Exception e){

            }finally {
               if(cursor != null){
                   cursor.close();
               }

               if(mDb.isOpen()){
                   mDb.endTransaction();
               }
            }
             }
        return bean;
    }
    //查询全部： 查询的字段  查询条件 查询条件的值 排序
    public ArrayList<LoadBean> queryAll(@Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder){
        ArrayList<LoadBean> list = new ArrayList<>();
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getReadableDatabase();
            }
            Cursor cursor = null;
            try {
                mDb.beginTransaction();
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(LoadDbHelper.TABLE_NAME_READER);
                cursor =
                        queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.moveToPosition(0);
                while (cursor.moveToNext()){
                    LoadBean bean = new LoadBean();
                    bean.setUid(cursor.getString(cursor.getColumnIndex(LoadDbHelper.READER_COLUMN_UID)));
                    bean.setSummary(cursor.getString(cursor.getColumnIndex(LoadDbHelper.READER_COLUMN_SUMMARY)));
                    bean.setDescription(cursor.getString(cursor.getColumnIndex(LoadDbHelper.READER_COLUMN_DESCRIPTION)));
                    list.add(bean);
                }
                mDb.setTransactionSuccessful();
            }catch (Exception e){}finally {
                if(cursor != null){
                    cursor.close();
                }

                if(mDb.isOpen()){
                    mDb.endTransaction();
                }
            }

        }
        return list;
    }

    //insert
    public long insert(LoadBean loadBean){
        long rowID = -1;
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getWritableDatabase();
            }
            try {
                mDb.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(LoadDbHelper.READER_COLUMN_UID,loadBean.getUid());
                values.put(LoadDbHelper.READER_COLUMN_SUMMARY,loadBean.getSummary());
                values.put(LoadDbHelper.READER_COLUMN_DESCRIPTION,loadBean.getDescription());
                rowID = mDb.insert(LoadDbHelper.TABLE_NAME_READER, "", values);
                mDb.setTransactionSuccessful();
            }catch (Exception e){}finally {
                if(mDb.isOpen()){
                    mDb.endTransaction();
                }
            }

        }
        return rowID;
    }

    public long insertAll(ArrayList<LoadBean> data){
        long rowID = -1;
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getWritableDatabase();
            }

            try {
                mDb.beginTransaction();
                for(int i=0;i<data.size();i++){
                    LoadBean loadBean = data.get(i);
                    ContentValues values = new ContentValues();
                    values.put(LoadDbHelper.READER_COLUMN_UID,loadBean.getUid());
                    values.put(LoadDbHelper.READER_COLUMN_SUMMARY,loadBean.getSummary());
                    values.put(LoadDbHelper.READER_COLUMN_DESCRIPTION,loadBean.getDescription());
                    rowID = mDb.insert(LoadDbHelper.TABLE_NAME_READER, "", values);
                }
                mDb.setTransactionSuccessful();
            }catch (Exception e){}finally {
                if(mDb.isOpen()){
                    mDb.endTransaction();
                }
            }

        }
        return rowID;
    }

    //update
    public int update(LoadBean bean,@Nullable String selection, @Nullable String[] selectionArgs){
        int count = 0;
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getWritableDatabase();
            }

            try {
                mDb.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(LoadDbHelper.READER_COLUMN_UID,bean.getUid());
                values.put(LoadDbHelper.READER_COLUMN_SUMMARY,bean.getSummary());
                values.put(LoadDbHelper.READER_COLUMN_DESCRIPTION,bean.getDescription());

                count = mDb.update(LoadDbHelper.TABLE_NAME_READER, values, selection, selectionArgs);
                mDb.setTransactionSuccessful();
            }catch (Exception e){}finally {
                if(mDb.isOpen()){
                    mDb.endTransaction();
                }
            }

        }
        return count;
    }

    //delete
    //删除某一项 : 删除条件 删除条件的值
    public int delete(String uid,@Nullable String selection, @Nullable String[] selectionArgs){
        int count = -1;
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getWritableDatabase();
            }
            try {
                mDb.beginTransaction();
                count = mDb.delete(LoadDbHelper.TABLE_NAME_READER, LoadDbHelper.READER_COLUMN_UID + " = " + uid +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                mDb.setTransactionSuccessful();
            }catch (Exception e){}finally {
                if(mDb.isOpen()){
                    mDb.endTransaction();
                }
            }
            }
        return count;
    }

    //删除全部 : 删除条件 删除条件的值
    public int deleteAll(@Nullable String selection, @Nullable String[] selectionArgs){
        int count = -1;
        synchronized (mLockTarget){
            if(mDb == null){
                mDb = loadDbHelper.getWritableDatabase();
            }
            try {
                mDb.beginTransaction();
                count = mDb.delete(LoadDbHelper.TABLE_NAME_READER, selection, selectionArgs);
                mDb.setTransactionSuccessful();
            }catch (Exception e){}finally {
                if(mDb.isOpen()){
                    mDb.endTransaction();
                }
            }
        }
        return count;
    }
}
