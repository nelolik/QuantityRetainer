package com.nelolik.stud.quantityretainer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.nelolik.stud.quantityretainer.Utilyties.RetainDBContract;
import com.nelolik.stud.quantityretainer.Utilyties.RetentionsProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatisticFragment extends Fragment {


    private String mRetentionName;
    private String mRetentionKey;
    private RetentionsProvider mRetProvider;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;
    private GraphView mGraphView;
    private DataPoint[] mPoints;



    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRetentionName = getArguments().getString(RecordFragment.TAG_NAME);
            mRetentionKey = getArguments().getString(RecordFragment.TAG_TABLE);
            mRetProvider = new RetentionsProvider(getContext(), mRetentionKey);
        }
        mWorkingThread = new HandlerThread("RetentionThread");
        mWorkingThread.start();
        mDbThreadHandler = new Handler(mWorkingThread.getLooper());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        mGraphView = view.findViewById(R.id.statistic_graph);
//        mGraphView.getViewport().setYAxisBoundsManual(true);
        mGraphView.getViewport().calcCompleteRange();
        mGraphView.getViewport().setScalable(true);
        if (mRetProvider != null) {
            mDbThreadHandler.post(() -> {
                getDataToDisplay();
                Activity activity = getActivity();
                if (activity != null && mPoints != null && mPoints.length > 0) {
                    activity.runOnUiThread(() -> {
                        BarGraphSeries<DataPoint> series =
                                new BarGraphSeries<>(mPoints);
                        series.setSpacing(50);
                        mGraphView.addSeries(series);
                    });
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getDataToDisplay() {
        Cursor cursor = mRetProvider.getAllRecords();
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        HashMap<String, Integer> dataMap = new HashMap<String, Integer>(cursor.getCount());
        int columnCountIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_COUNT);
        int columnDateIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_DATE);
        int count = cursor.getInt(columnCountIndex);
        long timestamp = cursor.getLong(columnDateIndex);
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        String stringDay = simpleDateFormat.format(date);
        dataMap.put(stringDay, count);
        while (cursor.moveToNext()) {
            count = cursor.getInt(columnCountIndex);
            timestamp = cursor.getLong(columnDateIndex);
            date.setTime(timestamp);
            stringDay = simpleDateFormat.format(date);
            if (dataMap.containsKey(stringDay)) {
                dataMap.put(stringDay, count + dataMap.get(stringDay));
            } else {
                dataMap.put(stringDay, count);
            }
        }
        mPoints = new DataPoint[dataMap.size() + 1];
        int i = 0;
        Set<Map.Entry<String,Integer>> dataSet = dataMap.entrySet();
        for (Map.Entry<String, Integer> e:
             dataSet) {
            mPoints[i] = new DataPoint(i, e.getValue());
            ++i;
        }
        mPoints[i] = new DataPoint(i, 0);
    }

}
