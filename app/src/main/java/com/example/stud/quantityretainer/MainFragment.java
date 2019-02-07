package com.example.stud.quantityretainer;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.stud.quantityretainer.Dialogs.AddRetentionDialog;
import com.example.stud.quantityretainer.Dialogs.DeleteConfirmationDialog;
import com.example.stud.quantityretainer.Dialogs.RenameRetentionDialog;
import com.example.stud.quantityretainer.Utilyties.RetainDBContract;
import com.example.stud.quantityretainer.Utilyties.RetainDBHelper;
import com.example.stud.quantityretainer.Utilyties.RetentionsNamesDBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        MainRecyclerAdapter.ListItemClickListener,
        AddRetentionDialog.AddRetentionOnCLick {

    public static String BACKSTACK_TAG = "backstak_tag";

    private SQLiteDatabase mDb;
    private RecyclerView mTopicsRecyclerView;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;
    private Cursor mCursor;
    private MainRecyclerAdapter mMainRecyclerAdapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWorkingThread.quit();
    }

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mWorkingThread = new HandlerThread("BackgroundThread");
        mWorkingThread.start();
        mDbThreadHandler = new Handler(mWorkingThread.getLooper());

        mTopicsRecyclerView = view.findViewById(R.id.topics_recycler_view);

        mTopicsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                AddRetentionDialog dialog = new AddRetentionDialog();
                dialog.setAddRetentionOnCLick(MainFragment.this);
                dialog.show(fragmentManager, "retentions");
            }
        });

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RetentionsNamesDBHelper dbHelper = new RetentionsNamesDBHelper(getContext());
        mDb = dbHelper.getWritableDatabase();
        getAllRetentionsCursor();
        mMainRecyclerAdapter = new MainRecyclerAdapter(getContext(),
                this,
                mCursor);
        mTopicsRecyclerView.setAdapter(mMainRecyclerAdapter);

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
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onListItemClick(String retentionName, String tableName) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(RecordFragment.TAG_NAME, retentionName);
        bundle.putString(RecordFragment.TAG_TABLE, tableName);
        RecordFragment recordFragment = new RecordFragment();
        recordFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, recordFragment);
        fragmentTransaction.addToBackStack(BACKSTACK_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onLongListItemClick(final TextView view, final String tableName) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
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

    private void getAllRetentionsCursor() {
        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCursor = mDb.query(RetainDBContract.Retentions.TABLE_NAME,
                            null,
                            null,
                            null,
                            null,
                            null,
                            RetainDBContract.Retentions._ID);
                    mMainRecyclerAdapter.setCursor(mCursor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMainRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
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

    private void addRetentionToDB(final String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
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
                getAllRetentionsCursor();
            }
        });
    }

    private void renameRetention(TextView view, final String tableName) {
        FragmentManager fragmentManager = getFragmentManager();
        RenameRetentionDialog dialog = new RenameRetentionDialog();
        dialog.setOldName(view.getText().toString());
        dialog.setOnClickListener(new RenameRetentionDialog.RenameRetentionONClickListener() {
            @Override
            public void onDialogClickRename(String newName) {
                writeNewRetentionNameToDB(newName, tableName);
            }
        });
        dialog.show(fragmentManager, "rename");
    }

    void writeNewRetentionNameToDB(final String newName, final String tableName) {
        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
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

                if ((cursor != null) && cursor.moveToFirst()) {
                    int columnIDIndex = cursor.getColumnIndex(RetainDBContract.Retentions._ID);
                    long ID = cursor.getLong(columnIDIndex);
                    ContentValues cv = new ContentValues();
                    cv.put(RetainDBContract.Retentions._ID, ID);
                    cv.put(RetainDBContract.Retentions.COLUMN_TABLE_NAME, tableName);
                    cv.put(RetainDBContract.Retentions.COLUMN_RETENTION_NAME, newName);
                    try {
                        mDb.beginTransaction();
                        mDb.replaceOrThrow(RetainDBContract.Retentions.TABLE_NAME,
                                null,
                                cv);
                        mDb.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mDb.endTransaction();
                    }
                    getAllRetentionsCursor();
                }
            }
        });
    }

    void deleteRetention(final String tableName) {
        FragmentManager manager = getFragmentManager();
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog();
        dialog.setOnDeleteClickListener(new DeleteConfirmationDialog.RenameRetentionONClickListener() {
            @Override
            public void onDialogClickDelete() {
                deleteRetentionFromDB(tableName);
            }
        });
        dialog.show(manager, "deleteDialog");
    }

    void deleteRetentionFromDB(final String tableName) {
        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mDb.beginTransactionWithListener(new SQLiteTransactionListener() {
                        @Override
                        public void onBegin() {

                        }

                        @Override
                        public void onCommit() {
                            getAllRetentionsCursor();
                        }

                        @Override
                        public void onRollback() {

                        }
                    });
//                    mDb.beginTransaction();
                    mDb.delete(RetainDBContract.Retentions.TABLE_NAME,
                            RetainDBContract.Retentions.COLUMN_TABLE_NAME + "=?",
                            new String[] {tableName});
                    mDb.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDb.endTransaction();
                }
                SQLiteDatabase retentionsDb = null;
                try {
                    retentionsDb = new RetainDBHelper(getContext()
                    ).getWritableDatabase();
                    retentionsDb.beginTransaction();
                    retentionsDb.delete(RetainDBContract.RetainEntity.TABLE_NAME,
                            RetainDBContract.RetainEntity.COLUMN_NAME + "=?",
                            new String[] {tableName});
                    retentionsDb.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    retentionsDb.endTransaction();
                    retentionsDb.close();
                }
            }
        });


    }


}
