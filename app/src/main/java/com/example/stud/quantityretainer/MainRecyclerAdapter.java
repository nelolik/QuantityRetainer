package com.example.stud.quantityretainer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.TopicViewHolder> {
    static String topics[] = {"one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine"};

    Context mContext;

    public MainRecyclerAdapter(Context context) {
        mContext = context;
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
        if (position < 0 || position >= topics.length) {
            return;
        }

        holder.mTopicTextView.setText(topics[position]);
    }


    @Override
    public int getItemCount() {
        return topics.length;
    }


    public class TopicViewHolder extends RecyclerView.ViewHolder {

        TextView mTopicTextView;

        public TopicViewHolder(View itemView) {
            super(itemView);
            mTopicTextView = itemView.findViewById(R.id.topic_tv);
        }
    }
}
