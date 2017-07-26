package com.wcp.weathertest;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.wcp.data.MyJsonTrans;
import com.wcp.data.WeatherData;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,CalendarFragment.OnFrgDataListener {

    WeatherData NewWeather=new WeatherData();

    String phone="";
    String text="";
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ArrayAdapter<String> adapter;
    List<String> contactsList = new ArrayList<>();
    String selectInviter;
    String selectInviterNumber;

    private WeatherFregment mWeatherFrag;
    private CalendarFragment mCalendarFreg;
    private TravelFragment mTravelFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //TODO:Your code here

        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(savedInstanceState==null) {
                mWeatherFrag = new WeatherFregment();
                Bundle timeBundle = new Bundle();
                mCalendarFreg = new CalendarFragment();
                mTravelFrag = new TravelFragment();
                ft.add(R.id.content, mWeatherFrag);
                ft.add(R.id.content, mCalendarFreg);
                ft.add(R.id.content, mTravelFrag);
            }
            ft.show(mWeatherFrag);
            ft.hide(mCalendarFreg);
            ft.hide(mTravelFrag);
            ft.commit();
        }catch(Exception e){
            Log.d("TAG","Fragment new Error");
            e.printStackTrace();
        }

        try {
            LitePal.getDatabase();
        }catch(Exception e){
            Log.d("TAG","LitePal new Error");
            e.printStackTrace();
        }

        try{
            startAlarm();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_weather) {
            if(mWeatherFrag==null) {
                mWeatherFrag = new WeatherFregment();
            }
            ft.show(mWeatherFrag);
            ft.hide(mCalendarFreg);
            ft.hide(mTravelFrag);
        } else if (id == R.id.nav_travel) {
            if(mTravelFrag==null) {
                mTravelFrag = new TravelFragment();
            }
            ft.show(mTravelFrag);
            ft.hide(mWeatherFrag);
            ft.hide(mCalendarFreg);
        } else if (id == R.id.nav_calendar) {
            if(mCalendarFreg==null) {
                mCalendarFreg = new CalendarFragment();
            }
            ft.show(mCalendarFreg);
            ft.hide(mWeatherFrag);
            ft.hide(mTravelFrag);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            sendMessage();
        } else if (id == R.id.nav_me) {
            final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("关于");
            builder.setMessage("wcp@Copyright");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog about=builder.show();
        } else if(id == R.id.nav_exit) {
            stopAlarm();
            Toast.makeText(MainActivity.this,"已退出",Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
        }
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendMessage(){
        sp=MainActivity.this.getSharedPreferences("weather",MODE_PRIVATE);
        editor=sp.edit();
        String jsonString=sp.getString("jsonString", "");
        MyJsonTrans mJson=new MyJsonTrans(NewWeather);
        NewWeather=mJson.TransJSON(jsonString);
        text=NewWeather.result.today.city+"今天："+NewWeather.result.today.weather+"，"
                +NewWeather.result.today.wind+"。气温"+NewWeather.result.today.temperature+"°，体感 "
                +NewWeather.result.today.dressing_index+","+NewWeather.result.today.dressing_advice+"明日"
                +NewWeather.result.future[0].weather+"，气温"+NewWeather.result.future[0].temperature+"°";

        final AlertDialog share;
        final AlertDialog.Builder shareDialog = new AlertDialog.Builder(MainActivity.this);
        shareDialog.setTitle("分享天气给");

        try {
            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, contactsList);
            //contactsView.setAdapter(adapter);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                readContacts();
            }
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
                Toast.makeText(getApplicationContext(), "已发送给"+selectInviter, Toast.LENGTH_SHORT).show();
            }
        });

        shareDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        share=shareDialog.show();

    }

    private void readContacts(){
        Cursor cursor=null;
        try{
            cursor=getContentResolver().query(ContactsContract.CommonDataKinds.
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
        }
    }

    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void progress(Date date) {
        //每次选择日期后,都会调用这个方法
        if(date!=null){
            String str=sdf.format(date);
        }
    }

    public void startAlarm(){
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int minutes = 1000 * 10;  //
        Intent toService = new Intent(this,AlarmService.class);
        PendingIntent pi = PendingIntent.getService(this,0,toService,0);
        long firstTime = SystemClock.elapsedRealtime();
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,minutes,pi);
        Toast.makeText(MainActivity.this, "通知已开启", Toast.LENGTH_SHORT).show();
    }
    public void stopAlarm(){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this,AlarmService.class);
        PendingIntent pi = PendingIntent.getService(this,0,intent,0);
        manager.cancel(pi);
        Toast.makeText(MainActivity.this, "通知已关闭", Toast.LENGTH_SHORT).show();
    }
}
