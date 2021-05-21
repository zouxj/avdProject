package com.zxj.avdproject.uitls

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager


fun Context.dip2px(dpValue: Float): Int {
    return dpValue.toInt()
    val scale: Float = resources.displayMetrics.density
    return ((dpValue * scale + 0.5f * (if (dpValue >= 0.0f) 1 else -1).toFloat()).toInt())
}

fun Context.dimenDip2px(dpValue: Int): Int {
    val value: Float = resources.getDimension(dpValue)
    return dip2px(value / resources.displayMetrics.density)
}

fun Context.px2dip(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f * (if (pxValue >= 0.0f) 1 else -1).toFloat()).toInt()
}

fun Context.defaultDisplay(): DisplayMetrics {
    val outMetrics = DisplayMetrics()
    val ws: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    ws.defaultDisplay.getMetrics(outMetrics)
    return outMetrics
}