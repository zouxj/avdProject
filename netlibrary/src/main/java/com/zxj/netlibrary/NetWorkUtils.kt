package com.zxj.netlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

/**
 * desc   :
 * date   : 2020/08/18
 * version: 1.0
 */
class NetWorkUtils {

    companion object {
        @SuppressLint("MissingPermission")
        fun isNetworkAvailable(context: Context): Boolean {
            val manager = context.applicationContext.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return !(null == info || !info.isAvailable)
        }
    }
}