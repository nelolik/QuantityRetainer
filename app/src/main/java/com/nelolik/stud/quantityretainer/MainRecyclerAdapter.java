package com.nelolik.stud.quantityretainer;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nelolik.stud.quantityretainer.Utilyties.RetainDBContract;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.TopicViewHolder> {

    private Context mContext;

    private Cursor mCursor;

    final private ListItemClickListener mItemClickListener;

    public MainRecyclerAdapter(Context context, ListItemClickListener listener, Cursor cursor) {

        mContext = context;
        mItemClickListener = listener;
        mCursor = cursor;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.topic_view, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position) || mCursor == null) {
            return;
        }
        int columnIndex = mCursor.getColumnIndex(RetainDBContract.Retentions.COLUMN_RETENTION_NAME);
        String retentionName = mCursor.getString(columnIndex);
        holder.mTopicTextView.setText(retentionName);
        columnIndex = mCursor.getColumnIndex(RetainDBContract.Retentions.COLUMN_TABLE_NAME);
        holder.mTableName = mCursor.getString(columnIndex);
    }


    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }


    public class TopicViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {

        TextView mTopicTextView;
        String mTableName;

        public TopicViewHolder(View itemView) {
            super(itemView);
            mTopicTextView = itemView.findViewById(R.id.topic_tv);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mItemClickListener.onListItemClick(mTopicTextView.getText().toString(), mTableName);
        }

        @Override
        public boolean onLongClick(View v) {
            return mItemClickListener.onLongListItemClick(this.mTopicTextView, mTableName);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(String retentionName, String tableName);
        boolean onLongListItemClick(TextView view, String tableName);
    }
}
