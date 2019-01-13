package com.example.stud.quantityretainer;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stud.quantityretainer.Utilyties.RecordsProvider;
import com.example.stud.quantityretainer.Utilyties.RetainDBContract;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.TopicViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    private RecordsProvider recordsProvider;
    final private ListItemClickListener mItemClickListener;

    public MainRecyclerAdapter(Context context, ListItemClickListener listener, Cursor cursor) {

        mContext = context;
        recordsProvider = new RecordsProvider();
        mItemClickListener = listener;
        mCursor = cursor;
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
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTopicTextView;

        public TopicViewHolder(View itemView) {
            super(itemView);
            mTopicTextView = itemView.findViewById(R.id.topic_tv);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        public void onListItemClick(int clickedItemIndex);
    }
}
