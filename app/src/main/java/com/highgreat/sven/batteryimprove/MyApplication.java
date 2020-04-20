package com.highgreat.sven.batteryimprove;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;

import com.highgreat.sven.batteryimprove.location.LocationService;

import java.util.List;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(!TextUtils.equals(BuildConfig.APPLICATION_ID+":location",
                getProcessName(Process.myPid()))){
            Intent location = new Intent(this, LocationService.class);
            startService(location);
        }
    }

    private String getProcessName(int myPid) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        if(runningAppProcesses == null){
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
            if(appProcess.pid == myPid){
                return appProcess.processName;
            }
        }
        return null;
    }
}
