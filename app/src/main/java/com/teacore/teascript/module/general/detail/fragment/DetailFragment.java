package com.teacore.teascript.module.general.detail.fragment;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.R;
import com.teacore.teascript.module.general.base.basefragment.BaseFragment;
import com.teacore.teascript.module.general.detail.constract.DetailContract;
import com.teacore.teascript.module.general.widget.TWebView;

/**
 * DetailFragment基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-23
 */

public abstract class DetailFragment<Data,DataView extends DetailContract.BaseView,Presenter extends DetailContract.BasePresenter<Data,DataView>>
       extends BaseFragment implements DetailContract.BaseView{

    //获取Presenter
    Presenter mPresenter;
    //获取webview
    TWebView mWebView;
    private NestedScrollView mScrollView;
    private View mTargetView;

    public Presenter getPresenter(){
        return mPresenter;
    }

    @Override
    public void onAttach(Context context){
        this.mPresenter=(Presenter) context;

        this.mPresenter.setDataView((DataView) this);

        super.onAttach(context);
    }

    @Override
    protected void initView(View view){

        super.initView(view);

        initWebView(R.id.webview_fl);

    }

    //在webview_fl中添加webview
    void initWebView(int layId){

        TWebView webView=new TWebView(getActivity());

        ((ViewGroup) mRootView.findViewById(layId)).addView(webView);

        mWebView=webView;
    }

    //加载返回的数据主体
    void setBodyContent(String bodyContent){
        mWebView.loadDetailDataAsync(bodyContent, new Runnable() {
            @Override
            public void run() {
                Presenter operator = mPresenter;
                if (operator != null) {
                    operator.hideLoading();
                }
            }
        });
    }

    void setCommentCount(int count) {
        if (mPresenter != null) {
            mPresenter.setCommentCount(count);
        }
    }

    void registerScroller(NestedScrollView nestedScrollView,View targetView){
        mScrollView=nestedScrollView;
        mTargetView=targetView;
    }

    private int mScrollYPoint=-1;

    @Override
    public void scrollToComment(){

        if(mScrollView!=null && mTargetView!=null){
            int curY = mScrollView.getScrollY();
            int targetY = mTargetView.getTop();
            if (targetY > 0) {
                if (curY == targetY && targetY == mScrollYPoint) {
                    mScrollView.fullScroll(View.FOCUS_UP);
                } else {
                    if (mScrollYPoint == -1) {
                        mScrollView.smoothScrollTo(0, targetY);
                        mScrollYPoint = curY;
                        return;
                    }
                    if (curY > targetY) {
                        // 当前在评论之后
                        if (mScrollYPoint < targetY) {
                            mScrollView.smoothScrollTo(0, mScrollYPoint);
                        } else {
                            mScrollView.fullScroll(View.FOCUS_UP);
                        }
                        mScrollYPoint = curY;
                    } else {
                        // 当前在评论之前
                        mScrollView.smoothScrollTo(0, mScrollYPoint);
                        if (mScrollYPoint < curY) {
                            mScrollYPoint = -1;
                        } else {
                            mScrollYPoint = 0;
                        }
                    }
                }
            } else {
                if (mScrollYPoint == -1) {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    mScrollYPoint = curY;
                } else {
                    mScrollView.smoothScrollTo(0, mScrollYPoint);
                    mScrollYPoint = -1;
                }
            }

        }

    }

    @Override
    public void onResume(){
        super.onResume();;
        if(mWebView!=null){
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }

        mTargetView = null;
        if (mScrollView != null) {
            mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
            mScrollView = null;
        }

        mPresenter = null;

        super.onDestroy();
    }

}
