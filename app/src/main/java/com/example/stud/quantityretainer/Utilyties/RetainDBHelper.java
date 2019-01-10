package com.example.stud.quantityretainer.Utilyties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RetainDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RetainEntities.db";

    private String mTableName;

    public RetainDBHelper (Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mTableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = RetainDBContract.RetainEntity.SQL_CREATE_PREFIX + mTableName +
                RetainDBContract.RetainEntity.SQL_CREATE_SUFFIX;
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setTableName(String tableName) {
        this.mTableName = tableName;
    }
}
