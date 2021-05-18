package com.zxj.avdproject

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils.getView
import com.youth.banner.util.LogUtils
import com.zxj.avdproject.comn.message.PlayManager
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.model.Template
import org.greenrobot.eventbus.EventBus


/**
 * 自定义布局，网络图片
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class ImageNetAdapter(val context: Context, mDatas: List<Template>) :
    BannerAdapter<Template, RecyclerView.ViewHolder>(mDatas), LifecycleEventObserver {

    private val mAppVideoView: AppVideoView by lazy {
        AppVideoView()
    }

    init {
        (context as? MainActivity)?.lifecycle?.addObserver(this)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageHolder(getView(parent, R.layout.banner_image))

    }

    override fun onBindView(
        holder: RecyclerView.ViewHolder?,
        data: Template?,
        position: Int,
        size: Int
    ) {
        (holder as? ImageHolder)?.let {
            it.imageView?.let { it1 ->
                Glide.with(it.itemView)
                    .load(data?.template?.img)
                    .thumbnail(
                        Glide.with(holder.itemView).load(R.drawable.loading)
                    )
                    .into(it1)
                loadCover(it.thumbImg,data?.template?.video,context)
                if (data?.template?.video?.length ?: 0 > 0) {
                    mAppVideoView.addVideoView(it.mVideoView!!, data?.template?.video!!,it.thumbImg)
                } else {
                    it.mVideoView?.removeAllViews()
                    mAppVideoView.mVideoView.stopPlayback()
                }
            }


        }
    }

    class AppVideoView {

        val mVideoView = object : VideoView(AvdApplication.getContext()) {
            override fun onMeasure(
                widthMeasureSpec: Int,
                heightMeasureSpec: Int
            ) {
                if (true) setMeasuredDimension(
                    widthMeasureSpec,
                    heightMeasureSpec
                ) else super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
        var mVideoUrl: String? = null

        fun addVideoView(fl: FrameLayout, videoUrl: String,thumImg:ImageView?) {
            mVideoUrl = videoUrl
            fl.removeAllViews()
            mVideoView.setOnPreparedListener {
                mVideoView.start()
                //准备好了
                thumImg?.visibility=View.GONE
                EventBus.getDefault().post(PlayManager(START_PLAY_STATUS))
            }
            mVideoView.setOnCompletionListener {
                //播完了
                thumImg?.visibility=View.VISIBLE
                EventBus.getDefault().post(PlayManager(STOP_PLAY_STATUS))

            }
            mVideoView.setOnErrorListener { mp, what, extra ->
                //播放出错
                thumImg?.visibility=View.VISIBLE
                EventBus.getDefault().post(PlayManager(ERROR_PLAY_STATUS))
                false
            }
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER
            if (mVideoView.parent != null) {
                (mVideoView.parent as ViewGroup).removeView(mVideoView)
            }
            fl.addView(mVideoView, params)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mVideoView.setOnInfoListener(MediaPlayer.OnInfoListener { mp: MediaPlayer?, what: Int, extra: Int ->
                    //播放第一帧
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) mVideoView!!.setBackgroundColor(
                        Color.TRANSPARENT
                    )
                    true
                })
            }
            mVideoView.setVideoURI(Uri.parse(videoUrl))


        }

    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                mAppVideoView.mVideoView.stopPlayback()
                mAppVideoView.mVideoUrl = null
            }
            Lifecycle.Event.ON_PAUSE -> {
                mAppVideoView.mVideoView.pause()
            }
            Lifecycle.Event.ON_RESUME -> {
                mAppVideoView.mVideoUrl?.let {
                    mAppVideoView.mVideoView.start()
                }
            }

        }
        LogPlus.i("onStateChanged", "event===>" + event)
    }

    fun loadCover(
        imageView: ImageView?,
        url: String?,
        context: Context?
    ) {
        imageView?.let {
            it.visibility=View.VISIBLE
            it.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context!!)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .frame(1000000)
                        .centerCrop()
                )
                .load(url).into(it)
        }
    }
}