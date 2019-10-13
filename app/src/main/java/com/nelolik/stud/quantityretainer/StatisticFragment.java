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
import android.widget.RadioGroup;

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
    private RadioGroup mIntervalRadioGroup;
    private boolean intervalDay, intervalMonth;


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
        intervalDay = true;
        intervalMonth = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        mIntervalRadioGroup = view.findViewById(R.id.statistic_interval);
        mIntervalRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.statistic_days) {
                    intervalDay = true;
                    intervalMonth = false;
                }
                if (checkedId == R.id.statistic_months) {
                    intervalDay = false;
                    intervalMonth = true;
                }
                if (mRetProvider != null) {
                    mDbThreadHandler.post(() -> {
                        getDataToDisplay();
                        BarData data = prepareBarData();

                        Activity activity = getActivity();
                        if (activity != null && data != null) {
                            activity.runOnUiThread(() -> {
                                mChart.setData(data);
                                mChart.moveViewToX(mChart.getXChartMax());
                                mChart.setVisibleXRangeMaximum(7);
                                mChart.setVisibleXRangeMinimum(7);
                                mChart.animateY(100);
                                mChart.invalidate();

                            });
                        }
                    });
                }
            }
        });
        mChart = view.findViewById(R.id.chart);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.getXAxis().setGranularity(1f);
        mChart.getXAxis().setTextSize(16.0f);
        mChart.getXAxis().setLabelRotationAngle(90);
        mChart.getXAxis().setValueFormatter(new ValueFormatter() {
            SimpleDateFormat formater = new SimpleDateFormat("dd MMMM");

            @Override
            public String getFormattedValue(float value) {
                long t = 0;
                if (intervalDay) {
                    formater.applyPattern("dd MMMM");
                    t = (long)value * 1000 * 86400;
                }
                if (intervalMonth) {
                    formater.applyPattern("LLLL YYYY");
                    int year = ((int)value) / 12;
                    int month = ((int)value) % 12;
                    Calendar c = Calendar.getInstance();
                    c.set(year, month,1);
                    t = c.getTimeInMillis();
                }

                String lbl = formater.format(t);
                return lbl;
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
                BarData data = prepareBarData();

                Activity activity = getActivity();
                if (activity != null && data != null) {
                    activity.runOnUiThread(() -> {
                        mChart.setData(data);
                        mChart.moveViewToX(mChart.getXChartMax());
                        mChart.setVisibleXRangeMaximum(7);
                        mChart.setVisibleXRangeMinimum(7);
//                        mChart.animateY(100);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("");
        int columnCountIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_COUNT);
        int columnDateIndex = cursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_DATE);
        int count;// = cursor.getInt(columnCountIndex);
        long timestamp;// = cursor.getLong(columnDateIndex);
        cursor.moveToPosition(-1);
        Calendar calendar = Calendar.getInstance();
        while (cursor.moveToNext()) {
            count = cursor.getInt(columnCountIndex);
            timestamp = cursor.getLong(columnDateIndex);
            long key;
            if (intervalDay) {
                key = timestamp / 1000 / 86400;    //86400 - seconds in day
            } else {
                calendar.setTimeInMillis(timestamp);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                key = year * 12 + month;
            }

            if (dataMap.containsKey(key)) {
                dataMap.put(key, count + dataMap.get(key));
            } else {
                dataMap.put(key, count);
            }
        }

        mPointsList = mapToSortedList(dataMap);
        addDataIfListShort();
        return mPointsList;
    }

    private BarData prepareBarData() {
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
        return data;
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
        ArrayList<BarEntry> sortedData = new ArrayList<>();
        for (Long k : sortedKeys) {
            sortedData.add(new BarEntry(k, data.get(k)));
        }
        return sortedData;
    }

    private void addDataIfListShort() {
        if (mPointsList.size() > 0 && mPointsList.size() < 8) {
            BarEntry be = mPointsList.get(0);
            long x = (long)be.getX();
            while (mPointsList.size() < 8) {
                mPointsList.add(new BarEntry(--x, 0));
            }
        }
    }

}
