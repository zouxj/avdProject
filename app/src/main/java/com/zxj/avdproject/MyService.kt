package com.zxj.avdproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.text.TextUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.youth.banner.util.LogUtils
import com.zxj.avdproject.comn.SerialPortManager
import com.zxj.avdproject.comn.message.IMessage
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.comn.util.ToastUtil
import com.zxj.avdproject.model.AdBeans
import com.zxj.avdproject.uitls.SharedPreferencesUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MyService : Service() {
    private val anHour = 10 * 1000 // 30s更新一次


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val task = Runnable {
            run {
                LogPlus.i("zouxiujun", "更新了${SystemClock.currentThreadTimeMillis()}")
                getAd()

            }
        }
        Thread(task).start()


        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
        val triggerAtTime: Long = SystemClock.elapsedRealtime() + anHour
        val intent2 = Intent(this, AutoUpdateReceiver::class.java)
        val pi = PendingIntent.getBroadcast(this, 0, intent2, 0)
        manager[AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime] = pi

        return super.onStartCommand(intent, flags, startId)
    }
    //获取广告列表
    private fun getAd() {
        OkGo.get<String>("${URLS}${ApiUrls.goods}").headers("deviceCode",getDeviceCode()).tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    sendData("AAA0AC")

                }


            })
    }
    fun getDeviceCode():String{
        return SharedPreferencesUtils.getParam(this,
            SharedPreferencesUtils.DEVICE_CODE,"").toString()
    }

    private fun sendData(text: String) {
        if (TextUtils.isEmpty(text) || text.length % 2 != 0) {
            ToastUtil.showOne(this, "无效数据")
            return
        }
        SerialPortManager.instance().sendCommand(text)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(message: IMessage?) {
        // 收到时间，刷新界面
        LogPlus.i(message?.message)
    }
}