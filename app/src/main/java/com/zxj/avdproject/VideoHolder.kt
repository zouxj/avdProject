package com.zxj.avdproject

import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class VideoHolder(view: View) : RecyclerView.ViewHolder(view) {
    var videoView: StandardGSYVideoPlayer = view as StandardGSYVideoPlayer

}