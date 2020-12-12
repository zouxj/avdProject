package com.zxj.avdproject

import android.app.Application
import com.zxj.netlibrary.NetContext

/**
 *
 * @des:
 * @data: 12/12/20 4:50 PM
 * @Version: 1.0.0
 */
class AvdApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NetContext.instance.CONTEXT = this
    }
}