package com.zxj.avdproject
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils.getView
import com.zxj.avdproject.comn.message.PlayManager
import com.zxj.avdproject.model.Template
import org.greenrobot.eventbus.EventBus


/**
 * 自定义布局，网络图片
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class ImageNetAdapter(mDatas: List<Template>) :
    BannerAdapter<Template, RecyclerView.ViewHolder>(mDatas) {

    private val mAppVideoView: AppVideoView by lazy {
        AppVideoView()
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
                if (data?.template?.video?.length ?: 0 > 0) {
                    mAppVideoView.addVideoView(it.mVideoView!!, data?.template?.video!!)
                } else {
                    it.mVideoView?.removeAllViews()
                    mAppVideoView.mVideoView.stopPlayback()
                }
            }


        }
    }

    class AppVideoView {

        val mVideoView= object : VideoView(AvdApplication.getContext()) {
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

        var mVideoUrl = ""

        fun addVideoView(fl: FrameLayout, videoUrl: String) {
            fl.removeAllViews()
            mVideoView.setOnPreparedListener {
                mVideoView.start()
                //准备好了
                EventBus.getDefault().post(PlayManager(START_PLAY_STATUS))
            }
            mVideoView.setOnCompletionListener {
                //播完了
                EventBus.getDefault().post(PlayManager(STOP_PLAY_STATUS))

            }
            mVideoView.setOnErrorListener { mp, what, extra ->
                //播放出错
                EventBus.getDefault().post(PlayManager(ERROR_PLAY_STATUS))
                false
            }
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER
            if (mVideoView.parent!=null){
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
}