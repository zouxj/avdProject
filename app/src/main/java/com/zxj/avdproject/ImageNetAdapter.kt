package com.zxj.avdproject

import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils

/**
 * 自定义布局，网络图片
 */
class ImageNetAdapter(mDatas: List<AvdDataBean>) :
    BannerAdapter<AvdDataBean, ImageHolder>(mDatas) {
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView =
            BannerUtils.getView(parent, R.layout.banner_image) as ImageView
        //通过裁剪实现圆角
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BannerUtils.setBannerRound(imageView, 0f)
        }
        return ImageHolder(imageView)
    }


    override fun onBindView(holder: ImageHolder, data: AvdDataBean, position: Int, size: Int) {
        //通过图片加载器实现圆角，你也可以自己使用圆角的imageview，实现圆角的方法很多，自己尝试哈
        Glide.with(holder.itemView)
            .load(data.imageUrl)
            .thumbnail(
                Glide.with(holder.itemView).load(R.drawable.loading)
            ) //                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
            .into(holder.imageView)
    }
}