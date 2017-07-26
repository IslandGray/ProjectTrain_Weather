package com.wcp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wcp.data.CalendarData;
import com.wcp.weathertest.DetailsAndDel;
import com.wcp.weathertest.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/20 0020.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<CalendarData> mDataset;
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTime;
        public TextView mItem;
        private View view;

        public ViewHolder(View v) {
            super(v);
            //TODO:Your Code Here
            mTime=(TextView) v.findViewById(R.id.date_time);
            mItem=(TextView)v.findViewById(R.id.date_item);
            view=v;
        }
    }

    public CalendarAdapter(List<CalendarData> myDataset, Context context) {
        mDataset = myDataset;
        mContext=context;
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_card, parent, false);

        final ViewHolder vh = new ViewHolder(v);
        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = vh.getAdapterPosition();
                CalendarData Can = mDataset.get(pos);
                Intent intent=new Intent(mContext, DetailsAndDel.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("id", Can.getId() + "");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //TODO:your code here
        try {
            int color = 0;
            SimpleDateFormat fn = new SimpleDateFormat("HH:mm");
            switch (mDataset.get(position).getBelong()) {
                case "home": {
                    color = R.color.home;
                    holder.mTime.setBackgroundColor(mContext.getResources().getColor(color));
                    holder.mTime.setTextColor(mContext.getResources().getColor(R.color.black));
                    break;
                }
                case "work": {
                    color = R.color.work;
                    holder.mTime.setBackgroundColor(mContext.getResources().getColor(color));
                    holder.mTime.setTextColor(mContext.getResources().getColor(R.color.white));
                    break;
                }
                case "holiday": {
                    color = R.color.holiday;
                    holder.mTime.setBackgroundColor(mContext.getResources().getColor(color));
                    holder.mTime.setTextColor(mContext.getResources().getColor(R.color.white));
                    break;
                }
                case "warn": {
                    color = R.color.warn;
                    holder.mTime.setBackgroundColor(mContext.getResources().getColor(color));
                    holder.mTime.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }
            if(mDataset.get(position).getAllDay()){
                holder.mTime.setText(" 全天 ");
            }else{
                holder.mTime.setText(fn.format(mDataset.get(position).getDate()));
            }
            holder.mItem.setText(mDataset.get(position).getName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
