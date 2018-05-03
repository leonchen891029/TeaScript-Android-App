package com.teacore.teascript.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.bean.Banner;
import com.teacore.teascript.module.general.detail.activity.NewsDetailActivity;

/**
 * 新闻的BannerView控件
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class NewsBannerView extends RelativeLayout implements View.OnClickListener{

    private Banner banner;
    private ImageView banner_iv;
    private Context mContext;

    public NewsBannerView(Context context){
        super(context,null);
        mContext=context;
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_news_banner, this, true);
        banner_iv = (ImageView) findViewById(R.id.banner_iv);
        setOnClickListener(this);
    }

    public void initData(RequestManager requestManager,Banner banner){

        this.banner=banner;

        requestManager.load(banner.getImg()).into(banner_iv);
    }


    @Override
    public void onClick(View v) {

        if (banner != null) {

            long dataId = banner.getDataId();

            NewsDetailActivity.show(mContext,dataId);
        }

    }

    public String getTitle() {
        return banner.getName();
    }

}
