package com.zxj.avdproject

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView? =view.findViewById(R.id.image_one)
    val mVideoView: FrameLayout? =view.findViewById(R.id.video_view)
    val thumbImg: ImageView? =view.findViewById(R.id.iv_thum_img)

}