package com.highgreat.sven.batteryimprove.location;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;

import com.highgreat.sven.batteryimprove.JobManager;

import androidx.annotation.Nullable;

public class LocationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

//        wakelock锁主要是相对系统的休眠而言的，意思就是我的程序给CPU加了这个锁那系统就不会休眠了，
//        这样做的目的是为了全力配合我们程序的运行。有的情况如果不这么做就会出现一些问题，
//        比如微信等及时通讯的心跳包会在熄屏不久后停止网络访问等问题。所以微信里面是有大量使用到了wake_lock锁。希望经过上述共同学习你能正确使用WakeLock，不要做电池杀手。
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        //判断是否支持
//        pm.isWakeLockLevelSupported(PowerManager.PARTIAL_WAKE_LOCK);
//        //只唤醒cpu
//        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock locationLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "location_lock");
//       //使CPU一直处于工作状态，手动调用release来关闭
//        locationLock.acquire();

        //为了优化电池使用，我们可以想到办法来替换掉wakelock这种机制
        //替换方法一：alarm（闹钟）
//        alarmKeep();
        //替换方法二：JobScheduler
        JobManager.getInstance().init(this);
        LocationManager.getInstance().startLocation(this);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //        if (null != locationLock) {
//            locationLock.release();
//        }
        //注销广播接收者
//        unregisterReceiver(alarmReceiver);
        LocationManager.getInstance().destoryLocation();
    }

    private void alarmKeep() {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("LOCATION");
        //获得闹钟管理器
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        //动态注册一个Action为LOCATION的广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        registerReceiver(alarmReceiver,filter);
        //设置一个 每隔 5s 发送一个广播
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),5000,broadcast);
    }

    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(),"LOCATION")){
                LocationManager.getInstance().startLocation(LocationService.this);
            }
        }
    };

}
