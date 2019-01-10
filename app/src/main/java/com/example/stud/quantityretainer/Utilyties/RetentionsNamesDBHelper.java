package com.example.stud.quantityretainer.Utilyties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RetentionsNamesDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Retentions.db";
    public static final int DATABASE_VERSION = 1;

    public RetentionsNamesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RetainDBContract.Retentions.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
