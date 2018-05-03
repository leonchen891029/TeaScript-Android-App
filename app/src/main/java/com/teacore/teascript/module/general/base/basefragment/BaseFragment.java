package com.teacore.teascript.module.general.base.basefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.util.GlideImageLoader;

import java.io.Serializable;

/**
 * 综合下的Fragment基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public abstract class  BaseFragment extends Fragment {

    protected View mRootView;

    protected Bundle mBundle;
    //Glide图片加载器
    private RequestManager mImageLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBundle=getArguments();
        initBundle(mBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        //如果mRootView不为null，删除mRootView，如果为null，初始化mRootView
        if(mRootView!=null){
            ViewGroup parent=(ViewGroup) mRootView.getParent();
            if(parent!=null)
                parent.removeView(mRootView);
        }else{
            mRootView=inflater.inflate(getLayoutId(),container,false);
            initView(mRootView);
            initData();
        }

        return mRootView;
    }

    //获取Fragment相应的layout id
    protected abstract int getLayoutId();

    //初始化Bundle
    protected void initBundle(Bundle bundle) {

    }

    //初始化mRootView
    protected void initView(View rootView) {

    }

    //初始化数据
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestManager manager = mImageLoader;
        if (manager != null)
            manager.onDestroy();
        mImageLoader = null;
        mBundle = null;
    }

    //获取Bundle中相应Key的值
    protected <T extends Serializable> T getBundleSerializable(String key) {
        if (mBundle == null) {
            return null;
        }
        return (T) mBundle.getSerializable(key);
    }

    //获取一个特定viewId的View对象
    protected <T extends View> T findView(int viewId){
        return (T) mRootView.findViewById(viewId);
    }

    //获取一个图片加载管理器
    public synchronized RequestManager getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = Glide.with(this);
        return mImageLoader;
    }

    /*从网络中加载图片到View中
    *@param viewId view的ID
    *@param imageUrl 图片的地址
    */
    protected void setImageFromNet(int viewId, String imageUrl) {
        setImageFromNet(viewId, imageUrl, 0);
    }

    protected void setImageFromNet(int viewId, String imageUrl, int placeholder) {
        ImageView imageView = findView(viewId);
        setImageFromNet(imageView, imageUrl, placeholder);
    }

    protected void setImageFromNet(ImageView imageView, String imageUrl) {
        setImageFromNet(imageView, imageUrl, 0);
    }

    protected void setImageFromNet(ImageView imageView, String imageUrl, int placeholder) {
        GlideImageLoader.loadImage(getImageLoader(), imageView, imageUrl, placeholder);
    }

    protected void setText(int viewId, String text) {
        TextView textView = findView(viewId);
        if (TextUtils.isEmpty(text)) {
            return;
        }
        textView.setText(text);
    }

    protected void setText(int viewId, String text, String emptyTip) {
        TextView textView = findView(viewId);
        if (TextUtils.isEmpty(text)) {
            textView.setText(emptyTip);
            return;
        }
        textView.setText(text);
    }

    protected void setTextEmptyGone(int viewId, String text) {
        TextView textView = findView(viewId);
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setText(text);
    }

    protected void setGone(int id) {
        findView(id).setVisibility(View.GONE);
    }

    protected void setVisibility(int id) {
        findView(id).setVisibility(View.VISIBLE);
    }

    protected void setInVisibility(int id) {
        findView(id).setVisibility(View.INVISIBLE);
    }


}
