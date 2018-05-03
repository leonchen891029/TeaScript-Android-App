package com.teacore.teascript.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.teacore.teascript.R;
import com.teacore.teascript.bean.Banner;
import com.teacore.teascript.module.general.detail.activity.EventDetailActivity;

import net.qiujuer.genius.blur.StackBlur;

/**
 * Event的BannerView
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-15
 */

public class EventBannerView extends RelativeLayout implements View.OnClickListener{

    private Banner banner;
    private ImageView eventBgIV,eventImgIV;
    private TextView eventTitleTV,eventBodyTV;
    private Context mContext;

    public EventBannerView(Context context){
        super(context,null);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_event_banner,this,true);
        eventBgIV=(ImageView) findViewById(R.id.event_banner_bg_iv);
        eventImgIV=(ImageView) findViewById(R.id.event_banner_img_iv);
        eventTitleTV=(TextView) findViewById(R.id.event_banner_title_tv);
        eventBodyTV=(TextView) findViewById(R.id.event_banner_body_tv);
        setOnClickListener(this);
    }

    public void initData(RequestManager manager,Banner banner){
        this.banner=banner;
        eventTitleTV.setText(banner.getName());
        eventBodyTV.setText(banner.getDetail());
        manager.load(banner.getImg()).into(eventImgIV);
        manager.load(banner.getImg()).centerCrop()
                .transform(new BitmapTransformation(getContext()) {
                    @Override
                    protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
                        bitmap= StackBlur.blur(bitmap,25,true);
                        return bitmap;
                    }

                    @Override
                    public String getId() {
                        return "blur";
                    }
                }).into(eventBgIV);
    }

    @Override
    public void onClick(View v){

        if (banner != null) {

            long dataId = banner.getDataId();

            EventDetailActivity.show(mContext,dataId);
        }
    }



}
