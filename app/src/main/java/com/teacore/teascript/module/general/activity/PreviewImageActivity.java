package com.teacore.teascript.module.general.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.module.quickoption.activity.TeatimePubActivity;
import com.teacore.teascript.widget.dialog.ShareMenuDialog;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.TouchImageView;

import java.io.IOException;

/**
 * 图片预览界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-17
 */

public class PreviewImageActivity extends BaseActivity{

    public static final String BUNDLE_KEY_IMAGES="bundle_key_images";

    private TouchImageView mTouchImageView;
    private ProgressBar mProgressBar;
    private ImageView mOptionIV;
    private AsyncTask<Void,Void,Bitmap> mTask;
    private String mImageUrl;

    public static void showImagePreview(Context context,String imageUrl){
        Intent intent=new Intent(context,PreviewImageActivity.class);
        intent.putExtra(BUNDLE_KEY_IMAGES,imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        mImageUrl=getIntent().getStringExtra(BUNDLE_KEY_IMAGES);

        mTouchImageView=(TouchImageView) findViewById(R.id.image_tiv);

        mTouchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressBar=(ProgressBar) findViewById(R.id.loading_pb);

        mOptionIV=(ImageView) findViewById(R.id.more_iv);
        mOptionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoOptionMenu();
            }
        });

        loadImage(mTouchImageView,mImageUrl);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

    }

    private void shoOptionMenu(){
        final ShareMenuDialog dialog=new ShareMenuDialog(this);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setOnMenuClickListener(new ShareMenuDialog.OnMenuClickListener(){
            @Override
            public void onClick(TextView menuItem) {
                if (menuItem.getId() == R.id.menu1) {
                    saveImg();
                } else if (menuItem.getId() == R.id.menu2) {
                    sendTeatime();
                } else if (menuItem.getId() == R.id.menu3) {
                    copyUrl();
                }
                dialog.dismiss();
            }
        });
    }

    //发送到动弹
    private void sendTeatime() {
        Bundle bundle = new Bundle();
        bundle.putString(TeatimePubActivity.REPOST_IMAGE_KEY, mImageUrl);
        UiUtils.showTeatimeActivity(this, TeatimePubActivity.ACTION_TYPE_REPOST, bundle);
        finish();
    }


    //复制链接
    private void copyUrl() {
        TDevice.copyTextToClipboard(mImageUrl);
        AppContext.showToast("已复制到剪贴板");
    }

    //保存图片
    private void saveImg(){
        final String filePath= AppConfig.DEFAULT_SAVE_IMAGE_PATH+getFileName(mImageUrl);

        Drawable drawable=mTouchImageView.getDrawable();

        if(drawable !=null && drawable instanceof BitmapDrawable){
            Bitmap bitmap=((BitmapDrawable) drawable).getBitmap();
            try{
                ImageUtils.saveImageToSD(this,filePath,bitmap,100);
                AppContext.getInstance().showToast(getString(R.string.tip_save_image_success));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String getFileName(String imgUrl) {
        int index = imgUrl.lastIndexOf('/') + 1;
        if (index == -1) {
            return System.currentTimeMillis() + ".jpeg";
        }
        return imgUrl.substring(index);
    }

    private void loadImage(final ImageView mHeaderImageView,final String imageUrl){
        Glide.with(this).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                mHeaderImageView.setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
                mHeaderImageView.setVisibility(View.VISIBLE);
                mOptionIV.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }


}
