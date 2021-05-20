package com.zxj.avdproject.widgt;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.zxj.avdproject.uitls.SSlUtiles;

import javax.net.ssl.HttpsURLConnection;

public class FullScreenVideoView extends VideoView {


    public FullScreenVideoView(Context context) {
        super(context);
    }
    public FullScreenVideoView (Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }
    public FullScreenVideoView(Context context, AttributeSet attrs,int defStyle)
    {
        super(context,attrs,defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width , height);
    }

    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
        HttpsURLConnection.setDefaultSSLSocketFactory(SSlUtiles.createSSLSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new SSlUtiles.TrustAllHostnameVerifier());
    }
}

