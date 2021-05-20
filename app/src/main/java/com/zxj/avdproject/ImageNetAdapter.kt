package com.zxj.avdproject

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.zxj.avdproject.uitls.SSlUtiles
import com.zxj.avdproject.uitls.SSlUtiles.TrustAllHostnameVerifier
import com.zxj.avdproject.uitls.defaultDisplay
import com.zxj.avdproject.uitls.dip2px
import com.zxj.avdproject.widgt.FullScreenVideoView
import org.greenrobot.eventbus.EventBus
import javax.net.ssl.HttpsURLConnection


/**
 * 自定义布局，网络图片
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class ImageNetAdapter(val context: Context, mDatas: List<Template>) :
    BannerAdapter<Template, RecyclerView.ViewHolder>(mDatas), LifecycleEventObserver {

    private val mAppVideoView: AppVideoView by lazy {
        AppVideoView(context)
    }
    private  val videoFour=   AppVideoView(context)
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
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                    it.top?.let { it1 -> loadImg(img, it1) }
                    fl.addView(img, lp)
                }
                2 -> {
                    //只有视频
                    val lp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    it.top?.let { url ->
                        mAppVideoView.addVideoPlay(fl,lp,url)

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
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                    it.top?.let { it1 -> loadImg(img, it1) }

                    fl.addView(img, lpTop)
                    //视频
                    val lpBottom = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.bottomHeight.toFloat())
                    )
                    lpBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    it.bottom?.let { url ->
                        mAppVideoView.addVideoPlay(fl,lpBottom,url)
                    }

                }
                4 -> {
                    //视频加图片
                    //视频
                    val lp = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.topHeight.toFloat())
                    )
//                    val video= FullScreenVideoView(context)
//                    video.setVideoURI(Uri.parse( it.top))
//                    fl.addView(video, lp)
//                    video.setOnPreparedListener {
//                        video.start()
//                    }

                    it.top?.let { url ->
                        videoFour.addVideoPlay(fl,lp,url)
                    }
//                    图片
                    val lpBottom = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.bottomHeight.toFloat())
                    )
                    lpBottom.setMargins(0,it.topHeight,0,0)
                    val img = ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                    it.bottom?.let { it1 -> loadImg(img, it1) }
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
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                    it.top?.let { it1 -> loadImg(imgTop, it1) }
                    fl.addView(imgTop, lpTop)
                    //视频
                    val lpMiddle = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.middleHeight.toFloat())
                    )

                    lpMiddle.setMargins(0, context.dip2px(it.topHeight.toFloat()), 0, 0)
                    it.middle?.let { url ->
                        mAppVideoView.addVideoPlay(fl,lpMiddle,url)

                    }
                    //图片
                    val lpBottom = RelativeLayout.LayoutParams(
                        widthApp,
                        context.dip2px(it.bottomHeight.toFloat())
                    )
                    lpBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    val imgBottom = ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                    it.bottom?.let { it1 -> loadImg(imgBottom, it1) }
                    fl.addView(imgBottom, lpBottom)
                }

                else -> {

                }
            }
        }
    }

    class AppVideoView constructor(val context: Context) {
        var mVideoUrl:String?=null
        val mVideoFl: View by lazy {
                LayoutInflater.from(context).inflate(R.layout.video_view, null, false)
        }

        private  val mThumImg :ImageView by lazy {
            mVideoFl.findViewById<ImageView>(R.id.img_video)
        }
        val mVideoView: VideoView by lazy {
            mVideoFl.findViewById<VideoView>(R.id.video)
        }
      fun  addVideoPlay(fl: RelativeLayout,lp:RelativeLayout.LayoutParams,url: String){
          mVideoUrl=null
          if (mVideoFl.parent != null) {
              (mVideoFl.parent as ViewGroup).removeView(mVideoFl)
          }
          loadCover(mThumImg,url,context)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
              mVideoView.setOnInfoListener { _: MediaPlayer?, what: Int, _: Int ->
                  //播放第一帧
                  if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) mVideoView?.setBackgroundColor(
                      Color.TRANSPARENT
                  )
                  true
              }
          }

          mVideoView.stopPlayback()
          mVideoView.setVideoURI(Uri.parse(url))
          fl.addView(mVideoFl, lp)
        }
        init {
            mVideoView.setOnPreparedListener {
                mVideoView.start()
                //准备好了
                mThumImg.visibility = View.GONE
                EventBus.getDefault().post(PlayManager(START_PLAY_STATUS))
            }
            mVideoView.setOnCompletionListener {
                //播完了
                mThumImg.visibility = View.VISIBLE
                EventBus.getDefault().post(PlayManager(STOP_PLAY_STATUS))

            }
            mVideoView.setOnErrorListener { mp, what, extra ->
                //播放出错
                mThumImg.visibility = View.VISIBLE
                EventBus.getDefault().post(PlayManager(ERROR_PLAY_STATUS))
                false
            }
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

            else -> {}
        }
        LogPlus.i("onStateChanged", "event===>" + event)
    }
    private fun loadImg(img:ImageView, imgUrl:String){
        Glide.with(context)
            .load(imgUrl)
            .thumbnail(
                Glide.with(context).load(R.drawable.loading)
            )
            .into(img)
    }

}