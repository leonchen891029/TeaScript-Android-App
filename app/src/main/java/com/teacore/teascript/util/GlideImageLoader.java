package com.teacore.teascript.util;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.teacore.teascript.widget.CircleImageView;

/**
 * Glide图片加载工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public class GlideImageLoader {

    private GlideImageLoader() {
    }

    public static void loadImage(RequestManager loader, ImageView view, String url) {
        loadImage(loader, view, url, 0);
    }

    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder) {
        boolean isCenterCrop = false;
        if (view instanceof CircleImageView)
            isCenterCrop = true;
        loadImage(loader, view, url, placeholder, placeholder, isCenterCrop);
    }

    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder, int error, boolean isCenterCrop) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(placeholder);
        } else {
            if (view instanceof CircleImageView) {

                BitmapRequestBuilder builder = loader.load(url).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(placeholder)
                        .error(error);
                if (isCenterCrop)
                    builder.centerCrop();

                builder.into(
                        new BitmapImageViewTarget(view) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(view.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                view.setImageDrawable(circularBitmapDrawable);
                            }
                        });

            } else {
                DrawableRequestBuilder builder = loader.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).error(error);
                if (isCenterCrop)
                    builder.centerCrop();
                builder.into(view);
            }
        }
    }

    public static void loadImage(RequestManager loader, ImageView view, String url,int width,int height,int placeholder, int error, boolean isCenterCrop) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(placeholder);
        } else {
            if (view instanceof CircleImageView) {
                BitmapRequestBuilder builder = loader.load(url).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(width,height)
                        .placeholder(placeholder)
                        .error(error);
                if (isCenterCrop)
                    builder.centerCrop();

                builder.into(
                        new BitmapImageViewTarget(view) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(view.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                view.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } else {
                DrawableRequestBuilder builder = loader.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).error(error);
                if (isCenterCrop)
                    builder.centerCrop();
                builder.into(view);
            }
        }
    }
}
