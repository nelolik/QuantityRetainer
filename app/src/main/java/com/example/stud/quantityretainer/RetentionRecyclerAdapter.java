package com.example.stud.quantityretainer;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stud.quantityretainer.Utilyties.RetainDBContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RetentionRecyclerAdapter extends RecyclerView.Adapter<RetentionRecyclerAdapter.RecordViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public RetentionRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.record_view, parent, false);
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
        public RecordViewHolder(View itemView) {
            super(itemView);
            mCount = itemView.findViewById(R.id.retreat_count);
            mDate = itemView.findViewById(R.id.retreat_date);
        }
    }
}
