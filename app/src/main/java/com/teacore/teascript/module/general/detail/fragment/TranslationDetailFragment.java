package com.teacore.teascript.module.general.detail.fragment;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.like.LikeButton;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.bean.TranslationDetail;
import com.teacore.teascript.module.general.behavior.ScrollingAutoHideBehavior;
import com.teacore.teascript.module.general.comment.CommentsView;
import com.teacore.teascript.module.general.comment.OnCommentClickListener;
import com.teacore.teascript.module.general.detail.constract.TranslationDetailContract;
import com.teacore.teascript.module.general.widget.AboutRecommendView;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;

/**
 * 与TranslationDetailActivity相关联的Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-16
 */

public class TranslationDetailFragment extends DetailFragment<TranslationDetail,TranslationDetailContract.TransView,TranslationDetailContract.TransPresenter>
       implements TranslationDetailContract.TransView,View.OnClickListener,OnCommentClickListener{

    private long mId;
    private TextView mNameTV;
    private TextView mPubDateTV;
    private TextView mTitleTV;
    private AvatarView mPortraitAV;
    private LikeButton mFavoriteLB;
    private ImageView mShareIV;
    private EditText mInputET;
    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty=false;
    private AboutRecommendView mAboutView;
    private CommentsView mCommentsView;
    private CoordinatorLayout mCoordinatorLayout;
    private NestedScrollView mContentNSV;
    private LinearLayout mBottomLL;
    private TextView mAboutSoftwareTitleTV;
    private LinearLayout mAboutSoftwareLL;


    @Override
    protected int getLayoutId() {
        return R.layout.general_fragment_translation_detail;
    }


    @Override
    protected void initView(View view) {
        super.initView(view);

        mCoordinatorLayout=(CoordinatorLayout) view.findViewById(R.id.general_fragment_news_detail);
        mContentNSV=(NestedScrollView) view.findViewById(R.id.content_nsv);
        mPortraitAV=(AvatarView) view.findViewById(R.id.avatar_av);
        mPubDateTV=(TextView) view.findViewById(R.id.pub_date_tv);
        mNameTV=(TextView) view.findViewById(R.id.name_tv);
        mTitleTV=(TextView) view.findViewById(R.id.title_tv);
        mAboutSoftwareLL=(LinearLayout) view.findViewById(R.id.about_software_ll);
        mAboutSoftwareTitleTV=(TextView) view.findViewById(R.id.about_software_title_tv);
        mAboutView=(AboutRecommendView) view.findViewById(R.id.about_recommend_view);
        mCommentsView=(CommentsView) view.findViewById(R.id.comments_view);
        mBottomLL=(LinearLayout) view.findViewById(R.id.bottom_ll);
        mInputET=(EditText) view.findViewById(R.id.input_et);
        mFavoriteLB=(LikeButton) view.findViewById(R.id.favorite_lb);
        mShareIV=(ImageView) view.findViewById(R.id.share_iv);

        registerScroller(mContentNSV,mCommentsView);

        mFavoriteLB.setOnClickListener(this);
        mShareIV.setOnClickListener(this);

        mInputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handleSendComment();
                    return true;
                }
                return false;
            }
        });
        mInputET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_lb:
                handleFavorite();
                break;
            // 分享
            case R.id.share_iv:
                handleShare();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        final TranslationDetail translationDetail = mPresenter.getData();
        if (translationDetail == null)
            return;

        mId = mCommentId = translationDetail.getId();

        setCommentCount(translationDetail.getCommentCount());
        setBodyContent(translationDetail.getBody());

        getImageLoader().load(translationDetail.getAuthorPortrait()).error(R.drawable.widget_dface).into(mPortraitAV);

        mNameTV.setText(translationDetail.getAuthor());
        String time =  TimeUtils.friendly_time(translationDetail.getPubDate());
        mPubDateTV.setText(time);

        mTitleTV.setText(translationDetail.getTitle());

        toFavoriteOk(translationDetail);

        setText(R.id.info_see_tv, String.valueOf(translationDetail.getViewCount()));
        setText(R.id.info_comment_tv, String.valueOf(translationDetail.getCommentCount()));

        mAboutSoftwareLL.setVisibility(View.GONE);
        mAboutView.setVisibility(View.GONE);

        mCommentsView.setTitle(String.format("评论 (%s)", translationDetail.getCommentCount()));
        mCommentsView.initComments(translationDetail.getId(), TeaScriptApi.COMMENT_TRANSLATION, translationDetail.getCommentCount(), getImageLoader(), this);
    }

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mInputET.getText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
                    mInputET.setHint("发表评论");
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }


    private void handleFavorite() {
        mPresenter.toFavorite();
    }

    private void handleShare() {
        mPresenter.toShare();
    }

    private void handleSendComment() {
        mPresenter.toSendComment(mId, mCommentId, mCommentAuthorId, mInputET.getText().toString());
    }


    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(TranslationDetail translationDetail) {
        if (translationDetail.isFavorite())
            mFavoriteLB.setLiked(true);
        else
            mFavoriteLB.setLiked(false);
    }

    @Override
    public void toSendCommentOk(Comment comment) {
        AppContext.showToast(R.string.comment_publish_success);
        mInputET.setText("");
        mCommentsView.addComment(comment, getImageLoader(), this);
        TDevice.hideSoftKeyboard(mInputET);
    }

    @Override
    public void onClick(View view, Comment comment) {
        ScrollingAutoHideBehavior.showBottomLayout(mCoordinatorLayout, mContentNSV, mBottomLL);
        mCommentId = comment.getId();
        mCommentAuthorId = comment.getAuthorId();
        mInputET.setHint(String.format("回复: %s", comment.getAuthor()));
        TDevice.showSoftKeyboard(mInputET);
    }



}
