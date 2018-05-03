package com.teacore.teascript.module.general.base.baseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.RequestManager;
import com.teacore.teascript.module.general.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 综合下的BaseListAdapter
 * @author  陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public  abstract class BaseListAdapter<T> extends BaseAdapter implements ViewHolder.Callback{

    protected LayoutInflater mInflater;
    protected Callback mCallback;
    private List<T> mDatas;

    //构造函数中传入实现了BaseListAdapter.Callback接口的类
    public BaseListAdapter(Callback callback){
        this.mCallback=callback;
        this.mInflater=LayoutInflater.from(callback.getContext());
        this.mDatas=new ArrayList<T>();
    }

    @Override
    public int getCount() {
        return mDatas .size();
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && position < mDatas.size())
            return mDatas.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getDatas(){
        return mDatas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        T item = getItem(position);

        int layoutId = getLayoutId(position, item);

        final ViewHolder vh = ViewHolder.getViewHolder(this, convertView, parent, layoutId, position);

        convert(vh, item, position);

        return vh.getConvertView();
    }

    protected abstract void convert(ViewHolder vh, T item, int position);

    protected abstract int getLayoutId(int position, T item);

    public void updateItem(int location, T item) {
        if (mDatas.isEmpty()) return;
        mDatas.set(location, item);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        checkListNull();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        checkListNull();
        mDatas.add(location, item);
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        checkListNull();
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(int position, List<T> items) {
        checkListNull();
        mDatas.addAll(position, items);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
    }

    public int getCurrentPage() {
        return getCount() % 20;
    }

    //实现ViewHolder.Callback的getImageLoader()方法
    @Override
    public RequestManager getImageLoader() {
        return mCallback.getImageLoader();
    }

    //实现ViewHolder.Callback的getInflater方法
    @Override
    public LayoutInflater getInflater() {
        return mInflater;
    }

    public interface Callback {

        RequestManager getImageLoader();

        Context getContext();

        Date getSystemTime();
    }


}
