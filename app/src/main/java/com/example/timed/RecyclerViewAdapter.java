package com.example.timed;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<String> timeData;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, List<String> data, List<String> appTimeData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.timeData = appTimeData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String appName = mData.get(position);
        //holder.iconApp.setImageResource();
        holder.name.setText(appName);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iconApp;
        TextView name;
        TextView appTime;
        LineChart appTimeChart;
        View verSeparator;
        ImageView iconTimer;
        TextView timer;

        ViewHolder(View itemView) {
            super(itemView);
            iconApp = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.appName);
            appTime = itemView.findViewById(R.id.appTime);
            name = itemView.findViewById(R.id.appName);
            appTimeChart = itemView.findViewById(R.id.appTimeChart);
            verSeparator = itemView.findViewById(R.id.verSeparator);
            iconTimer = itemView.findViewById(R.id.iconTimer);
            timer = itemView.findViewById(R.id.timer);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
