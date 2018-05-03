package com.teacore.teascript.module.general.teatime;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.bean.TeatimeDetail;
import com.teacore.teascript.module.general.base.baseactivity.BaseBackActivity;
import com.teacore.teascript.module.general.behavior.KeyboardInputDelegation;
import com.teacore.teascript.module.general.detail.constract.TeatimeDetailContract;
import com.teacore.teascript.module.general.widget.TWebView;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.PlatformUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.recordbutton.RecordButtonUtils;

import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 * Teatime详情Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-20
 */

public class TeatimeDetailActivity extends BaseBackActivity implements TeatimeDetailContract.Presenter{

    public static final String BUNDLE_KEY_TEATIME="BUNDLE_KEY_TEATIME";

    private CoordinatorLayout mCoordinatorLayout;
    private AvatarView mUserAV;
    private TextView mNameTV;
    private TWebView mWebView;
    private RelativeLayout mRecordRL;
    private ImageView mRecordIV;
    private TextView  mRecordSecondTV;
    private TextView mPubDateTV;
    private TextView mClientTV;
    private ImageView mCommentIV;
    private ImageView mThumbupIV;
    private FrameLayout mFragmentFL;


    EditText mInputET;

    private Teatime mTeatime;
    private Comment mComment;
    private Dialog mDialog;
    private RecordButtonUtils mRecordUtil;

    private TeatimeDetailContract.ICommentView mCommentViewImp;
    private TeatimeDetailContract.IThumbupView mThumbupViewImp;
    private TeatimeDetailContract.IAgencyView  mAgencyViewImp;

    private KeyboardInputDelegation mDelegation;
    private View.OnClickListener onAvatarClickListener;

    public static void show(Context context,Teatime teatime){
        Intent intent=new Intent(context,TeatimeDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TEATIME,(Serializable) teatime);
        context.startActivity(intent);
    }

    private AsyncHttpResponseHandler pubAdmireHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            mThumbupIV.setSelected(!mThumbupIV.isSelected());
            mThumbupViewImp.onLikeSuccess(mThumbupIV.isSelected(), null);
            dismissDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            AppContext.showToast(mThumbupIV.isSelected() ? "取消失败" : "点赞失败");
            dismissDialog();
        }

    };

    private AsyncHttpResponseHandler pubCommentHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            mCommentViewImp.onCommentSuccess(null);
            mComment = null;
            mInputET.setHint("发表评论");
            mInputET.setText(null);
            dismissDialog();
            TDevice.hideSoftKeyboard(mDelegation.getInputView());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            AppContext.showToast(R.string.comment_publish_faile);
            dismissDialog();
        }
    };

    @Override
    protected int getLayoutId(){
        return R.layout.general_activity_teatime_detail;
    }

    @Override
    protected boolean initBundle(Bundle bundle){
        mTeatime=(Teatime) getIntent().getSerializableExtra(BUNDLE_KEY_TEATIME);
        if(mTeatime==null){
            AppContext.showToast("Teatime对象没有找到");
            //直接finish
            return false;
        }
        return super.initBundle(bundle);
    }

    @Override
    protected void initView() {

        mCoordinatorLayout=(CoordinatorLayout) findViewById(R.id.improve_activity_teatime_detail);
        mUserAV=(AvatarView) findViewById(R.id.user_av);
        mNameTV=(TextView) findViewById(R.id.name_tv);
        mWebView=(TWebView) findViewById(R.id.webview);
        mRecordRL=(RelativeLayout) findViewById(R.id.teatime_record_rl);
        mRecordIV=(ImageView) findViewById(R.id.teatime_record_iv);
        mRecordSecondTV=(TextView) findViewById(R.id.teatime_record_tv);
        mPubDateTV=(TextView) findViewById(R.id.pub_date_tv);
        mClientTV=(TextView) findViewById(R.id.client_tv);
        mCommentIV=(ImageView) findViewById(R.id.comment_iv);
        mThumbupIV=(ImageView) findViewById(R.id.thumbup_iv);
        mFragmentFL=(FrameLayout) findViewById(R.id.fragment_fl);

        mThumbupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.getProgressDialog(TeatimeDetailActivity.this, "正在提交请求...");
                mDialog.show();
                if (!mThumbupIV.isSelected()) {
                    TeaScriptApi.pubLikeTeatime(mTeatime.getId(), mTeatime.getAuthorId(), pubAdmireHandler);
                } else {
                    TeaScriptApi.pubUnlikeTeatime(mTeatime.getId(), mTeatime.getAuthorId(), pubAdmireHandler);
                }
            }
        });

        mCommentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TDevice.showSoftKeyboard(mInputET);
            }
        });

        mDelegation = KeyboardInputDelegation.delegate(this, mCoordinatorLayout, mFragmentFL);
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView view, String content) {

                if (TextUtils.isEmpty(content.replaceAll("[ \\s\\n]+", ""))) {
                    AppContext.showToast("请输入文字");
                    return;
                }

                if (!AppContext.getInstance().isLogin()) {
                    UiUtils.showLoginActivity(TeatimeDetailActivity.this);
                    return;
                }

                mDialog = DialogUtils.getProgressDialog(TeatimeDetailActivity.this, "正在发表评论...");
                mDialog.show();

                //如果mComment为null，针对Teatime发表评论，不为null，针对mComment发表评论
                if (mComment== null) {
                    TeaScriptApi.pubComment(3, mTeatime.getId(), AppContext.getInstance().getLoginUid(),
                            view.getText().toString(), 1, pubCommentHandler);
                } else {
                    TeaScriptApi.replyComment(mTeatime.getId(), 3, mComment.getId(), mComment.getAuthorId(),
                            AppContext.getInstance().getLoginUid(), view.getText().toString(), pubCommentHandler);
                }

            }

            @Override
            public void onFinalBackSpace(View v) {
                if (mComment == null) return;
                mComment = null;
                mInputET.setHint("发表评论");
            }

        });

        mInputET = mDelegation.getInputView();

    }

    @Override
    protected void initData() {

        resolveVoice();

        fillDetailView();

        //设置TeatimeDetailFragment对象为实现了这三个接口的具体对象
        TeatimeDetailViewPagerFragment mTeatimeDetailViewPagerFrag = TeatimeDetailViewPagerFragment.instantiate(this);
        mCommentViewImp = mTeatimeDetailViewPagerFrag;
        mThumbupViewImp = mTeatimeDetailViewPagerFrag;
        mAgencyViewImp = mTeatimeDetailViewPagerFrag;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_fl, mTeatimeDetailViewPagerFrag)
                .commit();

        //网络请求数据初始化TeatimeDetailFragment
        TeaScriptApi.getTeatimeDetail(mTeatime.getId(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                TeatimeDetail teatimeDetail = XmlUtils.toBean(TeatimeDetail.class, responseBody);

                if (teatimeDetail == null || teatimeDetail.getTeatime() == null) {
                    AppContext.showToast(R.string.teatime_detail_data_null);
                    finish();
                }

                mTeatime = teatimeDetail.getTeatime();
                mAgencyViewImp.resetCmnCount(mTeatime.getCommentCount());
                mAgencyViewImp.resetLikeCount(mTeatime.getLikeCount());
                fillDetailView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppContext.showToast("获取失败");
                finish();
            }
        });

    }

    private void resolveVoice() {

        if (TextUtils.isEmpty(mTeatime.getAttach())) return;

        mRecordRL.setVisibility(View.VISIBLE);

        final AnimationDrawable drawable = (AnimationDrawable) mRecordIV.getBackground();

        mRecordRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTeatime == null) return;
                getRecordUtil().startPlay(mTeatime.getAttach(), mRecordSecondTV);
            }
        });

        getRecordUtil().setOnPlayListener(new RecordButtonUtils.OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mRecordIV.setBackground(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                drawable.start();
                mRecordIV.setBackground(drawable);
            }
        });
    }

    private RecordButtonUtils getRecordUtil() {
        if (mRecordUtil == null) {
            mRecordUtil = new RecordButtonUtils();
        }
        return mRecordUtil;
    }

    private void dismissDialog(){
        if (mDialog == null) return;
        mDialog.dismiss();
        mDialog = null;
    }

    private void fillDetailView() {

        if (isDestroy())
            return;

        if (TextUtils.isEmpty(mTeatime.getAuthorPortrait())) {
            mUserAV.setImageResource(R.drawable.widget_dface);
        } else {
            getImageLoader()
                    .load(mTeatime.getAuthorPortrait())
                    .asBitmap()
                    .placeholder(ContextCompat.getDrawable(getBaseContext(),R.drawable.widget_dface))
                    .error(ContextCompat.getDrawable(getBaseContext(),R.drawable.widget_dface))
                    .into(mUserAV);
        }

        mUserAV.setOnClickListener(getOnAvatarClickListener());
        mNameTV.setText(mTeatime.getAuthor());

        if (!TextUtils.isEmpty(mTeatime.getPubDate()))
            mPubDateTV.setText(TimeUtils.friendly_time(mTeatime.getPubDate()));

        PlatformUtils.setPlatFromString(mClientTV, mTeatime.getAppClient());

        if (mTeatime.getIsLike() == 1) {
            mThumbupIV.setSelected(true);
        } else {
            mThumbupIV.setSelected(false);
        }

        fillWebViewBody();

    }

    /**
     * 填充webview内容
     */
    private void fillWebViewBody() {

        if (TextUtils.isEmpty(mTeatime.getBody())) return;

        String html = mTeatime.getBody() + "<br/><img src=\"" + mTeatime.getImgSmall() + "\" data-url=\"" + mTeatime.getImgBig() + "\"/>";

        mWebView.loadTeatimeDataAsync(html, null);

    }

    private View.OnClickListener getOnAvatarClickListener() {

        if (onAvatarClickListener == null) {
            onAvatarClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.showUserCenter(TeatimeDetailActivity.this, mTeatime.getAuthorId(), mTeatime.getAuthor());
                }
            };
        }

        return onAvatarClickListener;

    }

    @Override
    public Teatime getTeatime() {
        return mTeatime;
    }

    @Override
    public void toReply(Comment comment) {
        mDelegation.notifyWrapper();
        this.mComment = comment;
        mInputET.setHint("回复@ " + comment.getAuthor());
        TDevice.showSoftKeyboard(mInputET);
    }

    @Override
    public void onScroll() {
        if (mDelegation != null) mDelegation.onTurnBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (!mDelegation.onTurnBack()) return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
