package com.wcp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wcp.data.CalendarData;
import com.wcp.data.CityData;
import com.wcp.weathertest.DetailsAndDel;
import com.wcp.weathertest.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/24 0024.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private List<CityData> mDataset;
    private Context mContext;
    private EditText mEditor;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mPro;
        public TextView mCity;
        private View view;

        public ViewHolder(View v) {
            super(v);
            //TODO:Your Code Here
            mPro=(TextView) v.findViewById(R.id.date_time);
            mCity=(TextView)v.findViewById(R.id.date_item);
            view=v;
        }
    }

    public CityAdapter(List<CityData> myDataset, EditText edit,Context context) {
        mDataset = myDataset;
        mContext=context;
        mEditor=edit;
    }

    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_card, parent, false);

        final CityAdapter.ViewHolder vh = new CityAdapter.ViewHolder(v);
        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=vh.getAdapterPosition();
                mEditor.setText(mDataset.get(pos).getCity());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(CityAdapter.ViewHolder holder, int position) {

        //TODO:your code here
        holder.mPro.setText(mDataset.get(position).getProvince());
        holder.mCity.setText(mDataset.get(position).getCity());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
