package com.wcp.weathertest;

import android.app.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wcp.adapter.CalendarAdapter;
import com.wcp.adapter.CalendarDecorator;
import com.wcp.adapter.CalendarDecoratorWeekend;
import com.wcp.data.CalendarData;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/19 0019.
 */

public class CalendarFragment extends DialogFragment implements OnDateSelectedListener {
    DialogFragment mContext=this;
    Context mmContext;

    private View view;
    //Calendar
    MaterialCalendarView mcv;
    //接收从Activity传来的数据
    Bundle timeBundle;
    OnFrgDataListener IListener;

    List<CalendarData> myDataset=new ArrayList<>();
    Date selectingNow=new Date();
    CalendarDecorator PointDecorator;

    //List
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter CalendarAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.calendar_fregment, null);
        mmContext=CalendarFragment.this.getContext();

        mcv= (MaterialCalendarView) view.findViewById(R.id.mcv);
        timeBundle=getArguments();
        initData();

        //RecyclerView  +   Cards
        mRecyclerView=(RecyclerView)view.findViewById(R.id.calendar_scroll);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager=new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //String[] myDataset={"星期一","星期二","星期三"};
        CalendarDay now=new CalendarDay(new Date());
        List<CalendarData> all=DataSupport.select().order("Date asc").find(CalendarData.class);
        for(Object i:all){
            CalendarDay it=new CalendarDay(((CalendarData)i).getDate());
            if(it.equals(now)){
                myDataset.add((CalendarData) i);
            }
        }
        CalendarAdapter=new CalendarAdapter(myDataset,mmContext);
        mRecyclerView.setAdapter(CalendarAdapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
/*
        //下面这些都是为了让dialog宽度全充满
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable( new ColorDrawable(getResources().getColor(R.color.white)));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        WindowManager.LayoutParams params = win.getAttributes();
        // params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕

        params.width =  ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
*/
        /*
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 1), ViewGroup.LayoutParams.WRAP_CONTENT);
        }*/
        CalendarDay now=new CalendarDay(selectingNow);
        List<CalendarData> all=DataSupport.select().order("Date asc").find(CalendarData.class);
        myDataset.clear();
        for(Object i:all){
            CalendarDay it=new CalendarDay(((CalendarData)i).getDate());
            if(it.equals(now)){
                myDataset.add((CalendarData) i);
            }
        }
        CalendarAdapter.notifyDataSetChanged();

        mcv.removeDecorators();
        initPoint();
        initWeekend();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这个主题 背景全透明 没有半透明
        // setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    public void initData(){
        // 显示兴起补全的整个礼拜的上个月或者下个月的日期 一般会多出一行整个礼拜
        // 点击补全出来的另外一个月的信息 可以直接跳到那个月
        mcv.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        // 设置日历默认的时间为当前的时间
        mcv.setSelectedDate(new Date());

        // 日历的主要设置
        mcv.state().edit()
                // 设置你的日历 第一天是周一还是周一
                .setFirstDayOfWeek(Calendar.SUNDAY)
                // 设置你的日历的最小的月份  月份为你设置的最小月份的下个月 比如 你设置最小为1月 其实你只能滑到2月
                .setMinimumDate(CalendarDay.from(2015, 1, 1))
                // 同最小 设置最大
                .setMaximumDate(CalendarDay.from(2018, 12, 30))
                // 设置你的日历的模式  是按月 还是按周
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // 设置你选中日期的背景底色
        mcv.setSelectionColor(getResources().getColor(R.color.divider));
        //       mcv.setSelectionColor(0xff4285f4);


        //为事件加点
        initPoint();
        //周末
        initWeekend();

        mcv.setOnDateChangedListener(this);

    }

    public void initPoint(){
        String[] co={"home","work","holiday","warn"};
        int color;
        for(Object i:co){
            List<CalendarData> data= DataSupport.select().where("Belong = ?",((String)i)).find(CalendarData.class);
            switch(((String)i)){
                case "home":{
                    color=mContext.getResources().getColor(R.color.home);
                    break;
                }
                case "work":{
                    color=mContext.getResources().getColor(R.color.work);
                    break;
                }
                case "holiday":{
                    color=mContext.getResources().getColor(R.color.holiday);
                    break;
                }
                case "warn":{
                    color=mContext.getResources().getColor(R.color.warn);
                    break;
                }
                default:{
                    color=mContext.getResources().getColor(R.color.black);
                    break;
                }
            }
            PointDecorator=new CalendarDecorator(data,color);
            mcv.addDecorator(PointDecorator);
        }
    }

    private void initWeekend(){
        int color=mContext.getResources().getColor(R.color.colorAccent);
        mcv.addDecorator(new CalendarDecoratorWeekend(color));
    }

    /**
     * 日期选择 回调函数
     * @param widget
     * @param date
     * @param selected
     */
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        IListener.progress(mcv.getSelectedDate().getDate());

        selectingNow=mcv.getSelectedDate().getDate();

        CalendarDay now=new CalendarDay(selectingNow);
        List<CalendarData> all=DataSupport.select().order("Date asc").find(CalendarData.class);
        myDataset.clear();
        for(Object i:all){
            CalendarDay it=new CalendarDay(((CalendarData)i).getDate());
            if(it.equals(now)){
                myDataset.add((CalendarData) i);
            }
        }
        CalendarAdapter.notifyDataSetChanged();
    }

    /**
     * 将日期转换为字符串
     * @return
     */
    private String getSelectedDatesString() {
        CalendarDay date = mcv.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        Log.i("sinstar", "getSelectedDatesString: "+date.toString());
        return FORMATTER.format(date.getDate());
    }

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();


    /**
     * 定义接口，向Activity传递数据
     */
    public interface OnFrgDataListener{
        public void progress(Date date);
    }

    /**
     * 注入实例方法
     * @param
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof OnFrgDataListener)
        {
            IListener = (OnFrgDataListener)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements OnFrgDataListener");
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();

        IListener = null;
    }


}
