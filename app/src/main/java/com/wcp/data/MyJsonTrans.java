package com.wcp.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Created by DELL-pc on 2017/7/17 0017.
 */

public class MyJsonTrans {
    private WeatherData NewWeather;

    public MyJsonTrans(WeatherData w){
        this.NewWeather=w;
    }

    public WeatherData TransJSON(String jsonString){
        try {
            Map mapType = JSON.parseObject(jsonString, Map.class);
            {
                NewWeather.resultcode =  mapType.get("error_code").toString();  //结果码
                NewWeather.reason =  mapType.get("reason").toString();  //故障原因
                String result_str =  mapType.get("result").toString();  //result字段集合
                //解析result
                Map result = JSON.parseObject(result_str, Map.class);
                {
                    String sk_str=result.get("sk").toString();  //当前实况天气
                    String today_str=result.get("today").toString();    //今日
                    String future_str=result.get("future").toString();  //未来几日
                    //解析sk
                    Map sk=JSON.parseObject(sk_str, Map.class);
                    {
                        NewWeather.result.sk.temp=sk.get("temp").toString();
                        NewWeather.result.sk.wind_direction=sk.get("wind_direction").toString();
                        NewWeather.result.sk.wind_strength=sk.get("wind_strength").toString();
                        NewWeather.result.sk.humidity=sk.get("humidity").toString();
                        NewWeather.result.sk.time=sk.get("time").toString();
                    }
                    //解析today
                    Map today=JSON.parseObject(today_str, Map.class);
                    {
                        NewWeather.result.today.city=today.get("city").toString();
                        NewWeather.result.today.date_y=today.get("date_y").toString();
                        NewWeather.result.today.week=today.get("week").toString();
                        NewWeather.result.today.temperature=today.get("temperature").toString();
                        NewWeather.result.today.weather=today.get("weather").toString();
                        NewWeather.result.today.fa=today.get("fa").toString();
                        NewWeather.result.today.fb=today.get("fb").toString();
                        NewWeather.result.today.wind=today.get("wind").toString();
                        NewWeather.result.today.dressing_index=today.get("dressing_index").toString();
                        NewWeather.result.today.dressing_advice=today.get("dressing_advice").toString();
                        NewWeather.result.today.uv_index=today.get("uv_index").toString();
                        NewWeather.result.today.comfort_index=today.get("comfort_index").toString();
                        NewWeather.result.today.wash_index=today.get("wash_index").toString();
                        NewWeather.result.today.travel_index=today.get("travel_index").toString();
                        NewWeather.result.today.exercise_index=today.get("exercise_index").toString();
                        NewWeather.result.today.drying_index=today.get("drying_index").toString();
                    }
                    //解析future
                    JSONArray array=JSONArray.parseArray(future_str);
                    List<String> future=array.toJavaList(String.class);
                    //List<String> future=JSON.parseObject(future_str, List.class);
                    String[] FutData=new String[7];
                    for(int i=0;i<7;i++){
                        FutData[i]=future.get(i);
                        System.out.println(FutData[i]);
                        Map temp=JSON.parseObject(FutData[i], Map.class);
                        NewWeather.result.future[i].temperature=temp.get("temperature").toString();
                        NewWeather.result.future[i].weather=temp.get("weather").toString();
                        NewWeather.result.future[i].fa=temp.get("fa").toString();
                        NewWeather.result.future[i].fb=temp.get("fb").toString();
                        NewWeather.result.future[i].wind=temp.get("wind").toString();
                        NewWeather.result.future[i].week=temp.get("week").toString();
                        NewWeather.result.future[i].date=temp.get("date").toString();
                    }
                }
            }
        }catch(Exception e){
            System.out.println("JSON转换异常");
            e.printStackTrace();
        }
        return this.NewWeather;
    }
}
