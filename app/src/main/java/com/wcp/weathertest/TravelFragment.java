package com.wcp.weathertest;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Switch;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.wcp.adapter.TravelAdapter;
import com.wcp.data.CalendarData;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/21 0021.
 */

public class TravelFragment extends Fragment{
    Fragment mContext=this;

    private View view;

    //List
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CalendarData> data;
    private SwipeRefreshLayout mRefreshLayout;

    private Boolean[] selectShow={true,true,true,true};

    //Add
    private FloatingActionButton Add;

    //Menu
    Menu mMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.travel_fragment, null);
        //ToolBar
        Toolbar bar=(Toolbar)view.findViewById(R.id.toolbar2);
        //TravelFragment.this.getActivity().setSupportActionBar(bar);
        ((MainActivity)getActivity()).setSupportActionBar(bar);
        setHasOptionsMenu(true);
        init(view);


        return view;
    }

    public TravelFragment() {

    }

    private void init(View view){
        try {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.travel_scroll);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this.getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            /*
            String[][] data = {
                    {"2016-01-25", "2016-05-18", "2016-11-22"},
                    {"EAT", "Play", "Sleep"},
                    {"13:00", "6:00", "12:30"},
                    {"9:00", "5:30", "12:00"},
                    {"无", "111111", "阿三"}
            };
            */
            Date now=new Date(System.currentTimeMillis());
            data= DataSupport.select().order("Date asc").find(CalendarData.class);
            CalendarDay today=new CalendarDay(new Date());
            for(int i=0;i<data.size();i++){
                CalendarDay it=new CalendarDay(data.get(i).getDate());
                if(it.isBefore(today)){
                    data.remove(i);
                    i--;
                }
            }
            mAdapter = new TravelAdapter(data,view.getContext());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.layout_swipe_refresh);
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    /*
                    data.clear();
                    data.addAll(DataSupport.select().order("Date asc").find(CalendarData.class));
                    CalendarDay today=new CalendarDay(new Date());
                    for(int i=0;i<data.size();i++){
                        CalendarDay it=new CalendarDay(data.get(i).getDate());
                        if(it.isBefore(today)){
                            data.remove(i);
                            i--;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    */
                    SelectShow(selectShow[0],selectShow[1],selectShow[2],selectShow[3]);
                    mRefreshLayout.setRefreshing(false);
                }
            });

            mRecyclerView.scrollToPosition(0);

        }catch(Exception e){
            Log.d("TAG","RecyclerView and Cards error");
            e.printStackTrace();
        }

        //Add new travel_event
        Add=(FloatingActionButton)view.findViewById(R.id.floatingAddButton);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewTravel=new Intent(v.getContext(),AddTravel.class);
                startActivity(addNewTravel);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Date now=new Date(System.currentTimeMillis());
        Log.d("TAG",now.toString());
        /*
        data.clear();
        data.addAll(DataSupport.select().order("Date asc").find(CalendarData.class));
        CalendarDay today=new CalendarDay(new Date());
        for(int i=0;i<data.size();i++){
            CalendarDay it=new CalendarDay(data.get(i).getDate());
            if(it.isBefore(today)){
                data.remove(i);
                i--;
            }
        }
        mAdapter.notifyDataSetChanged();
        */
        SelectShow(selectShow[0],selectShow[1],selectShow[2],selectShow[3]);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu=menu;
        menu.clear();
        TravelFragment.this.getActivity().getMenuInflater().inflate(R.menu.travel_select, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int[] ID={R.id.nav_home,R.id.nav_work,R.id.nav_holiday,R.id.nav_warn};
        switch(item.getItemId()){
            case R.id.nav_home:{
                if(item.isChecked()){
                    item.setChecked(false);
                    //取消选择home
                    selectShow[0]=false;
                }else{
                    item.setChecked(true);
                    //选中home
                    selectShow[0]=true;
                }
                break;
            }
            case R.id.nav_work:{
                if(item.isChecked()){
                    item.setChecked(false);
                    selectShow[1]=false;
                }else{
                    item.setChecked(true);
                    selectShow[1]=true;
                }
                break;
            }
            case R.id.nav_holiday:{
                if(item.isChecked()){
                    item.setChecked(false);
                    selectShow[2]=false;
                }else{
                    item.setChecked(true);
                    selectShow[2]=true;
                }
                break;
            }
            case R.id.nav_warn:{
                if(item.isChecked()){
                    item.setChecked(false);
                    selectShow[3]=false;
                }else{
                    item.setChecked(true);
                    selectShow[3]=true;
                }
                break;
            }
            case R.id.nav_all:{
                for(int i=0;i<4;i++){
                    selectShow[i]=true;
                    mMenu.findItem(ID[i]).setChecked(true);
                }
            }
        }
        SelectShow(selectShow[0],selectShow[1],selectShow[2],selectShow[3]);
        return super.onOptionsItemSelected(item);
    }

    public void SelectShow(Boolean cond1,Boolean cond2,Boolean cond3,Boolean cond4){
        data.clear();
        data.addAll(DataSupport.select().order("Date asc").find(CalendarData.class));
        CalendarDay today=new CalendarDay(new Date());
        for(int i=0;i<data.size();i++){
            CalendarDay it=new CalendarDay(data.get(i).getDate());
            if(it.isBefore(today)){
                data.remove(i);
                i--;
                continue;
            }
            if(!cond1&&data.get(i).getBelong().equals("home")
                    || !cond2&&data.get(i).getBelong().equals("work")
                    || !cond3&&data.get(i).getBelong().equals("holiday")
                    || !cond4&&data.get(i).getBelong().equals("warn")){
                data.remove(i);
                i--;
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
