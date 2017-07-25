package com.wcp.adapter;

import android.content.Context;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.wcp.data.CalendarData;
import com.wcp.weathertest.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/23 0023.
 */

public class CalendarDecorator implements DayViewDecorator {
    private List<CalendarData> change=new ArrayList<>();
    private int color;

    public CalendarDecorator(List<CalendarData> dateToChange,int color) {
        this.color=color;

        change.clear();
        change.addAll(dateToChange);
    }

    @Override
    public void decorate(DayViewFacade view) {
        /*
        for(Object i:change){
            String kind=((CalendarData)i).getBelong();
            switch(kind){
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
        }
        */
        view.addSpan(new DotSpan(15,color));
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        for(Object aa:change){
            /*
            if (((CalendarData)aa).getDate().getYear()==day.getYear()
                    &&((CalendarData)aa).getDate().getMonth()==day.getMonth()
                    &&((CalendarData)aa).getDate().getDate()==day.getDay()){
                return true;
            }
            */
            CalendarDay it=new CalendarDay(((CalendarData)aa).getDate());
            if(it.equals(day)){
                return true;
            }
            Log.d("DAY",it.toString());
            Log.d("DAY",day.toString());
        }

        return false;
    }

}
