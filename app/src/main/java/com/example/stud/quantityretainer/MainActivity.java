package com.example.stud.quantityretainer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.stud.quantityretainer.Utilyties.RecordsProvider;
import com.example.stud.quantityretainer.Utilyties.RetainDBContract;
import com.example.stud.quantityretainer.Utilyties.RetentionsNamesDBHelper;

public class MainActivity extends AppCompatActivity implements
        MainRecyclerAdapter.ListItemClickListener,
        AddRetentionDialog.AddRetentionOnCLick {

    private SQLiteDatabase mDb;
    private RecyclerView mTopicsRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mProgressBar = findViewById(R.id.main_progress_bar);
        mTopicsRecyclerView = findViewById(R.id.topics_recycler_view);

        mTopicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RetentionsNamesDBHelper dbHelper = new RetentionsNamesDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor =getAllRetentions();
        if (cursor.getCount() == 0) {
            RecordsProvider.writeFakeRetentionsNames(mDb);
        }
        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(this,
                this,
                cursor);


        mTopicsRecyclerView.setAdapter(mainRecyclerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddRetentionDialog dialog = new AddRetentionDialog();
                dialog.setAddRetentionOnCLick(MainActivity.this);
                dialog.show(fragmentManager, "retentions");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(String retentionName, String tableName) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra(RecordActivity.TAG_NAME, retentionName);
        intent.putExtra(RecordActivity.TAG_TABLE, tableName);
        startActivity(intent);
    }

    private Cursor getAllRetentions() {
        return mDb.query(RetainDBContract.Retentions.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                RetainDBContract.Retentions._ID);
    }

    @Override
    public void onDialogClickAdd(DialogFragment dialogFragment) {
        Snackbar.make(findViewById(R.id.fab), "Button add pushed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        AddRetentionDialog dialog = (AddRetentionDialog) dialogFragment;
        String newName = dialog.getNewRetentionName();
        addRetentionToDB(newName);
    }

    @Override
    public void onDialogClickCancel(DialogFragment dialogFragment) {
        Snackbar.make(findViewById(R.id.fab), "Button cancel pushed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void addRetentionToDB(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        try {
            mDb.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(RetainDBContract.Retentions.COLUMN_RETENTION_NAME, name);
            long id = mDb.insert(RetainDBContract.Retentions.TABLE_NAME, null, cv);
            if (id != -1) {
                String tableName = "table" + id;
                cv.put(RetainDBContract.Retentions._ID, id);
                cv.put(RetainDBContract.Retentions.COLUMN_TABLE_NAME, tableName);
                mDb.replaceOrThrow(RetainDBContract.Retentions.TABLE_NAME, null, cv);
                mDb.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            mDb.endTransaction();
        }

        Cursor cursor = getAllRetentions();
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(this, this, cursor);
        mTopicsRecyclerView.setAdapter(adapter);
    }

}
