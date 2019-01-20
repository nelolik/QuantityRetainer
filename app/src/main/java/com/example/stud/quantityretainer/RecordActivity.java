package com.example.stud.quantityretainer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stud.quantityretainer.Utilyties.RetainDBContract;
import com.example.stud.quantityretainer.Utilyties.RetainDBHelper;

import java.util.Date;


public class RecordActivity extends AppCompatActivity {
    public static final String TAG_NAME = "RETENTION_NAME";
    public static final String TAG_TABLE = "TABLE_NAME";

    TextView mTotalText;
    TextView mTotalCount;
    private EditText mAddCount;
    private Button mAddButton;
    private RecyclerView mCountRecycler;
    private String mRetentionName;
    private String mCountName;
    private SQLiteDatabase mDb;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        mTotalText = findViewById(R.id.total_text);
        mTotalCount = findViewById(R.id.total_count);
        mAddCount = findViewById(R.id.add_count_input);
        mAddButton = findViewById(R.id.btn_add);
        mCountRecycler = findViewById(R.id.count_recycler);

        Intent intent = getIntent();

        if (intent.hasExtra(TAG_NAME)) {
            mRetentionName = intent.getStringExtra(TAG_NAME);
            getSupportActionBar().setTitle(mRetentionName);
        }
        if (intent.hasExtra(TAG_TABLE)) {
            mCountName = intent.getStringExtra(TAG_TABLE);
            RetainDBHelper dbHelper = new RetainDBHelper(this, mCountName);
            mDb = dbHelper.getWritableDatabase();
            mCursor = getAllRecords();
            mTotalCount.setText(String.valueOf(getTotalCount(mCursor)));
        }
        mCountRecycler.setLayoutManager(new LinearLayoutManager(this));
        RetentionRecyclerAdapter adapter =
                new RetentionRecyclerAdapter(getApplicationContext(), mCursor);
        mCountRecycler.setAdapter(adapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCount();
                mTotalCount.setText(String.valueOf(getTotalCount(mCursor)));
            }
        });
    }

    private Cursor getAllRecords() {
        try {
            String selection = RetainDBContract.RetainEntity.COLUMN_NAME + "=?";
            return mDb.query(RetainDBContract.RetainEntity.TABLE_NAME,
                    null,
                    selection,
                    new String[]{mCountName},
                    null,
                    null,
                    RetainDBContract.RetainEntity._ID + " DESC"
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getTotalCount(Cursor cursor) {
        if (cursor == null) {
            return 0;
        }

        cursor.moveToPosition(-1);
        int columnIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_COUNT);
        int count = 0;
        while (cursor.moveToNext()) {
            count += cursor.getInt(columnIndex);
        }
        return count;
    }

    private void addNewCount() {
        int newCount = Integer.parseInt(mAddCount.getText().toString());

        ContentValues cv = new ContentValues();
        cv.put(RetainDBContract.RetainEntity.COLUMN_COUNT, newCount);
        cv.put(RetainDBContract.RetainEntity.COLUMN_NAME, mCountName);
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

        mCursor = getAllRecords();
        RetentionRecyclerAdapter adapter =
                new RetentionRecyclerAdapter(this, mCursor);
        mCountRecycler.setAdapter(adapter);
    }
}
