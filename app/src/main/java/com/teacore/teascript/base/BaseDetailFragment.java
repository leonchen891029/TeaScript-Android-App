package com.teacore.teascript.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Report;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.widget.dialog.ReportDialog;
import com.teacore.teascript.widget.dialog.ShareDialog;
import com.teacore.teascript.widget.emoji.OnSendClickListener;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.FontSizeUtils;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import cz.msebera.android.httpclient.Header;

import static com.teacore.teascript.util.UiUtils.showLoginActivity;

/**详情BaseFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-6
 */
public abstract class BaseDetailFragment <T extends Serializable> extends BaseFragment
        implements OnSendClickListener {

    protected int mId;

    protected EmptyLayout mEmptyLayout;

    protected int mCommentCount=0;

    protected WebView mWebView;

    protected T mDetail;

    private AsyncTask<String,Void,T> mCacheTask;

    private ShareDialog shareDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view= inflater.inflate(getLayoutId(),container,false);

        //获取评论数
        mCommentCount=getActivity().getIntent().getIntExtra("comment_count",0);
        //获取Activity的id值
        mId=getActivity().getIntent().getIntExtra("id",0);

        initView(view);

        initData();

        requestData(false);

        return view;
    }

    @Override
    public void initView(View view){
        mEmptyLayout=(EmptyLayout) view.findViewById(R.id.empty_layout);

        //设置DetailActivity中的toolbarFragment的评论数
        setCommentCount(mCommentCount);


        mWebView=(WebView) view.findViewById(R.id.fragment_detail_webview);
        //初始化webview
        UiUtils.initWebView(mWebView);
    }

    protected void setCommentCount(int commentCount){

        ((DetailActivity) getActivity()).toolbarFragment.setCommentCount(commentCount);

    }

    //是否从网络或者缓存中读取数据
    private void requestData(boolean refresh){
        String key=getCacheKey();

        //如果有网络连接并且不存在相应的key值缓存 或者 refresh为true 从网络加载数据
        if(TDevice.hasInternet() && (!CacheManager.isCacheExist(getActivity(),key) || refresh)){
            sendRequestDataForNet();
        }else{
            readCacheData(key);
        }
    }

    //获取缓存的key
    protected abstract String getCacheKey();

    //从网络中读取数据
    protected abstract void sendRequestDataForNet();

    //读取缓存数据
    private void readCacheData(String cacheKey){
        cancelReadCache();
        mCacheTask=new CacheTask(getActivity()).execute(cacheKey);
    }

    //取消读取缓存
    private void cancelReadCache(){
        if(mCacheTask!=null){
            mCacheTask.cancel(true);
            mCacheTask=null;
        }
    }

    @Override
    public void onDestroyView(){
        recycleWebView();
        shareDialog=null;
        super.onDestroyView();
    }

    //回收相应的webview
    private void recycleWebView(){
        if(mWebView!=null){
         mWebView.setVisibility(View.GONE);
         mWebView.removeAllViews();
         mWebView.destroy();
         mWebView=null;
        }
    }

    //请求具体数据的AsyncHttpResponseHandler
    protected AsyncHttpResponseHandler mDetailHandler=new AsyncHttpResponseHandler(){
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2){
            try{
                //解析字节数组
                T detail=parseData(new ByteArrayInputStream(arg2));

                if(detail != null){
                    mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);

                    //数据加载成功以后，初始化webview和toolbarfragment
                    executeOnLoadDataSuccess(detail);

                    //将detail保存到缓存中
                    saveCache(detail);
                }else{
                    //数据加载失败，初始化EmptyLayout
                    executeOnLoadDataError();
                }
            }catch (Exception e){
                e.printStackTrace();
                executeOnLoadDataError();
            }
        }

        //AsyncHttpResponseHandler开始请求时，初始化EmptyLayout
        @Override
        public void onStart(){
            super.onStart();
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        }

        //如果网络请求失败，尝试请求缓存中的数据
        @Override
        public void onFailure(int arg0,Header[] arg1,byte[] arg2,Throwable arg3){
            readCacheData(getCacheKey());
        }
    };

    //解析数据
    protected abstract  T parseData(InputStream is);

    protected void executeOnLoadDataSuccess(T detail){
        this.mDetail=detail;

        //判断detail是否为null或者webview内容是否为空
        if(mDetail==null || TextUtils.isEmpty(getWebViewBody(detail))){
            executeOnLoadDataError();
            return;
        }
        //如果mWebView不为null
        if(mWebView!=null){
            //加载webview的内容
            mWebView.loadDataWithBaseURL("",getWebViewBody(detail),"text/html","UTF-8","");
            //显示存储的字体大小
            mWebView.loadUrl(FontSizeUtils.getSaveFontSize());
            //获取收藏的状态
            boolean favoriteState=getFavoriteState()==1;
            setFavoriteState(favoriteState);
        }
        //判断最新的评论数是否大于评论总数
        if(getCommentCount()>mCommentCount){
            mCommentCount=getCommentCount();
        }

        setCommentCount(mCommentCount);
    }

    //设置DetailActivity中toolbarFragment的FavoriteIV是否被选中
    private void setFavoriteState(boolean isFavorited){
        ((DetailActivity) getActivity()).toolbarFragment.setFavorite(isFavorited);
    }

    //加载数据错误
    protected void executeOnLoadDataError(){
        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mState = STATE_REFRESH;
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });
    }

    //返回填充到webview中的内容
    protected abstract String getWebViewBody(T detail);

    //保存detail到缓存
    protected void saveCache(T detail){
        new SaveCacheTask(getActivity(),detail,getCacheKey()).execute();
    }

    //读取缓存的AsyncTask
    private class CacheTask extends AsyncTask<String,Void,T>{
        private final WeakReference<Context> mContext;

        private CacheTask(Context context){
            mContext=new WeakReference<Context>(context);
        }

        @Override
        protected  T doInBackground(String... params){
            if (mContext.get() != null){

                //通过CacheManager获取相应的缓存值
                Serializable seri=CacheManager.readObject(mContext.get(),params[0]);
                if(seri==null){
                    return null;
                }else{
                    return (T) seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(T detail){
            super.onPostExecute(detail);
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            if(detail != null){
                executeOnLoadDataSuccess(detail);
            }else{
                executeOnLoadDataError();
            }
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        }
    }

    //保存detail到缓存
    private class SaveCacheTask extends AsyncTask<Void,Void,Void>{
        private final WeakReference<Context> mContext;

        private final Serializable seri;

        private final String key;

        private SaveCacheTask(Context context,Serializable seri,String key){
            mContext=new WeakReference<Context>(context);
            this.seri=seri;
            this.key=key;
        }

        @Override
        protected Void doInBackground(Void... params){
            CacheManager.saveObject(mContext.get(),seri,key);
            return null;
        }
    }

    //创建Fragment的OptionsMenu
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        inflater.inflate(R.menu.menu_fragment_detail,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    //OptionsMenu的MenuItem被选中之后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_refresh:
                sendRequestDataForNet();
                return false;
            case R.id.menu_item_font_size:
                showChangeFontSize();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    AlertDialog fontSizeChangeDialog;

    //重新选择字体
    private void showChangeFontSize(){
        final String[] items=getResources().getStringArray(R.array.font_size);
        fontSizeChangeDialog= DialogUtils.getSingleChoiceDialog(getActivity(),items, FontSizeUtils.getSaveFontSizeIndex(),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i){
                     //更改字体的大小
                     FontSizeUtils.saveFontSize(i);
                     mWebView.loadUrl(FontSizeUtils.getFontSize(i));
                     fontSizeChangeDialog.dismiss();
                    }
                }).show();
    }

    //收藏或者取消收藏
    public void favourOrRemove(){
        if(mDetail==null){
            return;
        }
        //检查网络是否可用
        if(!TDevice.hasInternet()){
            AppContext.showToast(R.string.tip_no_internet);
            return;
        }
        //检查用户是否登录
        if(!AppContext.getInstance().isLogin()){
            showLoginActivity(getActivity());
            return;
        }
        //获取用户的Uid;
        int uid=AppContext.getInstance().getLoginUid();
        final boolean isFavorited=getFavoriteState()==1?true:false;

        //收藏与取消收藏
        AsyncHttpResponseHandler mFavoriteHandler=new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int arg0,Header[] arg1,byte[] arg2){
                try{
                    Result result= XmlUtils.toBean(ResultData.class,new ByteArrayInputStream(arg2)).getResult();
                    if(result.OK()){
                        AppContext.showToast(result.getMessage());

                        //获取新的状态
                        boolean newFavorited=!isFavorited;
                        setFavoriteState(newFavorited);

                        //更新收藏的状态
                        updateFavoriteChanged(!newFavorited?0:1);

                    }else{
                        onFailure(arg0,arg1,arg2,null);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    onFailure(arg0,arg1,arg2,e);
                }
            }

            @Override
            public void onFailure(int arg0,Header[] arg1,byte[] arg2,Throwable arg3){
                AppContext.showToast(R.string.add_favorite_fail);
            }

            @Override
            public void onStart(){
                super.onStart();
                showWaitDialog("请稍后...");
            }

            @Override
            public void onFinish(){
                super.onFinish();
                hideWaitDialog();
            }

        };

        if(isFavorited){
            TeaScriptApi.delFavorite(uid,mId,getFavoriteType(),mFavoriteHandler);
        }else{
            TeaScriptApi.addFavorite(uid,mId,getFavoriteType(),mFavoriteHandler);
        }

    }

    //举报帖子
    public void onReportMenuClick(){

        if(mId==0 || mDetail==null){
            AppContext.showToast("正在加载，请稍等...");
        }

        if(!AppContext.getInstance().isLogin()){
            UiUtils.showLoginActivity(getActivity());
            return;
        }

        int reportId=mId;

        final ReportDialog dialog=new ReportDialog(getActivity(),getReportUrl(),reportId,getReportType());
        dialog.setCancelable(true);
        dialog.setTitle(R.string.report);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setNegativeButton(R.string.cancle,null);

        final TextHttpResponseHandler mReportHandler=new TextHttpResponseHandler(){

            @Override
            public void onSuccess(int arg0,Header[] arg1,String arg2){
                if (TextUtils.isEmpty(arg2)){
                    AppContext.showToast(R.string.tip_report_success);
                }else{
                    AppContext.showToast(arg2);
                }
            }

            @Override
            public void onFailure(int arg0,Header[] arg1,String arg2,Throwable arg3){
                AppContext.showToast(R.string.tip_report_fail);
            }

            @Override
            public void onFinish(){
                hideWaitDialog();
            }

        };

        dialog.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface d,int which){
                Report report=null;
                if((report=dialog.getReport()) !=null){
                    showWaitDialog(R.string.progress_submit);
                    TeaScriptApi.report(report,mReportHandler);
                }
                d.dismiss();
            }
        });

        dialog.show();

    }

    //分享
    public void  handleShare(){
        if(mDetail==null || TextUtils.isEmpty(getShareContent())
                || TextUtils.isEmpty(getShareUrl()) || TextUtils.isEmpty(getShareTitle())){
            AppContext.showToast("内容加载失败...");
            return;
        }
        if(shareDialog==null){
            shareDialog=new ShareDialog(getActivity());
        }
        shareDialog.setCancelable(true);
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.setTitle(R.string.share_to);
        shareDialog.setShareInfo(getShareTitle(), getShareContent(), getShareUrl());
        shareDialog.show();
    }

    //显示评论列表
    public void onClickShowComment(){
        showCommentView();
    }

    //刷新数据
    protected void refresh(){
        sendRequestDataForNet();
    }

    //重写OnSendCommentListener中的OnClickSendButton发表评论
    @Override
    public void onClickSendButton(Editable str){
        if(!TDevice.hasInternet()){
            AppContext.showToast(R.string.tip_network_error);
            return;
        }
        if(!AppContext.getInstance().isLogin()){
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        if(TextUtils.isEmpty(str)){
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }
        showWaitDialog(R.string.progress_submit);
        TeaScriptApi.pubComment(getCommentType(),mId,AppContext.getInstance().getLoginUid(),str.toString(),0,mCommentHandler);
    }

    @Override
    public void onClickFlagButton(){

    }

    protected AsyncHttpResponseHandler mCommentHandler=new AsyncHttpResponseHandler(){

        @Override
        public void onSuccess(int arg0,Header[] arg1,byte[] arg2){
            try{
                ResultData resultData=XmlUtils.toBean(ResultData.class,new ByteArrayInputStream(arg2));
                Result result=resultData.getResult();
                if(result.OK()){
                    hideWaitDialog();
                    AppContext.showToast(result.getMessage());
                    //评论成功之后，评论数加1
                    setCommentCount(mCommentCount+1);
                }else {
                    hideWaitDialog();
                    AppContext.showToast(result.getMessage());
                }
            }catch(Exception e){
                e.printStackTrace();
                onFailure(arg0,arg1,arg2,e);
            }
            ((DetailActivity) getActivity()).emojiFragment.clean();
        }

        @Override
        public void onFailure(int arg0,Header[] arg1,byte[] arg2,Throwable arg3){
            hideWaitDialog();;
            AppContext.showToast(R.string.comment_publish_faile);
        }

        @Override
        public void onFinish(){
            ((DetailActivity) getActivity()).emojiFragment.hideAllKeyBoard();
        }

    };

    //获取去除了html标签的body
    protected String getFilterHtmlBody(String body){
        if(body==null)
            return "";
        return HtmlUtils.delHTMLTag(body.trim());
    }

    //显示评论列表
    protected abstract void showCommentView();

    //获取评论的类型
    protected abstract int getCommentType();

    protected abstract String getShareTitle();

    protected abstract String getShareContent();

    protected abstract String getShareUrl();

    //返回举报的url
    protected String getReportUrl(){
        return "";
    }

    //返回举报的类型
    protected byte getReportType(){
        return Report.TYPE_QUESTION;
    }

    //获取收藏类型(如新闻、博客、帖子)
    protected abstract int getFavoriteType();

    protected abstract int getFavoriteState();

    protected abstract void updateFavoriteChanged(int newFavoriteState);

    protected abstract int getCommentCount();

    public ShareDialog getShareDialog(){
        return shareDialog;
    }


}
