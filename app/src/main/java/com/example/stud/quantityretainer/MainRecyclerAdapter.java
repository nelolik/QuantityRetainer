package com.example.stud.quantityretainer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stud.quantityretainer.Utilyties.RecordsProvider;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.TopicViewHolder> {
//    static String topics[] = {"one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
//            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
//            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine"};

    Context mContext;

    private RecordsProvider recordsProvider;
    final private ListItemClickListener mItemClickListener;

    public MainRecyclerAdapter(Context context, ListItemClickListener listener) {

        mContext = context;
        recordsProvider = new RecordsProvider();
        mItemClickListener = listener;
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
        if (position < 0 || position >= recordsProvider.getRecordsCount()) {
            return;
        }

        holder.mTopicTextView.setText(recordsProvider.getRecord(position));
    }


    @Override
    public int getItemCount() {
        return recordsProvider.getRecordsCount();
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
