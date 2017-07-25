package com.wcp.weathertest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wcp.data.CalendarData;
import com.wcp.data.MyJsonTrans;
import com.wcp.data.WeatherData;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DELL-pc on 2017/7/19 0019.
 */

public class WeatherFregment extends Fragment {
    private Handler handler;
    private Thread thread;
    WeatherData NewWeather=new WeatherData();
    String nowWeather="";
    String nowCity="西安";
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ImageButton sync;
    ImageButton freshing;
    ImageButton addCity;

    TextView cityText;
    TextView weatherText;
    TextView tempText;
    LinearLayout days;
    TextView today;
    TextView todayItem;
    ListView weekList;
    TextView syncTime;
    TextView nowW;
    FloatingActionButton add;
    private SwipeRefreshLayout mRefreshLayout;

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.weather_fregment,null);

        sync=(ImageButton)view.findViewById(R.id.fresh);
        freshing=(ImageButton)view.findViewById(R.id.freshing);
        cityText=(TextView)view.findViewById(R.id.cityname);
        weatherText=(TextView)view.findViewById(R.id.curWeather);
        tempText=(TextView)view.findViewById(R.id.curTemp);
        days=(LinearLayout) view.findViewById(R.id.testLinear);
        today=(TextView)view.findViewById(R.id.today);
        todayItem=(TextView)view.findViewById(R.id.todayData);
        weekList=(ListView)view.findViewById(R.id.tendays);
        syncTime=(TextView)view.findViewById(R.id.sync_time);
        addCity=(ImageButton)view.findViewById(R.id.addButton);
        nowW=(TextView)view.findViewById(R.id.now_weather);
        add=(FloatingActionButton)view.findViewById(R.id.fab);

        mRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.layout_swipe_refresh_weather);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync.performClick();
                //mRefreshLayout.setRefreshing(false);
            }
        });



        sp=WeatherFregment.this.getContext().getSharedPreferences("weather",MODE_PRIVATE);
        editor=sp.edit();

        //加载上次保存值
        try {
            fresh_weather(sp.getString("jsonString", ""));
        }catch(Exception e){
            e.printStackTrace();
        }

        //刷新事件
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sync.setVisibility(View.INVISIBLE);
                //freshing.setVisibility(View.VISIBLE);

                thread=new Thread(new Runnable() {
                    String result="";
                    String strurl = "http://apis.haoservice.com/weather?key=bf2406eb4df549ee9f5759a53b592848&cityname=";
                    URL url=null;
                    @Override
                    public void run() {
                        try {
                            strurl += URLEncoder.encode(nowCity, "utf-8");
                        }catch(UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                        try{
                            url = new URL(strurl);
                            System.out.println(url.toString());
                            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                            BufferedReader bufferReader = new BufferedReader(in);
                            String readline = null;
                            while((readline = bufferReader.readLine())!=null){
                                result += readline;
                            }
                            in.close();
                            urlConn.disconnect();
                            System.out.println(result);
                        }catch(Exception e){
                            System.out.println("发送GET请求出现异常！" + e);
                            e.printStackTrace();
                        }
                        //通信
                        Message m=handler.obtainMessage();
                        m.obj=result;
                        m.what=0x101;
                        handler.sendMessage(m);
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }
        });


        //变更城市事件
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherFregment.this.getContext(),AddCity.class);
                Bundle data=new Bundle();
                intent.putExtras(data);
                startActivityForResult(intent,0x111);
            }
        });

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==0x101){
                    //保存为Preference
                    nowWeather=(String)msg.obj;
                    editor.putString("jsonString",nowWeather);
                    editor.commit();
                    //刷新
                    fresh_weather(nowWeather);
                }
                super.handleMessage(msg);
            }
        };

        return view;
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.menu.main).setVisible(true);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_exit){
            this.getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private List<Map<String,Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for(int i=0;i<7;i++) {
            map = new HashMap<String, Object>();
            map.put("week_day", NewWeather.result.future[i].week);
            switch(NewWeather.result.future[i].weather){
                case "晴":{
                    map.put("icon",R.drawable.w_sunny);
                    break;
                }
                case "多云":{
                    map.put("icon",R.drawable.w_cloudy);
                    break;
                }
                case "阴":{
                    map.put("icon",R.drawable.w_overcast);
                    break;
                }
                case "雷阵雨":{
                    map.put("icon",R.drawable.w_thunder_rain);
                    break;
                }
                case "小雨":{
                    map.put("icon",R.drawable.w_rain_small);
                    break;
                }

                default:{
                    map.put("icon",R.drawable.w_na);
                }
            }
            map.put("week_temp",NewWeather.result.future[i].temperature+"°");
            list.add(map);
        }
        return list;
    }

    private void fresh_weather(String data){
        //城市名
        cityText.setText(nowCity);
        //解析json
        MyJsonTrans mJson=new MyJsonTrans(NewWeather);
        NewWeather=mJson.TransJSON(data);
        weatherText.setText(NewWeather.result.today.weather);
        tempText.setText(NewWeather.result.sk.temp+"°");
        mRefreshLayout.setRefreshing(false);
        //sync.setVisibility(View.VISIBLE);
        //freshing.setVisibility(View.INVISIBLE);
        syncTime.setText("更新时间："+NewWeather.result.sk.time);
        today.setText("今天："+NewWeather.result.today.weather+"，"
                +NewWeather.result.today.wind+"。气温"+NewWeather.result.today.temperature+"°，体感 "
                +NewWeather.result.today.dressing_index+","+NewWeather.result.today.dressing_advice);
        todayItem.setText("\n风力："+NewWeather.result.sk.wind_strength+"\n"
                +"湿度："+NewWeather.result.sk.humidity+"%\n\n"
                +"穿衣指数："+NewWeather.result.today.dressing_index+"\n\n"
                +"紫外钱强度："+NewWeather.result.today.uv_index+"\n"
                +"洗车指数："+NewWeather.result.today.wash_index+"\n\n"
                +"户外锻炼："+NewWeather.result.today.exercise_index+"\n"
                +"旅行指数："+NewWeather.result.today.travel_index+"\n\n"
                +"舒适度指数："+NewWeather.result.today.comfort_index+"\n"
                +"干燥程度："+NewWeather.result.today.drying_index+"\n");
        nowW.setText(NewWeather.result.today.temperature+"°");


        final SimpleAdapter adapter=new SimpleAdapter(WeatherFregment.this.getContext(),getData(),R.layout.weatheritem,
                new String[]{"week_day","icon","week_temp"},
                new int[]{R.id.week_day,R.id.weather_icon,R.id.week_temp});
        int totalHeight = 0;
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            View listItem = adapter.getView(i, null, weekList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = weekList.getLayoutParams();
        params.height = totalHeight + (weekList.getDividerHeight() *
                (adapter.getCount() - 1));
        weekList.setLayoutParams(params);
        weekList.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==0x111 && resultCode==0x111){
            Bundle bundle=data.getExtras();
            nowCity=bundle.getString("newcity");

            mRefreshLayout.setRefreshing(true);
            sync.performClick();
        }
    }
}
