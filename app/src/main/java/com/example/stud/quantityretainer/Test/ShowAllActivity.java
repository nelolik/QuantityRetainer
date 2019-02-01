package com.example.stud.quantityretainer.Test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.stud.quantityretainer.R;
import com.example.stud.quantityretainer.Utilyties.RetainDBContract;
import com.example.stud.quantityretainer.Utilyties.RetainDBHelper;

public class ShowAllActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        mRecyclerView = findViewById(R.id.all_rec_recycler);

        RetainDBHelper dbHelper = new RetainDBHelper(this, "");
        mDb = dbHelper.getWritableDatabase();
        mCursor = getAllRecords();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShowAllRecyclerAdapter adapter =
                new ShowAllRecyclerAdapter(getApplicationContext(), mCursor);
        mRecyclerView.setAdapter(adapter);

    }

    private Cursor getAllRecords() {
        try {
            return mDb.query(RetainDBContract.RetainEntity.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    RetainDBContract.RetainEntity._ID + " DESC"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
