package com.wcp.weathertest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.wcp.data.CalendarData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    List<CalendarData> noticeEvent = new ArrayList<>();
    long now;

    public AlarmService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: 创建服务");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 服务开始查询");
        accessEventDatabase();
        if (noticeEvent.size()>0){
            lightScreen(AlarmService.this);
            showNotification();
        }
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    public void accessEventDatabase(){
        List<CalendarData> myEventList = DataSupport.findAll(CalendarData.class);
        noticeEvent.clear();
        now=System.currentTimeMillis();
        for(CalendarData event: myEventList){
            for(int i=0;i<event.getRemind().size();i++) {
                if (event.getRemind().size()>0 && now >= event.getRemind().get(i) ) {
                    noticeEvent.add(event);
                    //event.setNotification(true);
                    event.getRemind().remove(i);
                    i--;
                    event.save();
                }
            }
        }
    }

    public void showNotification(){
        Intent toMain = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,toMain, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        for(CalendarData event:noticeEvent){
            Notification notification = new Notification.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_settings_system_daydream_black_36dp))
                    .setSmallIcon(R.drawable.ic_flight_takeoff_black_36dp)  //设置小图标
                    .setContentIntent(pi)       //单击后跳转
                    .setContentTitle("即将到来的新日程")
                    .setContentText("\""+event.getName()+"\"大约在"+(event.getDate().getTime()-(new Date()).getTime())/(1000*60)+"分钟后开始")
                    .setDefaults(Notification.DEFAULT_VIBRATE)   //震动
                    .setAutoCancel(true) // 点击后自动取消
                    .build();
            //manager.notify((int)(System.currentTimeMillis()),notification);
            manager.notify(noticeEvent.indexOf(event),notification);
        }
    }

    public void lightScreen(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();
        Log.d(TAG, "lightScreen: task : screen on >>> " + isScreenOn);
        if (isScreenOn == false){
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE,"MyLock");
            wakeLock.acquire(1000);
            PowerManager.WakeLock wakeLock_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wakeLock_cpu.acquire(1000);
        }
    }
}
