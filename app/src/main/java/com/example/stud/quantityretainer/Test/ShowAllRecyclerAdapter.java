package com.example.stud.quantityretainer.Test;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stud.quantityretainer.R;
import com.example.stud.quantityretainer.Utilyties.RetainDBContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowAllRecyclerAdapter extends RecyclerView.Adapter<ShowAllRecyclerAdapter.RecordViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public ShowAllRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.all_records_view, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            holder.mCount.setText("1");
            holder.mDate.setText("2");
            return;
        }
        int columnIndex = mCursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_COUNT);
        int count = mCursor.getInt(columnIndex);
        columnIndex = mCursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_DATE);
        long timestamp = mCursor.getLong(columnIndex);
        holder.mCount.setText(String.valueOf(count));
        Date  date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD MMM YYYY");
        holder.mDate.setText(simpleDateFormat.format(date));
        columnIndex = mCursor.getColumnIndex(RetainDBContract.RetainEntity.COLUMN_NAME);
        String name = mCursor.getString(columnIndex);
        holder.mTable.setText(name);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {

        TextView mCount;
        TextView mDate;
        TextView mTable;

        public RecordViewHolder(View itemView) {
            super(itemView);
            mCount = itemView.findViewById(R.id.retreat_count);
            mDate = itemView.findViewById(R.id.retreat_date);
            mTable = itemView.findViewById(R.id.retreat_table);
        }
    }
}
