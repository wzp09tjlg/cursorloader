package com.example.wuzp.cursorloaderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.wuzp.cursorloaderdemo.db.LoadDbHelper;

/**
 * Created by wuzp on 2017/5/16.
 */
public class LoadContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.cursorloader";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LoadDbHelper.TABLE_NAME_READER);

    public static final int READERS_URI_CODE = 0;
    public static final int READER_URI_CODE = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // 关联Uri和Uri_Code
    static {
        sUriMatcher.addURI(AUTHORITY, LoadDbHelper.TABLE_NAME_READER, READERS_URI_CODE);//全表
        sUriMatcher.addURI(AUTHORITY, LoadDbHelper.TABLE_NAME_READER + "/#", READER_URI_CODE);//具体的某行数据
    }

    private Context mContext;
    private SQLiteDatabase mDb;

    public LoadContentProvider() {}

    @Override
    public boolean onCreate() {
        mContext = getContext();
        LoadDbHelper loadDbHelper = new LoadDbHelper(mContext);
        mDb = loadDbHelper.getWritableDatabase();
        return false;
    }

    //得到reader表中的所有记录
    public static String READERS = "vnd.android.cursor.dir/vnd.example.reader";
    //得到一个表信息
    public static String READER = "vnd.android.cursor.item/vnd.example.reader";
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case READERS_URI_CODE:
                return READERS;
            case READER_URI_CODE:
                return READER;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LoadDbHelper.TABLE_NAME_READER);

        switch (sUriMatcher.match(uri)) {
            case READERS_URI_CODE:
                break;
            case READER_URI_CODE:
                queryBuilder.appendWhere(LoadDbHelper.READER_COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor =
                queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case READERS_URI_CODE: {
                long rowID = mDb.insert(LoadDbHelper.TABLE_NAME_READER, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                    mContext.getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
            }
            default:
                throw new IllegalArgumentException("This is a unKnow Uri" + uri.toString());
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case READERS_URI_CODE:
                count = mDb.delete(LoadDbHelper.TABLE_NAME_READER, selection, selectionArgs);
                break;
            case READER_URI_CODE:
                String id = uri.getLastPathSegment();
                count = mDb.delete(LoadDbHelper.TABLE_NAME_READER, LoadDbHelper.READER_COLUMN_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (count > 0) mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case READERS_URI_CODE:
                count = mDb.update(LoadDbHelper.TABLE_NAME_READER, values, selection, selectionArgs);
                break;
            case READER_URI_CODE:
                count =
                        mDb.update(LoadDbHelper.TABLE_NAME_READER, values, LoadDbHelper.READER_COLUMN_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (count > 0) mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }
}
