package com.teacore.teascript.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.util.TDevice;
import com.wang.avi.AVLoadingIndicatorView;

/**显示加载中错误的布局类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */
public class EmptyLayout extends LinearLayout implements android.view.View.OnClickListener{

    //empty类型
    public static final int NETWORK_ERROR=1;
    public static final int NETWORK_LOADING=2;
    public static final int NODATA=3;
    public static final int HIDE_LAYOUT=4;
    public static final int NODATA_ENABLE_CLICK=5;
    public static final int NO_LOGIN=6;
    //Loading类
    private AVLoadingIndicatorView mIndicatorView;

    private boolean clickEnable=true;
    private final Context context;
    private android.view.View.OnClickListener listener;
    private int emptyState;
    private String strNoDataContent="";

    private TextView tv;
    public ImageView img;

    public EmptyLayout(Context context){
        super(context);
        this.context=context;
        init();
    }

    public EmptyLayout(Context context,AttributeSet attributes){
        super(context, attributes);
        this.context=context;
        init();
    }

    private void init(){

        View view=View.inflate(context,R.layout.view_empty_layout,null);

        img=(ImageView) view.findViewById(R.id.empty_layout_iv);
        tv=(TextView) view.findViewById(R.id.empty_layout_tv);

        RelativeLayout mLayout=(RelativeLayout) view.findViewById(R.id.empty_layout_rl);

        mIndicatorView=(AVLoadingIndicatorView) view.findViewById(R.id.indicatorView);

        setBackgroundColor(-1);

        setOnClickListener(this);

        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (clickEnable) {
                    if (listener != null)
                        listener.onClick(v);
                }
            }
        });

        addView(view);

        changeEmptyLayoutBgMode(context);

    }

    public void changeEmptyLayoutBgMode(Context context){
        //
    }

    public void dismiss(){
        emptyState=HIDE_LAYOUT;
        setVisibility(View.GONE);
    }

    public int getEmptyState(){
        return emptyState;
    }

    public boolean isLoadError(){
        return emptyState==NETWORK_ERROR;
    }

    public boolean isLoading(){
        return emptyState==NETWORK_LOADING;
    }

    @Override
    public void onClick(View view) {
        if (clickEnable) {
            if (listener != null)
                listener.onClick(view);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onSkinChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onSkinChanged() {
    }

    public void setEmptyMessage(String msg) {
        tv.setText(msg);
    }

    //新添图片
    public void setEmptyImage(int imgResource) {
        try {
            img.setImageResource(imgResource);
        } catch (Exception e) {
        }
    }

    //设置Empty类型
    public void setEmptyType(int i) {
        //设置EmptyLayout为可见
        setVisibility(View.VISIBLE);
        switch (i) {
            case NETWORK_ERROR:
                emptyState = NETWORK_ERROR;
                if (TDevice.hasInternet()) {
                    tv.setText(R.string.error_view_load_error_click_to_refresh);
                    img.setBackgroundResource(R.drawable.pagefailed_bg);
                } else {
                    tv.setText(R.string.error_view_network_error_click_to_refresh);
                    img.setBackgroundResource(R.drawable.page_icon_network);
                }
                img.setVisibility(View.VISIBLE);
                mIndicatorView.hide();
                mIndicatorView.setVisibility(View.GONE);
                clickEnable = true;
                break;
            case NETWORK_LOADING:
                emptyState = NETWORK_LOADING;
                mIndicatorView.setVisibility(View.VISIBLE);
                mIndicatorView.show();
                img.setVisibility(View.GONE);
                tv.setText(R.string.error_view_loading);
                clickEnable = false;
                break;
            case NODATA:
                emptyState = NODATA;
                img.setBackgroundResource(R.drawable.page_icon_empty);
                img.setVisibility(View.VISIBLE);
                mIndicatorView.hide();
                mIndicatorView.setVisibility(View.GONE);
                setTvNoDataContent();
                clickEnable = true;
                break;
            case HIDE_LAYOUT:
                mIndicatorView.hide();
                setVisibility(View.GONE);
                break;
            case NODATA_ENABLE_CLICK:
                emptyState = NODATA_ENABLE_CLICK;
                img.setBackgroundResource(R.drawable.page_icon_empty);
                img.setVisibility(View.VISIBLE);
                mIndicatorView.hide();
                mIndicatorView.setVisibility(View.GONE);
                setTvNoDataContent();
                clickEnable = true;
                break;
            default:
                break;
        }
    }

    //设置无数据时的字符串
    public void setNoDataContent(String noDataContent) {
        strNoDataContent = noDataContent;
    }

    //设置该layout的监听器
    public void setOnLayoutClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //显示无数据时的提示
    public void setTvNoDataContent() {
        if (!strNoDataContent.equals(""))
            tv.setText(strNoDataContent);
        else
            tv.setText(R.string.error_view_no_data);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            emptyState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }


}
