package com.example.stud.quantityretainer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stud.quantityretainer.Dialogs.AddRetentionDialog;
import com.example.stud.quantityretainer.Dialogs.DeleteConfirmationDialog;
import com.example.stud.quantityretainer.Dialogs.RenameRetentionDialog;
import com.example.stud.quantityretainer.Test.ShowAllActivity;
import com.example.stud.quantityretainer.Utilyties.RetainDBContract;
import com.example.stud.quantityretainer.Utilyties.RetainDBHelper;
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
        Cursor cursor = getAllRetentions();
//        if (cursor.getCount() == 0) {
//            RecordsProvider.writeFakeRetentionsNames(mDb);
//        }
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

    @Override
    public boolean onLongListItemClick(final TextView view, final String tableName) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.main_list_item_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_rename:
                        renameRetention(view, tableName);
                        break;
                    case R.id.menu_delete:
                        deleteRetention(tableName);
                        break;
                }

                return false;
            }
        });
        popupMenu.show();
        return true;
    }

    private Cursor getAllRetentions() {
        try {
            return mDb.query(RetainDBContract.Retentions.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    RetainDBContract.Retentions._ID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDialogClickAdd(DialogFragment dialogFragment) {
        AddRetentionDialog dialog = (AddRetentionDialog) dialogFragment;
        String newName = dialog.getNewRetentionName();
        addRetentionToDB(newName);
    }

    @Override
    public void onDialogClickCancel(DialogFragment dialogFragment) {
//        Snackbar.make(findViewById(R.id.fab), "Button cancel pushed", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
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

    private void renameRetention(TextView view, final String tableName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RenameRetentionDialog dialog = new RenameRetentionDialog();
        dialog.setOnClickListener(new RenameRetentionDialog.RenameRetentionONClickListener() {
            @Override
            public void onDialogClickRename(String newName) {
                writeNewRetentionNameToDB(newName, tableName);
            }
        });
        dialog.show(fragmentManager, "rename");
    }

    void writeNewRetentionNameToDB(String newName, String tableName) {
        Cursor cursor = null;
        try {
            cursor = mDb.query(RetainDBContract.Retentions.TABLE_NAME,
                    null,
                    RetainDBContract.Retentions.COLUMN_TABLE_NAME + "=?",
                    new String[] {tableName},
                    null,
                    null,
                    RetainDBContract.Retentions._ID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (cursor.moveToFirst()) {
            int columnIDIndex = cursor.getColumnIndex(RetainDBContract.Retentions._ID);
            long ID = cursor.getLong(columnIDIndex);
            long retId = 0;
            ContentValues cv = new ContentValues();
            cv.put(RetainDBContract.Retentions._ID, ID);
            cv.put(RetainDBContract.Retentions.COLUMN_TABLE_NAME, tableName);
            cv.put(RetainDBContract.Retentions.COLUMN_RETENTION_NAME, newName);
            try {
                mDb.beginTransaction();
                retId = mDb.replaceOrThrow(RetainDBContract.Retentions.TABLE_NAME,
                        null,
                        cv);
                mDb.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                mDb.endTransaction();
            }
            cursor = getAllRetentions();
            MainRecyclerAdapter adapter = new MainRecyclerAdapter(this, this, cursor);
            mTopicsRecyclerView.setAdapter(adapter);
        }
    }

    void deleteRetention(final String tableName) {
        FragmentManager manager = getSupportFragmentManager();
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog();
        dialog.setOnDeleteClickListener(new DeleteConfirmationDialog.RenameRetentionONClickListener() {
            @Override
            public void onDialogClickDelete() {

            }
        });
        dialog.show(manager, "deleteDialog");
    }

    void deleteRetentionFromDB(String tableName) {
        try {
            mDb.beginTransaction();
            mDb.delete(RetainDBContract.Retentions.TABLE_NAME,
                    RetainDBContract.Retentions.TABLE_NAME + "=?",
                    new String[] {tableName});
            mDb.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.setTransactionSuccessful();
        }
        SQLiteDatabase retentionsDb = null;
        try {
            retentionsDb = new RetainDBHelper(this,
                    RetainDBContract.RetainEntity.TABLE_NAME).getWritableDatabase();
            retentionsDb.beginTransaction();
            retentionsDb.delete(RetainDBContract.RetainEntity.TABLE_NAME,
                    RetainDBContract.RetainEntity.TABLE_NAME + "=?",
                    new String[] {tableName});
            retentionsDb.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retentionsDb.setTransactionSuccessful();
            retentionsDb.close();
        }

    }

    public void onMenuShowAllRetentions(MenuItem item) {
        Toast.makeText(this, "On menu", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ShowAllActivity.class);
        startActivity(intent);
    }

}
