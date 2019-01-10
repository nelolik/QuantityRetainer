package com.example.stud.quantityretainer.Utilyties;

import android.provider.BaseColumns;

public class RetainDBContract {
    private RetainDBContract() {}

    public static class RetainEntity implements BaseColumns {
        public static final String COLUMN_COUNT = "count";
        public static final String COLUMN_DATE = "date";
        public static final String SQL_CREATE_PREFIX = "CREATE TABLE ";
        public static final String SQL_CREATE_SUFFIX = " (" + _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COUNT + " INTEGER NOT NULL, " +
                COLUMN_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        private static final String SQL_DELETE_ENTRIES_PREFIX =
                "DROP TABLE IF EXISTS ";
    }

    public static class Retentions implements BaseColumns {
        public static final String TABLE_NAME = "retains";
        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_RETENTION_NAME = "retention_name";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME +
                " (" + _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TABLE_NAME + " TEXT, " +
                COLUMN_RETENTION_NAME + " TEXT);";
        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
