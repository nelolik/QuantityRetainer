package com.nelolik.stud.quantityretainer.Utilyties;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class RecordsProvider {
    static String topics[] = {"one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine"};

    public int getRecordsCount() {
        return topics.length;
    }

    public String getRecord(int position) {
        if (position < 0 || position >= topics.length) {
            return "";
        }

        return topics[position];
    }


    public static void writeFakeRetentionsNames(SQLiteDatabase db) {
        if (db == null) {
            return;
        }

        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();

            cv.put(RetainDBContract.Retentions.COLUMN_RETENTION_NAME, "Dordje sempa");
            cv.put(RetainDBContract.Retentions.COLUMN_TABLE_NAME, "dordjesempa");
            db.insert(RetainDBContract.Retentions.TABLE_NAME,
                    null, cv);

            cv = new ContentValues();
            cv.put(RetainDBContract.Retentions.COLUMN_RETENTION_NAME, "Mandala");
            cv.put(RetainDBContract.Retentions.COLUMN_TABLE_NAME, "mandala");
            db.insert(RetainDBContract.Retentions.TABLE_NAME,
                    null, cv);

            cv = new ContentValues();
            cv.put(RetainDBContract.Retentions.COLUMN_RETENTION_NAME, "Prostrations");
            cv.put(RetainDBContract.Retentions.COLUMN_TABLE_NAME, "prostrations");
            db.insert(RetainDBContract.Retentions.TABLE_NAME,
                    null, cv);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }
}
