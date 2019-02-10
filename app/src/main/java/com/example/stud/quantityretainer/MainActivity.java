package com.example.stud.quantityretainer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.stud.quantityretainer.Test.ShowAllActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private RecyclerView mTopicsRecyclerView;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;
    private Cursor mCursor;
    private MainRecyclerAdapter mMainRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new MainFragment());
        fragmentTransaction.commit();
    }

    public void onMenuShowAllRetentions(MenuItem item) {
        Intent intent = new Intent(this, ShowAllActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
            return true;
        } else {
            return super.onSupportNavigateUp();
        }
    }

}
