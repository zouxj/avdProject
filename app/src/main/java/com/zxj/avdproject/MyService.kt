package com.zxj.avdproject

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zxj.avdproject.comn.SerialPortManager
import com.zxj.avdproject.comn.message.IMessage
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.comn.util.ToastUtil
import com.zxj.avdproject.model.GoodSellBean
import com.zxj.avdproject.uitls.SharedPreferencesUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MyService : Service() {
    private val anHour = 15 * 1000 // 15s更新一次
    private val mIntent = Intent("com.zxj.avdproject.RECEIVER")

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ID = "com.example.service1" //这里的id里面输入自己的项目的包的路径
            val NAME = "Channel One"
            val intent = Intent(this@MyService, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            var notification: NotificationCompat.Builder? = null //创建服务对象

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.setShowBadge(true)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                manager.createNotificationChannel(channel)
                notification = NotificationCompat.Builder(this@MyService, "").setChannelId(ID)
            } else {
                notification = NotificationCompat.Builder(this@MyService)
            }
            notification?.setContentTitle("标题")
                ?.setContentText("内容")
                ?.setWhen(System.currentTimeMillis())
                ?.setSmallIcon(R.drawable.ic_launcher)
                ?.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))
                ?.setContentIntent(pendingIntent)
                ?.build()
            val notification1: Notification? = notification?.build()

            startForeground(1, notification1)
        }
        EventBus.getDefault()?.register(this)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val task = Runnable {
            run {
                LogPlus.i("zouxiujun", "更新了${SystemClock.currentThreadTimeMillis()}")
                getGoods()
                sendBroadcast(mIntent)
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
    var mGoodSellBean: GoodSellBean? = null
    private fun getGoods() {
        OkGo.get<GoodSellBean>("${URLS}${ApiUrls.goods}").headers("deviceCode", getDeviceCode())
            .tag(this)
            .execute(object : JsonCallback<GoodSellBean>() {
                override fun onSuccess(response: Response<GoodSellBean>?) {
                    ToastUtil.showOne(
                        this@MyService,
                        "订单号===>" + response?.body()?.payload?.orderId + "===串口打开状态=" + MainActivity.mOpened
                    )
                    if (response?.body()?.payload?.orderId?.length ?: 0 > 0&&(MainActivity.mOpened)) {
                        mGoodSellBean = response?.body()
                        sendData("AAA0AC")

                    }
                }
            })
    }

    private fun setSell(outStatus: Int) {
        OkGo.post<String>("${URLS}${ApiUrls.sell}").headers("deviceCode", getDeviceCode())
            .params("orderId", mGoodSellBean?.payload?.orderId).params("outStatus", outStatus)
            .tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {

                }
            })
    }

    private fun getDeviceCode(): String {
        return SharedPreferencesUtils.getParam(
            this,
            SharedPreferencesUtils.DEVICE_CODE, ""
        ).toString()
    }

    private fun sendData(text: String) {
        if (TextUtils.isEmpty(text) || text.length % 2 != 0) {
            ToastUtil.showOne(this, "无效数据")
            return
        }
        ToastUtil.showOne(this@MyService, "出纸中...")
        SerialPortManager.instance().sendCommand(text)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(message: IMessage?) {
        // 收到时间，刷新界面
        when (message?.message) {
            "AAA1AC" -> {
                //出纸完成
                setSell(1)
            }
            "AAA2AC" -> {
                //缺纸
                setSell(2)
            }
            "AAA3AC" -> {
                //出纸失败
                setSell(3)
            }
        }

        LogPlus.i(message?.message)
    }
}