package com.zxj.avdproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class AutoUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, MyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service)
        } else {
            context.startService(service)
        }
    }
}