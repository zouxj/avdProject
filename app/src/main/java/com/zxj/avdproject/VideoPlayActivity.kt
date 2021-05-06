package com.zxj.avdproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.danikula.videocache.HttpProxyCacheServer
import com.zxj.avdproject.comn.util.LogPlus
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {
    var url = "http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"
    private val MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
            )
        } else {
            Log.i("aaa", "权限已申请")
            initVideo()
        }
    }

    private fun initVideo() {
        val proxy: HttpProxyCacheServer? = AvdApplication.getProxy(this)
        //1.我们会将原始url注册进去
        // proxy.registerCacheListener(, bean.getVideo_url());
        //2.我们播放视频的时候会调用以下代码生成proxyUrl
        val proxyUrl = proxy?.getProxyUrl(url)
        if (proxy?.isCached(url) == true) {
            LogPlus.i("aaaa", "已缓存")
        } else {
            LogPlus.i("aaaa", "未缓存")
        }
        LogPlus.i("aaaapath", proxyUrl)
        videoView.setVideoPath(proxyUrl)
        videoView.start()
        videoView.findFocus()
        videoView.setOnCompletionListener {
            finish()
        }
    }


}