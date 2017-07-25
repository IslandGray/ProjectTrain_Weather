package com.wcp.data;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.litepal.crud.DataSupport;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by DELL-pc on 2017/7/21 0021.
 */

public class CalendarData extends DataSupport {
    private int id;
    private String Name;
    private String Belong;
    private Date Date;
    private Date EndDate;
    private Boolean AllDay;
    private List<Long> Remind=new ArrayList<>();
    private String Invite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBelong() {
        return Belong;
    }

    public void setBelong(String belong) {
        Belong = belong;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public java.util.Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(java.util.Date endDate) {
        EndDate = endDate;
    }

    public Boolean getAllDay() {
        return AllDay;
    }

    public void setAllDay(Boolean allDay) {
        AllDay = allDay;
    }

    public List<Long> getRemind() {
        return Remind;
    }

    public void setRemind(List<Long> remind) {
        this.Remind.clear();
        this.Remind.addAll(remind);
    }

    public String getInvite() {
        return Invite;
    }

    public void setInvite(String invite) {
        Invite = invite;
    }

    @Override
    public String toString() {
        return "CalendarData{" +
                "id=" + id +
                ", Name='" + Name + '\'' +
                ", Belong='" + Belong + '\'' +
                ", Date=" + Date +
                ", EndDate=" + EndDate +
                ", AllDay=" + AllDay +
                ", Remind=" + Remind +
                ", Invite='" + Invite + '\'' +
                '}';
    }
}
