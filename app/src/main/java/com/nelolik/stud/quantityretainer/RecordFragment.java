package com.nelolik.stud.quantityretainer;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nelolik.stud.quantityretainer.Utilyties.RetainDBContract;
import com.nelolik.stud.quantityretainer.Utilyties.RetentionsProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends android.support.v4.app.Fragment {
    public static final String TAG_NAME = "RETENTION_NAME";
    public static final String TAG_TABLE = "TABLE_NAME";
    private static final String PREF_INCREMENT_SIZE = "pref_increment_size";
    private static final String INCREMENT_CHANNEL = "increment";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE = 0;

    private static RecordFragment instance = null;

    private TextView mTotalCount;
    private EditText mAddCount;
    private Button mAddButton;
    private NestedScrollView mBottomSheet;
    private EditText mAddOnTap;
    private TextView mTapField;
    private RecyclerView mCountRecycler;
    private RetentionRecyclerAdapter mAdapter;
    private String mRetentionName;
    private String mCountName;
    private RetentionsProvider mRetProvider;
    private Cursor mCursor;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mWorkingThread = new HandlerThread("RetentionThread");
        mWorkingThread.start();
        mDbThreadHandler = new Handler(mWorkingThread.getLooper());

        mTotalCount = view.findViewById(R.id.total_count);
        mAddCount = view.findViewById(R.id.add_count_input);
        mAddButton = view.findViewById(R.id.btn_add);
        mCountRecycler = view.findViewById(R.id.count_recycler);
        mBottomSheet = view.findViewById(R.id.bottom_sheet);
        mAddOnTap = view.findViewById(R.id.increment_count_input);
        mTapField = view.findViewById(R.id.tap_field);
        mTapField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnTap();
                showNotification();
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

        mRetProvider = new RetentionsProvider(getContext(), mCountName);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCount();
                mTotalCount.setText(String.valueOf(getTotalCount(mCursor)));
                mAddCount.selectAll();
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mRetentionName);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        int increment_size = ((AppCompatActivity)getActivity()).getPreferences(Context.MODE_PRIVATE)
                .getInt(PREF_INCREMENT_SIZE, 0);
        mAddOnTap.setText(String.valueOf(increment_size));
        if (mCountName != null) {
            int previousAddCount = ((AppCompatActivity) getActivity()).getPreferences(Context.MODE_PRIVATE)
                    .getInt(mCountName, 0);
            mAddCount.setText(String.valueOf(previousAddCount));
        } else {
            mAddCount.setText("0");
        }
        setHasOptionsMenu(true);
        instance = this;
        return view;
    }

    private void addOnTap() {
        String prevAddStr = mAddCount.getText().toString();
        String addOnTap = mAddOnTap.getText().toString();
        if (prevAddStr.isEmpty()) { prevAddStr = "0"; }
        if (addOnTap.isEmpty()) { addOnTap = "0"; }
        int add =  Integer.parseInt(addOnTap);
        int prevAdd = Integer.parseInt(prevAddStr);
        mAddCount.setText(String.valueOf(add + prevAdd));
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
        mWorkingThread.quit();
        cancelNotification();
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        showNotification();
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            SharedPreferences.Editor editor = activity.getPreferences(Context.MODE_PRIVATE).edit();
            editor.putInt(PREF_INCREMENT_SIZE, Integer.parseInt(mAddOnTap.getText().toString()));
            if (mCountName != null) {
                editor.putInt(mCountName, Integer.parseInt(mAddCount.getText().toString()));
            }
            editor.apply();
        }
    }

    private void getAllRecordsAndShow() {
        mDbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mCursor = mRetProvider.getAllRecords();
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTotalCount.setText(String.valueOf(getTotalCount(mCursor)));
                            mAdapter.setCursor(mCursor);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
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
                mRetProvider.addNewCountToDB(newCount);
                getAllRecordsAndShow();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            if (mAddCount.hasFocus()) {
                mBottomSheet.scrollTo((int)getResources().getDimension(R.dimen.sheet_peek_height),
                        0);
            }
        }

        super.onConfigurationChanged(newConfig);
    }

    private void createMessagesNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context
                    .getString(R.string.inсrement_channel_name);
            NotificationChannel channel = new NotificationChannel(
                    INCREMENT_CHANNEL,
                    name,
                    NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        createMessagesNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                INCREMENT_CHANNEL);
        String title = getString(R.string.notification_title) + " " + mAddCount.getText().toString();
        String text = getString(R.string.enter_add_on_tap) + " " + mAddOnTap.getText().toString();

        Intent addAction = new Intent(context,
                RecordFragment.NotificationActionReceiver.class);
        addAction.setAction(NotificationActionReceiver.ADD_PRESSED_ACTION);
        PendingIntent addIntent = PendingIntent.getBroadcast(context,
                REQUEST_CODE, addAction, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.mala_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(NotificationCompat.VISIBILITY_PUBLIC)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setColor(getResources().getColor(R.color.primaryColor))
                .addAction(new NotificationCompat.Action.Builder(
                        R.drawable.ic_add_box_black_24dp,
                        "Add",
                        addIntent).build())
        ;

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getContext());
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getContext());
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private static RecordFragment getInstance() {
        return instance;
    }

    public static class NotificationActionReceiver extends BroadcastReceiver {
        static final String ADD_PRESSED_ACTION =
                "com.nelolik.stud.quantityretainer.ADD_PRESSED_ACTION";

        public NotificationActionReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            RecordFragment fragment = RecordFragment.getInstance();
            if (fragment == null) {
                return;
            }
            fragment.addOnTap();
            fragment.showNotification();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_record_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_statistic) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString(RecordFragment.TAG_NAME, mRetentionName);
                bundle.putString(RecordFragment.TAG_TABLE, mCountName);
                StatisticFragment statisticFragment = new StatisticFragment();
                statisticFragment.setArguments(bundle);
                fragmentTransaction.setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit,
                        R.anim.anim_pop_enter, R.anim.anim_pop_exit);

                fragmentTransaction.replace(R.id.fragment_container, statisticFragment);
                fragmentTransaction.addToBackStack(MainFragment.BACKSTACK_TAG);
                fragmentTransaction.commit();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
