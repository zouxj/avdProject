package com.zxj.avdproject;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ActivityLifecycleCallbackWrapper implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "LifecycleCallback";
    private int count;
    private boolean isForeground;

    public boolean isForeground() {
        return isForeground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        //to do
    }

    @Override
    public void onActivityStarted(Activity activity) {
        count ++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //to do
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //to do
    }

    @Override
    public void onActivityStopped(Activity activity) {
        count --;
        if(count == 0) {
            isForeground = true;
        }

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        //to do
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //to do
    }
}