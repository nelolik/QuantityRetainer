package com.nelolik.stud.quantityretainer.Utilyties;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class RetentionsProvider {

    private SQLiteDatabase mDb;
    private String mRetentionKey;

    public RetentionsProvider(Context context, String retentionKey) {
        RetainDBHelper dbHelper = new RetainDBHelper(context);
        mDb = dbHelper.getWritableDatabase();
        mRetentionKey = retentionKey;
    }

    public Cursor getAllRecords() {
        try {
            String selection = RetainDBContract.RetainEntity.COLUMN_NAME + "=?";
            return mDb.query(RetainDBContract.RetainEntity.TABLE_NAME,
                    null,
                    selection,
                    new String[]{mRetentionKey},
                    null,
                    null,
                    RetainDBContract.RetainEntity._ID + " DESC"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addNewCountToDB(int count) {
        ContentValues cv = new ContentValues();
        cv.put(RetainDBContract.RetainEntity.COLUMN_COUNT, count);
        cv.put(RetainDBContract.RetainEntity.COLUMN_NAME, mRetentionKey);
        cv.put(RetainDBContract.RetainEntity.COLUMN_DATE, new Date().getTime());
        try {
            mDb.beginTransaction();
            mDb.insert(RetainDBContract.RetainEntity.TABLE_NAME, null, cv);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
    }
}
