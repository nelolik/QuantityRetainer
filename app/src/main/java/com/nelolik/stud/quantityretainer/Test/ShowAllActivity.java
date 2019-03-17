package com.nelolik.stud.quantityretainer.Test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nelolik.stud.quantityretainer.R;
import com.nelolik.stud.quantityretainer.Utilyties.RetainDBContract;
import com.nelolik.stud.quantityretainer.Utilyties.RetainDBHelper;

public class ShowAllActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private ShowAllRecyclerAdapter mAdapter;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        mWorkingThread = new HandlerThread("ShowAllThread");
        mWorkingThread.start();
        mDbThreadHandler = new Handler(mWorkingThread.getLooper());

        RecyclerView mRecyclerView = findViewById(R.id.all_rec_recycler);

        RetainDBHelper dbHelper = new RetainDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShowAllRecyclerAdapter(getApplicationContext(), mCursor);
        mRecyclerView.setAdapter(mAdapter);

        getAllRecordsAndShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWorkingThread.quit();
    }

    private void getAllRecordsAndShow() {
        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                getAllRecords();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setCursor(mCursor);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void getAllRecords() {
        try {
            mCursor = mDb.query(RetainDBContract.RetainEntity.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    RetainDBContract.RetainEntity._ID + " DESC"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
