package com.wcp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wcp.data.CalendarData;
import com.wcp.weathertest.DetailsAndDel;
import com.wcp.weathertest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/21 0021.
 */

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.ViewHolder> {
/*
    private String[] travel_datetime;
    private String[] travel_title;
    private String[] travel_begintime;
    private String[] travel_remind;
    private String[] travel_invite;
*/
    private CalendarData[] dataSet;
    private List<CalendarData> data_s;

    Context mContext;

    SimpleDateFormat fm=new SimpleDateFormat("yyyy年MM月dd日");
    SimpleDateFormat fn=new SimpleDateFormat("hh:mm");

    ArrayAdapter<String> adapter;
    List<String> contactsList = new ArrayList<>();
    String selectInviter;
    String selectInviterNumber;
    String phone="";
    String text="";


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView travel_datetime;
        public TextView travel_title;
        public TextView travel_begintime;
        public TextView travel_remind;
        public TextView travel_invite;
        public ImageView travel_color;
        View view;

        public ViewHolder(View v) {
            super(v);
            //TODO:Your Code Here
            view=v;
            travel_datetime=(TextView) v.findViewById(R.id.travel_date_time);
            travel_title=(TextView) v.findViewById(R.id.travel_title);
            travel_begintime=(TextView) v.findViewById(R.id.travel_begintime);
            travel_remind=(TextView) v.findViewById(R.id.travel_remind);
            travel_invite=(TextView) v.findViewById(R.id.travel_invite);
            travel_color=(ImageView)v.findViewById(R.id.travel_belong_color);
        }
    }
    public TravelAdapter(List<CalendarData> d,Context context) {
        this.mContext=context;

        this.data_s=d;
        //dataSet=new CalendarData[d.size()];
        //d.toArray(dataSet);
    }

    @Override
    public TravelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.travel_card, parent, false);

        final TravelAdapter.ViewHolder vh = new TravelAdapter.ViewHolder(v);
        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:

                int pos = vh.getAdapterPosition();
                CalendarData Can = data_s.get(pos);
                Intent intent = new Intent(mContext, DetailsAndDel.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("id", Can.getId() + "");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        vh.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final AlertDialog share;
                final AlertDialog.Builder shareDialog = new AlertDialog.Builder(mContext);
                shareDialog.setTitle("分享日程给");

                int pos = vh.getAdapterPosition();
                CalendarData Can = data_s.get(pos);
                text=fm.format(Can.getDate())+"日程:"+Can.getName()+",邀请"+Can.getInvite()+"。";
                if(Can.getAllDay()){
                    text+="日程安排在全天进行";
                }else{
                    text+="日程安排在"+fn.format(Can.getDate())+"到"+fn.format(Can.getEndDate())+"。";
                }

                try {
                    adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, contactsList);
                    //contactsView.setAdapter(adapter);
                    readContacts();
                }catch(Exception e){
                    e.printStackTrace();
                }

                shareDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectInviter=contactsList.get(which).split("\n")[0];
                        selectInviterNumber=contactsList.get(which).split("\n")[1];

                        SmsManager manager = SmsManager.getDefault();
                /*
                ArrayList<String> list = manager.divideMessage(text);  //因为一条短信有字数限制，因此要将长短信拆分
                for(String t:list){
                    manager.sendTextMessage(selectInviterNumber, null, t, null, null);
                }
                */
                        manager.sendTextMessage(selectInviterNumber, null, text, null, null);
                        Toast.makeText(mContext, "已发送给"+selectInviter, Toast.LENGTH_SHORT).show();
                    }
                });

                shareDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                share=shareDialog.show();
                return false;
            }
        });
        return vh;
    }
    @Override
    public void onBindViewHolder(TravelAdapter.ViewHolder holder, int position) {

        //TODO:your code here
        String begin;
        try {
            dataSet=new CalendarData[data_s.size()];
            data_s.toArray(dataSet);


            Log.d("TAG",data_s.get(position).toString());

            if(dataSet[position].getDate()!=null) {
                begin = fm.format(dataSet[position].getDate());
            }else{
                begin="null";
            }

            String begin_time;
            if(dataSet[position].getAllDay()==true){
                begin_time="全天";
            }else if(dataSet[position].getDate()==null) {
                begin_time="null";
            }else
            {
                begin_time=fn.format(dataSet[position].getDate());
            }

            String remind_time;
            String invite_p;
            if(dataSet[position].getRemind()!=null && dataSet[position].getRemind().size()>0) {
                remind_time = fn.format(new Date(dataSet[position].getRemind().get(0)));
            }else{
                remind_time="无";
            }

            if(dataSet[position].getInvite()==null){
                invite_p="无";
            }else{
                invite_p=dataSet[position].getInvite();
            }

            int color=R.color.white;
            switch (dataSet[position].getBelong()){
                case "home":{
                    color=R.color.home;break;
                }
                case "work":{
                    color=R.color.work;break;
                }
                case "holiday":{
                    color=R.color.holiday;break;
                }
                case "warn":{
                    color=R.color.warn;
                }
            }

            holder.travel_datetime.setText(begin);
            holder.travel_title.setText(dataSet[position].getName());
            holder.travel_begintime.setText(begin_time);
            holder.travel_remind.setText(remind_time);
            holder.travel_invite.setText(invite_p);
            holder.travel_color.setBackgroundColor(mContext.getResources().getColor(color));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.data_s.size();
    }

    private void readContacts(){
        Cursor cursor=null;
        try{
            cursor=mContext.getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI,null,null,null,null);
            if(cursor!=null){
                while(cursor.moveToNext()){
                    String displayName=cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number=cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactsList.add(displayName+"\n"+number);
                }
                adapter.notifyDataSetChanged();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }


}
