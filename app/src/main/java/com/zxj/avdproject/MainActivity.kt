package com.zxj.avdproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        banner.adapter = ImageNetAdapter(getTestData3())
        banner.setLoopTime(3000)
//        banner.indicator = RectangleIndicator(this)
//        banner.setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
//        banner.setIndicatorRadius(0)
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