package com.zxj.avdproject

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.util.BannerUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.youth.banner.util.LogUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

val URLS = "http://47.98.41.154:6578/api/"


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
<<<<<<< HEAD
        banner_looper.adapter = ImageNetAdapter(getTestData3())
        banner_looper.setLoopTime(3000)
        val bf= BufferedReader( FileReader( File("/sys/class/net/wlan0/address"))).readLine();

//        banner.indicator = RectangleIndicator(this)
//        banner.setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
//        banner.setIndicatorRadius(0)
=======
        banner.adapter = ImageNetAdapter(getTestData3())
        banner.setLoopTime(3000)
>>>>>>> parent of ea319b1... 112
        getAd()
        getReportError()
        getSize()
        getActive()
        getIncrease()
        getAccountToken()
        getResetNum()


    }

    private fun getTestData3(): List<AvdDataBean> {
        val list: MutableList<AvdDataBean> = ArrayList()
        list.add(
            AvdDataBean(
                imageUrl = "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg",
                viewType = 1
            )
        )
        list.add(
            AvdDataBean(
                imageUrl = "https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg",
                viewType = 1
            )
        )
        list.add(
            AvdDataBean(
                imageUrl = "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",
                viewType = 1
            )

        )
        list.add(
            AvdDataBean(
                imageUrl = "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",
                viewType = 1
            )

        )
        list.add(
            AvdDataBean(
                imageUrl = "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
                viewType = 1
            )
        )
        return list
    }
}

//获取广告列表
private fun getAd() {
    OkGo.get<String>("${URLS}${ApiUrls.getAd}").tag("this").execute(object : StringCallback() {
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
    OkGo.post<String>("${URLS}${ApiUrls.reportError}").tag("this").params("content", "测试")
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
    OkGo.post<String>("${URLS}${ApiUrls.reportError}").tag("this")
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
    OkGo.post<String>("${URLS}${ApiUrls.accountToken}").tag("this")
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
    OkGo.post<String>("${URLS}${ApiUrls.increase}").params("num", "10").tag("this")
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
    OkGo.post<String>("${URLS}${ApiUrls.active}").tag("this")
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
    OkGo.get<String>("${URLS}${ApiUrls.getSize}").tag("this").params(paramHasp)
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

