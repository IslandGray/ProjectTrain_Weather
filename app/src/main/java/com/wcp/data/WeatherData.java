package com.wcp.data;

import java.util.Date;

/**
 * Created by DELL-pc on 2017/7/12 0012.
 */

public class WeatherData {
    public String resultcode;
    public String reason;
    public Result result;
    public Date updateTime;

    public WeatherData(){
        this.result=new Result();
    }

    public class Result{
        public SK sk;
        public Today today;
        public FutureData[] future=new FutureData[7];

        public Result(){
            this.sk=new SK();
            this.today=new Today();
            for(int i=0;i<7;i++){
                this.future[i]=new FutureData();
            }
        }

        public class SK{
            public String temp;
            public String wind_direction;
            public String wind_strength;
            public String humidity;
            public String time;
        }

        public class Today{
            public String city;
            public String date_y;
            public String week;
            public String temperature;
            public String weather;
            public String fa;
            public String fb;
            public String wind;
            public String dressing_index;
            public String dressing_advice;
            public String uv_index;
            public String comfort_index;
            public String wash_index;
            public String travel_index;
            public String exercise_index;
            public String drying_index;
        }
    }
}
