package com.zxj.avdproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.serialport.SerialPortFinder
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.youth.banner.listener.OnPageChangeListener
import com.youth.banner.util.LogUtils
import com.zxj.avdproject.comn.Device
import com.zxj.avdproject.comn.SerialPortManager
import com.zxj.avdproject.comn.message.IMessage
import com.zxj.avdproject.comn.message.PlayManager
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.comn.util.ToastUtil
import com.zxj.avdproject.model.AdBeans
import com.zxj.avdproject.model.Template
import com.zxj.avdproject.ui.LoginActivity
import com.zxj.avdproject.uitls.QRCodeUtil
import com.zxj.avdproject.uitls.SharedPreferencesUtils
import com.zxj.avdproject.uitls.SharedPreferencesUtils.DEVICE_CODE
import com.zxj.avdproject.uitls.SharedPreferencesUtils.deviceQrcode
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

const val URLS = "https://api.sczn-ssas.com/api/"

const val START_PLAY_STATUS = 0//播放
const val STOP_PLAY_STATUS = 1//暂停
const val ERROR_PLAY_STATUS = 2//出错

class MainActivity : AppCompatActivity() {

    private var mDevices: Array<String>? = null
    private var mDevice: Device? = null
    private var mDeviceIndex = 4
    private val handler = Handler()
    private val adList = arrayListOf<Template>()
    private var msgReceiver: MsgReceiver? = null

    private val mAdapter by lazy {
        ImageNetAdapter(this, adList)
    }


    private val task: Runnable = object : Runnable {
        override fun run() {
            // TODOAuto-generated method stub
            SystemHelper.setTopApp(this@MainActivity)
            handler.postDelayed(this, 30 * 1000) //设置延迟时间，此处是5秒
            //需要执行的代码
        }
    }

    companion object {
        var mOpened = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if ((SharedPreferencesUtils.getParam(this, DEVICE_CODE, "") as String).isEmpty()
        ) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        EventBus.getDefault().register(this)
        banner.adapter = mAdapter
        banner.setLoopTime(15000)
        setStatus()
//        getReportError()
//        getSize()
//        getActive()
//        getIncrease()
//        getResetNum()
        initDevice()
        switchSerialPort()
        startService(Intent(this, MyService::class.java))
//        getAccountToken()
        handler.postDelayed(task, 30000);//延
        val path = "android.resource://" + packageName + "/" + R.raw.bottom_ad
        image_gif.setVideoPath(path)
        image_gif.setOnPreparedListener {
            image_gif.start()
        }
        image_gif.setOnErrorListener { mp, what, extra ->
            image_gif.start()
            true
        }
        image_gif.setOnCompletionListener {
            image_gif.start()
        }
//        Glide.with(this).asGif().load(R.drawable.ad).into(image_gif)// 迟调用
        img_core.setImageBitmap(QRCodeUtil.createQRCode(SharedPreferencesUtils.getParam(this,deviceQrcode,"没有支付二维码").toString()))
//        Glide.with(this)
//            .load("https://n.sinaimg.cn/tech/transform/324/w149h175/20210423/5868-kpamyii5341282.gif")
//            .into(image_gif)

        banner.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                LogPlus.i("")

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        //动态注册广播接收器
        msgReceiver = MsgReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.zxj.avdproject.RECEIVER")
        registerReceiver(msgReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        banner.stop()
    }

    override fun onResume() {
        super.onResume()
        banner.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        unregisterReceiver(msgReceiver)
    }

    /**
     * 初始化设备列表
     */
    private fun initDevice() {
        val serialPortFinder = SerialPortFinder()

        // 设备
        mDevices = serialPortFinder.allDevicesPath
        if (mDevices?.size == 0) {
            mDevices = arrayOf(
                getString(R.string.no_serial_device)
            )
        }
        // 波特率
        mDevice = Device(mDevices?.getOrNull(mDeviceIndex) ?: "", "19200")
    }


    /**
     * 打开或关闭串口
     */
    private fun switchSerialPort() {
        if (mOpened) {
            SerialPortManager.instance().close()
            mOpened = false
        } else {
            // 保存配置
            mOpened = SerialPortManager.instance().open(mDevice) != null
            if (mOpened) {
                ToastUtil.showOne(this, "成功打开串口")
            } else {
                ToastUtil.showOne(this, "打开串口失败")
            }
        }
    }

    //激活
    private fun setStatus() {
        OkGo.post<String>("${URLS}${ApiUrls.status}").headers("deviceCode", getDeviceCode())
            .params("status", "1").tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    getADList()
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }

    fun getDeviceCode(): String {
        return SharedPreferencesUtils.getParam(
            this,
            DEVICE_CODE, ""
        ).toString()
    }

    var getAdJson = ""

    //获取广告列表
    fun getADList() {
        OkGo.get<AdBeans>("${URLS}${ApiUrls.getAd}").headers("deviceCode", getDeviceCode())
            .tag(this)
            .execute(object : JsonCallback<AdBeans>() {
                override fun onSuccess(response: Response<AdBeans>?) {
                    response?.body()?.payload?.let {
                        if (getAdJson != response.body().toString()) {
                            adList.clear()
                            adList.addAll(it)
                            mAdapter.notifyDataSetChanged()
                        }
                        getAdJson = response.body().toString()

                    }

                    LogUtils.d(response?.body().toString())
                }


            })
    }

    /**
     * 上报错误
     */
    private fun getReportError() {
        OkGo.post<String>("${URLS}${ApiUrls.reportError}").tag(this).params("content", "测试")
            .params("errorType", "1").execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }

    /**
     * 重置计量
     */
    private fun getResetNum() {
        OkGo.post<String>("${URLS}${ApiUrls.reportError}").tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }

    /**
     * 根据账户获取token
     */
    private fun getAccountToken() {
        OkGo.post<String>("${URLS}${ApiUrls.accountToken}").tag(this)
            .params("", "").params("userName", "test001").params("devicePwd", "123456")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }


    /**
     * 激活机器
     */
    private fun getActive() {
        OkGo.post<String>("${URLS}${ApiUrls.active}").tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }

    /**
     * 激活机器
     */
    fun getSize() {
        val paramHasp = mutableMapOf<String, String>()
        paramHasp["deviceCode"] = "XC123n"
        paramHasp["type"] = "1"
        paramHasp["address"] = "深圳"
        paramHasp["sizeId"] = "XC123n"
        paramHasp["screen"] = "1"
        paramHasp["inductive"] = "1"
        paramHasp["scan"] = "1"
        paramHasp["maxNum"] = "50"
        OkGo.get<String>("${URLS}${ApiUrls.getSize}").tag(this).params(paramHasp)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(message: IMessage?) {
        // 收到时间，刷新界面
        LogPlus.i(message?.message)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetPlayManagerEvent(message: PlayManager) {
        // 收到时间，刷新界面
        when (message.playType) {
            START_PLAY_STATUS -> {
                //播放
                banner.stop()
            }
            STOP_PLAY_STATUS -> {
                //播放结束
                banner.start()

            }
            ERROR_PLAY_STATUS -> {
                //播放出错
                banner.start()


            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    var test=0
    inner class MsgReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //拿到进度，更新UI
            test++
            if (test>=10000){
                ToastUtil.showOne(this@MainActivity,"请续费")
                finish()
            }
            getADList()
        }
    }



}
