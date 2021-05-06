package com.zxj.avdproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, MyService::class.java)
        context.startService(i)
    }
}