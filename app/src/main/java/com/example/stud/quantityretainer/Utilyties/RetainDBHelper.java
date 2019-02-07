package com.example.stud.quantityretainer.Utilyties;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RetainDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "RetainEntities.db";

    private String mTableName;

    public RetainDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RetainDBContract.RetainEntity.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // query to obtain the names of all tables in your database
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            List<String> tables = new ArrayList<>();

    // iterate over the result set, adding every table name to a list
            while (c.moveToNext()) {
                tables.add(c.getString(0));
            }

    // call DROP TABLE on every table name
            for (String table : tables) {
                if (table.startsWith("sqlite_") || table.startsWith("android_")) {
                    continue;
                }
                String dropQuery = "DROP TABLE IF EXISTS " + table;
                db.execSQL(dropQuery);
            }
        }
        onCreate(db);
    }

}
