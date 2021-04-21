package com.zxj.avdproject

import android.os.Bundle
import android.os.Handler
import android.serialport.SerialPortFinder
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.youth.banner.listener.OnPageChangeListener
import com.youth.banner.util.LogUtils
import com.zxj.avdproject.comn.Device
import com.zxj.avdproject.comn.SerialPortManager
import com.zxj.avdproject.comn.message.IMessage
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.comn.util.PrefHelper
import com.zxj.avdproject.comn.util.ToastUtil
import com.zxj.avdproject.comn.util.constant.PreferenceKeys
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

val URLS = "http://47.98.41.154:6578/api/"


class MainActivity : AppCompatActivity() {

    private var mDevices: Array<String>? = null
    private var mDevice: Device? = null

    private var mDeviceIndex = 5
    private var mBaudrateIndex = 0
    private val handler = Handler()
    var player: StandardGSYVideoPlayer? = null

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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        banner.adapter = ImageNetAdapter(getTestData3())
        banner.setLoopTime(30000)
        getAd()
        getReportError()
        getSize()
        getActive()
        getIncrease()
        getAccountToken()
        getResetNum()
        initDevice()
        switchSerialPort()
        handler.postDelayed(task, 30000);//延迟调用
        Glide.with(this).asGif().load(R.drawable.test_image).into(image_gif)
        banner.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val viewHolder: RecyclerView.ViewHolder =
                    (banner.adapter as ImageNetAdapter).mVHMap.get(position)
                player?.onVideoPause()
                player = (viewHolder as? VideoHolder)?.videoView
                player?.startPlayLogic()


            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        player?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        player?.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
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
        mDeviceIndex = PrefHelper.getDefault().getInt(PreferenceKeys.SERIAL_PORT_DEVICES, 4)
        mDeviceIndex = if (mDeviceIndex >= mDevices!!.size) mDevices!!.size - 1 else mDeviceIndex
        mBaudrateIndex = PrefHelper.getDefault().getInt(PreferenceKeys.BAUD_RATE, 0)
        mDevice = Device(mDevices?.getOrNull(mDeviceIndex) ?: "", "19200")
    }

    private fun sendData(text: String) {
        if (TextUtils.isEmpty(text) || text.length % 2 != 0) {
            ToastUtil.showOne(this, "无效数据")
            return
        }
        SerialPortManager.instance().sendCommand(text)
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
            PrefHelper.getDefault().saveInt(PreferenceKeys.SERIAL_PORT_DEVICES, mDeviceIndex)
            PrefHelper.getDefault().saveInt(PreferenceKeys.BAUD_RATE, mBaudrateIndex)
            mOpened = SerialPortManager.instance().open(mDevice) != null
            if (mOpened) {
                sendData("AAA0AC")
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

    //获取广告列表
    private fun getAd() {
        OkGo.get<String>("${URLS}${ApiUrls.getAd}").tag(this).execute(object : StringCallback() {
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

}
