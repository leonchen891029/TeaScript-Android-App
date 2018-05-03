package com.teacore.teascript.module.detail.fragment;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.CommentAdapter;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseHeaderListFragment;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.bean.TeatimeDetail;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.module.quickoption.activity.TeatimePubActivity;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.AnimationsUtils;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.PlatformUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.OnSendClickListener;
import com.teacore.teascript.widget.recordbutton.RecordButtonUtils;
import com.teacore.teascript.widget.recordbutton.RecordButtonUtils.OnPlayListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**动弹的详情类！这里继承的是BaseHeaderListFragment，头部是具体类T的详细信息，下面是相关的评论
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-9
 */
public class TeatimeDetailFragment extends BaseHeaderListFragment<Comment,TeatimeDetail> implements
        OnItemClickListener,OnItemLongClickListener,OnSendClickListener {

    private static final String CACHE_KEY_PREFIX = "Teatime_";
    private static final String CACHE_KEY_Teatime_COMMENT = "Teatime_comment_";

    private AvatarView mAvatarIV;
    private TextView mNameTV,mTimeTV,mFromTV,mCommentCountTV;
    private WebView mContentWV;
    private Teatime mTeatime;
    private int mTeatimeId;
    private RelativeLayout mRecordSoundRL;
    private TextView mLikeUsersTV;
    private TextView mLikeStateTV;

    private DetailActivity detailActivity;
    private final RecordButtonUtils mUtils=new RecordButtonUtils();

    @Override
    protected CommentAdapter getListAdapter() {
        return new CommentAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        detailActivity=(DetailActivity) getActivity();
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_Teatime_COMMENT + mTeatimeId + "_" + mCurrentPage;
    }

    @Override
    protected CommentList parseList(InputStream is) throws Exception {
        CommentList list = XmlUtils.toBean(CommentList.class, is);
        return list;
    }

    @Override
    protected CommentList readList(Serializable seri) {
        return ((CommentList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getCommentList(mTeatimeId, CommentList.CATALOG_Teatime,mCurrentPage, mHandler);
    }

    @Override
    protected View initHeaderView(){
        Intent args=getActivity().getIntent();
        mTeatime=(Teatime) args.getParcelableExtra("Teatime");
        mTeatimeId=args.getIntExtra("Teatime_id",0);

        mListView.setOnItemLongClickListener(this);
        View header=LayoutInflater.from(getActivity()).inflate(R.layout.list_header_teatime_detail,null);

        mAvatarIV=(AvatarView) header.findViewById(R.id.avatar_iv);
        mNameTV=(TextView) header.findViewById(R.id.name_tv);
        mTimeTV=(TextView) header.findViewById(R.id.time_tv);
        mFromTV=(TextView) header.findViewById(R.id.from_tv);
        mCommentCountTV=(TextView) header.findViewById(R.id.comment_count_tv);
        mContentWV=(WebView) header.findViewById(R.id.webView);

        UiUtils.initWebView(mContentWV);
        mContentWV.loadUrl("file://android_asset/detail_page.html");
        initSoundView(header);

        mLikeUsersTV=(TextView) header.findViewById(R.id.likeusers_tv);
        mLikeStateTV=(TextView) header.findViewById(R.id.likestate_tv);
        mLikeStateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeOption();
            }
        });

        TypefaceUtils.setTypeface(mLikeStateTV);
        return header;
    }

    //初始化动弹的录音view
    private void initSoundView(View header){
        mRecordSoundRL=(RelativeLayout) header.findViewById(R.id.Teatime_record_rl);
        final ImageView playButton=(ImageView) header.findViewById(R.id.Teatime_record_iv);
        final TextView playTime=(TextView) header.findViewById(R.id.Teatime_record_tv);

        final AnimationDrawable drawable=(AnimationDrawable) playButton.getBackground();
        mRecordSoundRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mTeatime!=null){
                    mUtils.startPlay(mTeatime.getAttach(),playTime);
                }else{
                    AppContext.showToast("找不到语音动弹，可能已经被主人删除");
                }

            }
        });

        mUtils.setOnPlayListener(new OnPlayListener() {

            @Override
            public void stopPlay() {
                drawable.stop();
                playButton.setBackgroundResource(R.drawable.icon_record);
            }

            @Override
            public void starPlay() {
                playButton.setBackgroundResource(R.drawable.icon_record2);
                drawable.start();
            }
        });

    }

    @Override
    public void onStop(){
        super.onStop();
        if(mUtils!=null&&mUtils.isPlaying()){
            mUtils.stopPlay();
        }
    }

    @Override
    protected boolean requestDataIfViewCreated(){
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        detailActivity.emojiFragment.hideFlagButton();
    }

    //填充UI的数据
    private void fillUI() {
        mAvatarIV.setAvatarUrl(mTeatime.getAuthorPortrait());
        mAvatarIV.setUserInfo(mTeatime.getAuthorId(), mTeatime.getAuthor());
        mNameTV.setText(mTeatime.getAuthor());
        mTimeTV.setText(TimeUtils.friendly_time(mTeatime.getPubDate()));
        PlatformUtils.setPlatFromString(mFromTV, mTeatime.getAppClient());

        mCommentCountTV.setText(mTeatime.getCommentCount() + "");

        if (StringUtils.isEmpty(mTeatime.getAttach())) {
            mRecordSoundRL.setVisibility(View.GONE);
        } else {
            mRecordSoundRL.setVisibility(View.VISIBLE);
        }

        fillWebViewBody();
        setLikeUser();
        setLikeState();
    }

    private void setLikeState(){
        if(mTeatime!=null){
            if (mTeatime.getIsLike() == 1) {
                mLikeStateTV.setTextColor(ContextCompat.getColor(getActivity(),R.color.day_colorPrimary));
            } else {
                mLikeStateTV.setTextColor(ContextCompat.getColor(getActivity(),R.color.gray));
            }
        }

    }

    private void setLikeUser() {
        if (mTeatime == null || mTeatime.getLikeUsers() == null
                || mTeatime.getLikeUsers().isEmpty()) {
            mLikeUsersTV.setVisibility(View.GONE);
        } else {
            mLikeUsersTV.setVisibility(View.VISIBLE);
            mTeatime.setLikeUsers(getActivity(), mLikeUsersTV, false);
        }
    }

    //填充webview的内容
    private void fillWebViewBody(){
        StringBuffer body=new StringBuffer();
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        body.append(UiUtils.WEB_STYLE+UiUtils.WEB_LOAD_IMAGES);

        StringBuilder TeatimeBodyOriginal=new StringBuilder(mTeatime.getBody());

        String TeatimeBody= TextUtils.isEmpty(mTeatime.getImgSmall())?TeatimeBodyOriginal.toString():
                TeatimeBodyOriginal.toString()+"<br/><img src=\"" + mTeatime.getImgSmall() + "\">";
        body.append(setHtmlCotentSupportImagePreview(TeatimeBody));

        UiUtils.addWebImageShow(getActivity(),mContentWV);

        //封尾
        body.append("</div></body>");

        mContentWV.loadDataWithBaseURL(null, body.toString(), "text/html", "utf-8", null);
    }

    //添加图片放大支持
    private String setHtmlCotentSupportImagePreview(String body) {
        // 过滤掉 img标签的width,height属性
        body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
        return body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                "$1$2\" onClick=\"javascript:mWebViewImageListener.showImagePreview('"
                        + mTeatime.getImgBig() + "')\"");
    }

    /**
     注意这里OnItemClick的position很特别是从1开始的
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final Comment comment = mAdapter.getItem(position - 1);
        if (comment == null)
            return;
        detailActivity.emojiFragment.getEditText().setHint("回复:" + comment.getAuthor());
        detailActivity.emojiFragment.getEditText().setTag(comment);
        detailActivity.emojiFragment.showSoftKeyboard();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        if (position - 1 == -1) {
            return false;
        }

        final Comment item = mAdapter.getItem(position - 1);

        if (item == null)
            return false;

        int itemsLen = item.getAuthorId() == AppContext.getInstance().getLoginUid() ? 2 : 1; //判断是否为自己

        String[] items = new String[itemsLen];

        items[0] = getResources().getString(R.string.copy);

        //如果作者就是当前的用户
        if (itemsLen == 2) {
            items[1] = getResources().getString(R.string.delete);
        }

        DialogUtils.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(item.getContent()));
                } else if (i == 1) {
                    handleDeleteComment(item);
                }
            }
        }).show();
        return true;
    }

    private final AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {

                ResultData resultData = XmlUtils.toBean(ResultData.class,
                        new ByteArrayInputStream(arg2));

                Result res = resultData.getResult();

                if (res.OK()) {
                    hideWaitDialog();
                    AppContext.showToast(R.string.comment_publish_success);

                    mAdapter.setState(BaseListAdapter.STATE_NO_MORE);

                    mAdapter.addItem(0, resultData.getComment());

                    setTeatimeCommentCount();
                } else {

                    hideWaitDialog();

                    AppContext.showToast(res.getMessage());
                }

                detailActivity.emojiFragment.clean();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            hideWaitDialog();
            AppContext.showToast(R.string.comment_publish_faile);
        }
    };

    class DeleteOperationResponseHandler extends OperationResponseHandler {

        DeleteOperationResponseHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args) {
            try {
                Result res = XmlUtils.toBean(ResultData.class, is).getResult();

                if (res.OK()) {
                    AppContext.showToast(R.string.delete_success);
                    mAdapter.removeItem((Comment) args[0]);

                    setTeatimeCommentCount();
                } else {
                    AppContext.showToast(res.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailureOperation(code, e.getMessage(), args);
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                error) {
            AppContext.showToast(R.string.delete_fail);
        }
    }

    private void handleDeleteComment(Comment comment) {
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        AppContext.showToast(R.string.deleting);
        TeaScriptApi.deleteComment(mTeatimeId, CommentList.CATALOG_Teatime,
                comment.getId(), comment.getAuthorId(),
                new DeleteOperationResponseHandler(comment));
    }

    private void setTeatimeCommentCount() {
        mAdapter.notifyDataSetChanged();
        if (mTeatime != null) {
            mTeatime.setCommentCount(mAdapter.getDataSize());
            mCommentCountTV.setText(mTeatime.getCommentCount() + "");
        }
    }

    //转发动弹
    @TargetApi(Build.VERSION_CODES.N)
    private void repostTeatime(final Comment comment, final Teatime Teatime){
        Bundle bundle = new Bundle();
        bundle.putString(TeatimePubActivity.REPOST_IMAGE_KEY, Teatime.getImgBig());
        bundle.putString(TeatimePubActivity.REPOST_TEXT_KEY, String.format("//@%s :%s//@%s :%s",
                comment.getAuthor(), comment.getContent(),
                Teatime.getAuthor(), Html.fromHtml((Teatime.getBody()).toString(),Html.FROM_HTML_MODE_LEGACY)));
        UiUtils.showTeatimeActivity(getActivity(), TeatimePubActivity.ACTION_TYPE_REPOST, bundle);
    }

    @Override
    protected void requestDetailData(boolean isRefresh){
        String key=getDetailCacheKey();
        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        if((TDevice.hasInternet()&&(!CacheManager.isCacheExist(getActivity(),key))) || isRefresh){
            TeaScriptApi.getTeatimeDetail(mTeatimeId,mDetailHander);
        }else{
            readDetailCacheData(key);
        }
    }

    @Override
    protected boolean isRefresh() {
        return true;
    }

    //点赞逻辑，如果已经是点赞了则取消点赞
    private void likeOption(){
        if (mTeatime == null)
            return;

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
            }
        };

        if (AppContext.getInstance().isLogin()) {
            if (mTeatime.getIsLike() == 1) {
                mTeatime.setIsLike(0);
                mTeatime.getLikeUsers().remove(0);
                mTeatime.setLikeCount(mTeatime.getLikeCount() - 1);
                TeaScriptApi.pubUnlikeTeatime(mTeatimeId, mTeatime.getAuthorId(),
                        handler);
            } else {
                mLikeStateTV.setAnimation(AnimationsUtils.getScaleAnimation(1.5f,
                        300));
                mTeatime.setIsLike(1);
                mTeatime.getLikeUsers().add(0,
                        AppContext.getInstance().getLoginUser());
                mTeatime.setLikeCount(mTeatime.getLikeCount() + 1);
                TeaScriptApi.pubLikeTeatime(mTeatimeId, mTeatime.getAuthorId(), handler);
            }
            setLikeState();
            mTeatime.setLikeUsers(getActivity(), mLikeUsersTV, false);
        } else {
            AppContext.showToast("先登陆再点赞~");
            UiUtils.showLoginActivity(getActivity());
        }


    }

    @Override
    protected String getDetailCacheKey(){
        return CACHE_KEY_PREFIX+mTeatimeId;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeatimeDetail TeatimeDetail) {
        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        this.mTeatime = TeatimeDetail.getTeatime();
        fillUI();
        mAdapter.setNoDataText(R.string.comment_empty);
    }

    @Override
    protected TeatimeDetail getDetailBean(ByteArrayInputStream inputStream){
        return XmlUtils.toBean(TeatimeDetail.class,inputStream);
    }


    @Override
    protected void executeOnLoadDataSuccess(List<Comment> data) {
        super.executeOnLoadDataSuccess(data);
        int commentCount = StringUtils.toInt(mTeatime == null ? 0 : this.mTeatime
                .getCommentCount());
        if (commentCount < (mAdapter.getCount() - 1)) {
            commentCount = mAdapter.getCount() - 1;
        }
        mCommentCountTV.setText(commentCount + "");
    }

    @Override
    public void onClickSendButton(Editable str) {

        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_network_error);
            return;
        }
        if (TextUtils.isEmpty(str)) {
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }
        showWaitDialog(R.string.progress_submit);
        try {
            if (detailActivity.emojiFragment.getEditText().getTag() != null) {
                Comment comment = (Comment) detailActivity.emojiFragment.getEditText()
                        .getTag();
                TeaScriptApi.replyComment(mTeatimeId, CommentList.CATALOG_Teatime,
                        comment.getId(), comment.getAuthorId(), AppContext
                                .getInstance().getLoginUid(), str.toString(),
                        mCommentHandler);
            } else {
                TeaScriptApi.pubComment(CommentList.CATALOG_Teatime, mTeatimeId,
                        AppContext.getInstance().getLoginUid(), str.toString(),
                        0, mCommentHandler);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClickFlagButton() {
    }

    @Override
    public boolean onBackPressed() {
        if (detailActivity.emojiFragment.isShowEmojiKeyBoard()) {
            detailActivity.emojiFragment.hideAllKeyBoard();
            return true;
        }
        if (detailActivity.emojiFragment.getEditText().getTag() != null) {
            detailActivity.emojiFragment.getEditText().setTag(null);
            detailActivity.emojiFragment.getEditText().setHint("说点什么吧");
            return true;
        }
        return super.onBackPressed();
    }




}
