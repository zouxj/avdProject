package com.zxj.avdproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.serialport.SerialPortFinder
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.youth.banner.listener.OnPageChangeListener
import com.youth.banner.util.LogUtils
import com.zxj.avdproject.comn.Device
import com.zxj.avdproject.comn.SerialPortManager
import com.zxj.avdproject.comn.message.IMessage
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.comn.util.PrefHelper
import com.zxj.avdproject.comn.util.ToastUtil
import com.zxj.avdproject.comn.util.constant.PreferenceKeys
import com.zxj.avdproject.model.AdBeans
import com.zxj.avdproject.model.Template
import com.zxj.avdproject.ui.LoginActivity
import com.zxj.avdproject.uitls.SharedPreferencesUtils
import com.zxj.avdproject.uitls.SharedPreferencesUtils.DEVICE_CODE
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

val URLS = "https://api.sczn-ssas.com/api/"


class MainActivity : AppCompatActivity() {

    private var mDevices: Array<String>? = null
    private var mDevice: Device? = null

    private var mDeviceIndex = 4
    private val handler = Handler()
    private val adList = arrayListOf<Template>()
    private val mAdapter by lazy {
        ImageNetAdapter(adList)
    }

    private val task: Runnable = object : Runnable {
        override fun run() {
            // TODOAuto-generated method stub
            SystemHelper.setTopApp(this@MainActivity)
            handler.postDelayed(this, 30 * 1000) //设置延迟时间，此处是5秒
            //需要执行的代码
        }
    }
    private var mOpened = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if ((SharedPreferencesUtils.getParam(this, DEVICE_CODE, "") as String).isEmpty()
        ) {
            startActivity(Intent(this,LoginActivity::class.java))
            return
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        banner.adapter = mAdapter
        banner.setLoopTime(3000)
        setStatus()
//        getReportError()
//        getSize()
//        getActive()
//        getIncrease()
//        getResetNum()
        initDevice()
        switchSerialPort()
        startService(Intent(this,MyService::class.java))
        startActivity(Intent(this@MainActivity,VideoPlayActivity::class.java))

//        getAccountToken()

        handler.postDelayed(task, 30000);//延迟调用
        Glide.with(this)
            .load("https://n.sinaimg.cn/tech/transform/324/w149h175/20210423/5868-kpamyii5341282.gif")
            .into(image_gif)
        banner.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position==banner.realCount)     {
                    startActivity(Intent(this@MainActivity,VideoPlayActivity::class.java))
                }


            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
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

    private fun getTestData3(): List<AvdDataBean> {
        val list: MutableList<AvdDataBean> = ArrayList()
        list.add(
            AvdDataBean(
                imageUrl = "http://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg",
                viewType = 1
            )
        )
        list.add(
            AvdDataBean(
                imageUrl = "http://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg",
                viewType = 1
            )
        )
        list.add(
            AvdDataBean(
                imageUrl = "http://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",
                viewType = 1
            )

        )
        list.add(
            AvdDataBean(
                imageUrl = "http://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",
                viewType = 1
            )

        )
        list.add(
            AvdDataBean(

                videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
                viewType = 2
            )
        )
        return list
    }

    //激活
    private fun setStatus() {
        OkGo.post<String>("${URLS}${ApiUrls.status}").headers("deviceCode",getDeviceCode()).params("status", "1").tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    getAd()
                    LogUtils.d(response?.body().toString())
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    LogUtils.d(response?.body().toString())
                }
            })
    }
    fun getDeviceCode():String{
        return SharedPreferencesUtils.getParam(this,
            DEVICE_CODE,"").toString()
    }
    //获取广告列表
    private fun getAd() {
        OkGo.get<AdBeans>("${URLS}${ApiUrls.getAd}").headers("deviceCode",getDeviceCode()).tag(this)
            .execute(object : JsonCallback<AdBeans>() {
                override fun onSuccess(response: Response<AdBeans>?) {
                    response?.body()?.payload?.let {
                        adList.addAll(it)
                        mAdapter.notifyDataSetChanged()

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
     * 上报计数
     */
    private fun getIncrease() {
        OkGo.post<String>("${URLS}${ApiUrls.increase}").params("num", "10").tag(this)
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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
//    /**
//     * 系统类错误，4开头
//     */
//    const ERROR_CODE_40000 = 40000;     //系统繁忙
//    const ERROR_CODE_40001 = 40001;     //非法操作
//    const ERROR_CODE_40002 = 40002;     //缺少参数
//    const ERROR_CODE_40003 = 40003;     //参数异常
//    const ERROR_CODE_40004 = 40004;     //数据不存在
//    const ERROR_CODE_40005 = 40005;     //身份认证失败
//
//    /**
//     * 业务类错误，5开头
//     */
//    const ERROR_CODE_50000 = 50000;     //用户名或密码错误
//    const ERROR_CODE_50001 = 50001;     //账户已禁用
//    const ERROR_CODE_50002 = 50002;     //登录超时
//    const ERROR_CODE_50003 = 50003;     //手机已注册
//    const ERROR_CODE_50004 = 50004;     //渠道不存在
//    const ERROR_CODE_50005 = 50005;     //机器设备异常
//    const ERROR_CODE_50006 = 50006;     //计量已满
//    const ERROR_CODE_50007 = 50007;     //无投放广告
//    const ERROR_CODE_50008 = 50008;     //机器不存在或未激活
//    const ERROR_CODE_50009 = 50009;     //订单未支付
//    const ERROR_CODE_50010 = 50010;     //无待出货订单
//    const ERROR_CODE_50011 = 50011;     //订单状态异常
//    const ERROR_CODE_50012 = 50012;     //机器未绑定，请在小程序绑定机器
//    const ERROR_CODE_50013 = 50013;     //设备不在线
}
