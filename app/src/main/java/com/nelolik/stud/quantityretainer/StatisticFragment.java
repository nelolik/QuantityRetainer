package com.nelolik.stud.quantityretainer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nelolik.stud.quantityretainer.Utilyties.RetainDBContract;
import com.nelolik.stud.quantityretainer.Utilyties.RetentionsProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class StatisticFragment extends Fragment {


    private String mRetentionName;
    private String mRetentionKey;
    private RetentionsProvider mRetProvider;
    private HandlerThread mWorkingThread;
    private Handler mDbThreadHandler;
    private BarChart mChart;
    private ArrayList<BarEntry> mPointsList;
    private ArrayList<String> mLabels = new ArrayList<>();


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
        mChart = view.findViewById(R.id.chart);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setTextSize(16.0f);
        mChart.getXAxis().setLabelRotationAngle(90);
        mChart.getXAxis().setValueFormatter(new ValueFormatter() {
            SimpleDateFormat formater = new SimpleDateFormat("dd MMMM");

            @Override
            public String getFormattedValue(float value) {
                long timestamp = (long)(value * 1000 * 86400) + 43200000;   //43200000 - middle of a day
                return formater.format(timestamp);
            }
        });
        mChart.getAxisLeft().setAxisMinimum(0);
        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawZeroLine(true);
        mChart.getAxisRight().setAxisMinimum(0);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getLegend().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDragXEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setVisibleXRangeMaximum(7);

        if (mRetProvider != null) {
            mDbThreadHandler.post(() -> {
                getDataToDisplay();
                BarDataSet dataSet = new BarDataSet(mPointsList, "Retention");
                dataSet.setColor(Color.BLUE);
//                dataSet.setFormSize(30);
                dataSet.setValueTextSize(16);
                dataSet.setBarBorderColor(Color.BLUE);
                dataSet.setBarBorderWidth(3);
                BarData data = new BarData(dataSet);
                data.setBarWidth(0.8f);
                data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        if (value > 0) {
                            return String.valueOf((int)value);
                        }
                        return "";
                    }
                });

                Activity activity = getActivity();
                if (activity != null && dataSet != null) {
                    activity.runOnUiThread(() -> {
                        mChart.setData(data);
                        float lastIndex = mPointsList.get(0).getX();
                        mChart.moveViewToX(mChart.getXChartMax() - 7);
                        mChart.setVisibleXRangeMaximum(7);
                        mChart.invalidate();

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

    private ArrayList<BarEntry> getDataToDisplay() {
        Cursor cursor = mRetProvider.getAllRecordsASC();
        if (cursor == null || !cursor.moveToFirst()) {
            return new ArrayList<BarEntry>();
        }
        HashMap<Long, Integer> dataMap = new HashMap<Long, Integer>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
        int columnCountIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_COUNT);
        int columnDateIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_DATE);
        int count;// = cursor.getInt(columnCountIndex);
        long timestamp;// = cursor.getLong(columnDateIndex);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            count = cursor.getInt(columnCountIndex);
            timestamp = cursor.getLong(columnDateIndex);
            if (timestamp < 100000) {continue;}
            long day = timestamp / 1000 / 86400;    //86400 - seconds in day
            if (dataMap.containsKey(day)) {
                dataMap.put(day, count + dataMap.get(day));
            } else {
                dataMap.put(day, count);
            }
        }

        mPointsList = mapToSortedList(dataMap);
        addDataIfListShort();
        return mPointsList;
    }

    ArrayList<BarEntry> mapToSortedList(HashMap<Long, Integer> data) {
        Set<Long> keys = data.keySet();
        ArrayList<Long> sortedKeys = new ArrayList<Long>();
        for (long key : keys) {
            int position = 0;
            for (long k : sortedKeys) {
                if (k > key) break;
                position++;
            }
            sortedKeys.add(position, key);
        }
        ArrayList<BarEntry> sortedData = new ArrayList<BarEntry>();
        int i = 0;
        for (Long k : keys) {
            sortedData.add(new BarEntry(k, data.get(k)));
        }
        return sortedData;
    }

    private void addDataIfListShort() {
        if (mPointsList.size() > 0 && mPointsList.size() < 17) {
            BarEntry be = mPointsList.get(mPointsList.size() - 1);
            long day = (long)be.getX();
            while (mPointsList.size() < 17) {
                day -= 1;
                mPointsList.add(new BarEntry(day, 0));
            }
        }
    }

}
