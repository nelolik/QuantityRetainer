package com.example.stud.quantityretainer;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stud.quantityretainer.Utilyties.RetainDBContract;
import com.example.stud.quantityretainer.Utilyties.RetainDBHelper;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends android.support.v4.app.Fragment {
    public static final String TAG_NAME = "RETENTION_NAME";
    public static final String TAG_TABLE = "TABLE_NAME";

    TextView mTotalText;
    TextView mTotalCount;
    private EditText mAddCount;
    private Button mAddButton;
    private EditText mAddOnTap;
    private TextView mTapField;
    private RecyclerView mCountRecycler;
    private RetentionRecyclerAdapter mAdapter;
    private String mRetentionName;
    private String mCountName;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetainDBHelper dbHelper = new RetainDBHelper(getContext());
        mDb = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mWorkingThread = new HandlerThread("RetentionThread");
        mWorkingThread.start();
        mDbThreadHandler = new Handler(mWorkingThread.getLooper());

        mTotalText = view.findViewById(R.id.total_text);
        mTotalCount = view.findViewById(R.id.total_count);
        mAddCount = view.findViewById(R.id.add_count_input);
        mAddButton = view.findViewById(R.id.btn_add);
        mCountRecycler = view.findViewById(R.id.count_recycler);
        mAddOnTap = view.findViewById(R.id.increment_count_input);
        mTapField = view.findViewById(R.id.tap_field);
        mTapField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevAddStr = mAddCount.getText().toString();
                String addOnTap = mAddOnTap.getText().toString();
                if (prevAddStr.isEmpty()) { prevAddStr = "0"; }
                if (addOnTap.isEmpty()) { addOnTap = "0"; }
                int add =  Integer.parseInt(addOnTap);
                int prevAdd = Integer.parseInt(prevAddStr);
                mAddCount.setText(String.valueOf(add + prevAdd));
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(TAG_NAME)) {
                mRetentionName = getArguments().getString(TAG_NAME);
            }
            if (arguments.containsKey(TAG_TABLE)) {
                mCountName = getArguments().getString(TAG_TABLE);
            }
        }

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCount();
                mTotalCount.setText(String.valueOf(getTotalCount(mCursor)));
                mAddCount.selectAll();
            }
        });

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRetentionName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCountRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RetentionRecyclerAdapter(getContext(), mCursor);
        mCountRecycler.setAdapter(mAdapter);
        getAllRecordsAndShow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWorkingThread.quit();
    }

    private void getAllRecordsAndShow() {
        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                getAllRecords();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTotalCount.setText(String.valueOf(getTotalCount(mCursor)));
                        mAdapter.setCursor(mCursor);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void getAllRecords() {
        try {
            String selection = RetainDBContract.RetainEntity.COLUMN_NAME + "=?";
            mCursor = mDb.query(RetainDBContract.RetainEntity.TABLE_NAME,
                    null,
                    selection,
                    new String[]{mCountName},
                    null,
                    null,
                    RetainDBContract.RetainEntity._ID + " DESC"
            );
        } catch (Exception e) {
            e.printStackTrace();
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
        final int newCount = Integer.parseInt(mAddCount.getText().toString());

        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
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

                getAllRecordsAndShow();
            }
        });
    }
}
