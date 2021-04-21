package com.zxj.avdproject

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils.getView

/**
 * 自定义布局，网络图片
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class ImageNetAdapter(mDatas: List<AvdDataBean>) :
    BannerAdapter<AvdDataBean, RecyclerView.ViewHolder>(mDatas) {
     val mVHMap = SparseArray<RecyclerView.ViewHolder>()

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            2 -> {
                VideoHolder(getView(parent, R.layout.banner_video) as StandardGSYVideoPlayer)

            }
            else -> {
                ImageHolder(getView(parent, R.layout.banner_image) as ImageView)
            }


        }
    }


    override fun onBindView(
        holder: RecyclerView.ViewHolder?,
        data: AvdDataBean?,
        position: Int,
        size: Int
    ) {
        when (holder?.itemViewType) {

            2 -> {
                (holder as? VideoHolder)?.let {
                    it.videoView.setUp(data?.videoUrl, true, null)
                    it.videoView.backButton.visibility = View.GONE
                    it.videoView.startButton.visibility = VideoView.GONE
                    mVHMap.append(position, it)

                }
            }
            else -> {
                (holder as? ImageHolder)?.let {
                    mVHMap.append(position, it)
                    Glide.with(it.itemView)
                        .load(data?.imageUrl)
                        .thumbnail(
                            Glide.with(holder.itemView).load(R.drawable.loading)
                        )
                        .into(it.imageView)
                }

            }
        }
        //通过图片加载器实现圆角，你也可以自己使用圆角的imageview，实现圆角的方法很多，自己尝试哈


    }


    override fun getItemViewType(position: Int): Int {
        return getData(getRealPosition(position)).viewType
    }
}