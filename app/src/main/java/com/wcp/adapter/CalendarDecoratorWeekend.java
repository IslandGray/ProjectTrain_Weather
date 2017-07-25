package com.wcp.adapter;

import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.wcp.weathertest.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by DELL-pc on 2017/7/23 0023.
 */

public class CalendarDecoratorWeekend implements DayViewDecorator {
    private int mColor;

    public CalendarDecoratorWeekend(int color) {
        mColor=color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int weekday=day.getCalendar().get(Calendar.DAY_OF_WEEK);
        if(weekday==1 || weekday==7){
            return true;
        }
        if(day.equals(new CalendarDay(new Date()))){
            //mColor= R.color.today;
            return true;
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(mColor));
    }
}
