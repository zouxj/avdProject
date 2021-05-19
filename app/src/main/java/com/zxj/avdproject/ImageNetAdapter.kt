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
import android.widget.RelativeLayout
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils.getView
import com.zxj.avdproject.comn.message.PlayManager
import com.zxj.avdproject.comn.util.LogPlus
import com.zxj.avdproject.model.Template
import com.zxj.avdproject.uitls.defaultDisplay
import com.zxj.avdproject.uitls.dip2px
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

    private val widthApp by lazy {
        context.defaultDisplay().widthPixels
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
            it.flParent?.let { it1 -> sortView(data, it1) }
        }
    }

    /**
     * 对view重新排序
     */
    private fun sortView(data: Template?, fl: RelativeLayout) {
        fl.removeAllViews()
        data?.template?.let {
            when (it.sortType) {
                1 -> {
                    //只有一张图片
                    val lp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    val img = ImageView(context).apply {
                        scaleType=ImageView.ScaleType.FIT_XY
                    }
                    fl.addView(img, lp)
                }
                2 -> {
                    //只有视频
                    val lp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    it.top?.let { url ->
                        if (mAppVideoView.mVideoView.parent != null) {
                            (mAppVideoView.mVideoView.parent as ViewGroup).removeView(mAppVideoView.mVideoView)
                        }
                        fl.addView(mAppVideoView.mVideoView, lp)
                        mAppVideoView.mVideoView.setVideoURI(Uri.parse(url))
                    }

                }
                3 -> {
                    //图片加视频
                    val lpTop = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.topHeight.toFloat())
                    )
                    lpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    val img = ImageView(context).apply {
                        scaleType=ImageView.ScaleType.FIT_XY
                    }
                    Glide.with(fl)
                        .load(it.top)
                        .thumbnail(
                            Glide.with(fl).load(R.drawable.loading)
                        )
                        .into(img)
                    fl.addView(img, lpTop)
                    //视频
                    val lpBottom = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.middleHeight.toFloat())
                    )
                    lpBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    it.middle?.let { url ->
                        if (mAppVideoView.mVideoView.parent != null) {
                            (mAppVideoView.mVideoView.parent as ViewGroup).removeView(mAppVideoView.mVideoView)
                        }
                        fl.addView(mAppVideoView.mVideoView, lpBottom)
                        mAppVideoView.mVideoView.setVideoURI(Uri.parse(url))
                    }

                }
                4 -> {
                    //视频加图片
                    //视频
                    val lpTop = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.topHeight.toFloat())
                    )
                    lpTop.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    it.middle?.let { url ->
                        if (mAppVideoView.mVideoView.parent != null) {
                            (mAppVideoView.mVideoView.parent as ViewGroup).removeView(mAppVideoView.mVideoView)
                        }
                        fl.addView(mAppVideoView.mVideoView, lpTop)
                        mAppVideoView.mVideoView.setVideoURI(Uri.parse(url))
                    }
                    //图片
                    val lpBottom = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.middleHeight.toFloat())
                    )
                    lpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    val img = ImageView(context).apply {
                        scaleType=ImageView.ScaleType.FIT_XY
                    }
                    Glide.with(fl)
                        .load(it.top)
                        .thumbnail(
                            Glide.with(fl).load(R.drawable.loading)
                        )
                        .into(img)
                    fl.addView(img, lpBottom)

                }
                5 -> {
                    //图片加视频加图片
                    val lpTop = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.topHeight.toFloat())
                    )
                    lpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    val imgTop = ImageView(context).apply {
                        scaleType=ImageView.ScaleType.FIT_XY
                    }
                    Glide.with(fl)
                        .load(it.top)
                        .thumbnail(
                            Glide.with(fl).load(R.drawable.loading)
                        )
                        .into(imgTop)
                    fl.addView(imgTop, lpTop)
                    //视频
                    val lpMiddle = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.middleHeight.toFloat())
                    )
                    lpMiddle.setMargins(0, context.dip2px(it.middleHeight.toFloat()), 0, 0)
                    it.middle?.let { url ->
                        if (mAppVideoView.mVideoView.parent != null) {
                            (mAppVideoView.mVideoView.parent as ViewGroup).removeView(mAppVideoView.mVideoView)
                        }
                        fl.addView(mAppVideoView.mVideoView, lpMiddle)
                        mAppVideoView.mVideoView.setVideoURI(Uri.parse(url))
                    }
                    //图片
                    val lpBottom = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.bottomHeight.toFloat())
                    )
                    lpBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    val imgBottom = ImageView(context).apply {
                        scaleType=ImageView.ScaleType.FIT_XY
                    }
                    Glide.with(fl)
                        .load(it.top)
                        .thumbnail(
                            Glide.with(fl).load(R.drawable.loading)
                        )
                        .into(imgBottom)
                    fl.addView(imgBottom, lpBottom)
                }

                else -> {

                }
            }
        }
    }

    class AppVideoView {
        private var mThumImg: ImageView? = null
        val mVideoView = object : VideoView(AvdApplication.getContext()) {

        }

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mVideoView.setOnInfoListener { _: MediaPlayer?, what: Int, _: Int ->
                    //播放第一帧
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) mVideoView.setBackgroundColor(
                        Color.TRANSPARENT
                    )
                    true
                }
            }
            mVideoView.setOnPreparedListener {
                mVideoView.start()
                //准备好了
                mThumImg?.visibility = View.GONE
                EventBus.getDefault().post(PlayManager(START_PLAY_STATUS))
            }
            mVideoView.setOnCompletionListener {
                //播完了
                mThumImg?.visibility = View.VISIBLE
                EventBus.getDefault().post(PlayManager(STOP_PLAY_STATUS))

            }
            mVideoView.setOnErrorListener { mp, what, extra ->
                //播放出错
                mThumImg?.visibility = View.VISIBLE
                EventBus.getDefault().post(PlayManager(ERROR_PLAY_STATUS))
                false
            }
        }

        var mVideoUrl: String? = null

        fun addVideoView(fl: RelativeLayout, videoUrl: String, thumImg: ImageView?) {
            mVideoUrl = videoUrl
            mThumImg = thumImg
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
                mVideoView.setOnInfoListener { _: MediaPlayer?, what: Int, _: Int ->
                    //播放第一帧
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) mVideoView.setBackgroundColor(
                        Color.TRANSPARENT
                    )
                    true
                }
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
            it.visibility = View.VISIBLE
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