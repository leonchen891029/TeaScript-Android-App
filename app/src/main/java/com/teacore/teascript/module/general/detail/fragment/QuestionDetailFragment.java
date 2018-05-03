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
import com.teacore.teascript.module.general.bean.CommentQ;
import com.teacore.teascript.module.general.bean.QuestionDetail;
import com.teacore.teascript.module.general.bean.QuestionTags;
import com.teacore.teascript.module.general.comment.CommentQView;
import com.teacore.teascript.module.general.detail.constract.QuestionDetailContract;
import com.teacore.teascript.module.general.widget.FlowLayout;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;

import java.util.List;

/**
 * 与QuestionDetailActivity相关联的Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-15
 */

public class QuestionDetailFragment extends DetailFragment<QuestionDetail,QuestionDetailContract.QuestionView,QuestionDetailContract.QuestionPresenter>
       implements View.OnClickListener,QuestionDetailContract.QuestionView{

    private long mId;
    private long mCommentId;
    private long mCommentAuthorId;

    private CoordinatorLayout mCoordinatorLayout;
    private NestedScrollView mContentNSV;
    private TextView mTitleTV;
    private FlowLayout mFlowLayout;
    private TextView mNameTV;
    private TextView mPubDateTV;
    private CommentQView mCommentQView;
    private LinearLayout mBottomLL;
    private EditText mInputET;
    private LikeButton mFavoriteLB;
    private ImageView mShareIV;

    @Override
    protected int getLayoutId(){
        return R.layout.general_fragment_question_detail;
    }

    @Override
    protected void initView(View view){
        super.initView(view);

        mCoordinatorLayout=(CoordinatorLayout) view.findViewById(R.id.general_fragment_question_detail);
        mContentNSV=(NestedScrollView) view.findViewById(R.id.content_nsv);
        mTitleTV=(TextView) view.findViewById(R.id.question_title_tv);
        mFlowLayout=(FlowLayout) view.findViewById(R.id.question_flowlayout);
        mNameTV=(TextView) view.findViewById(R.id.question_author_tv);
        mPubDateTV=(TextView) view.findViewById(R.id.question_pub_date_tv);
        mCommentQView=(CommentQView) view.findViewById(R.id.comment_q_view);
        mBottomLL=(LinearLayout) view.findViewById(R.id.bottom_ll);
        mInputET=(EditText) view.findViewById(R.id.input_et);
        mFavoriteLB=(LikeButton) view.findViewById(R.id.favorite_lb);
        mShareIV=(ImageView) view.findViewById(R.id.share_iv);

        mFavoriteLB.setOnClickListener(this);
        mShareIV.setOnClickListener(this);
        mCommentQView.setOnClickListener(this);

        registerScroller(mContentNSV,mCommentQView);

        mInputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
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
            // 收藏
            case R.id.favorite_lb:
                handleFavorite();
                break;
            // 分享
            case R.id.share_iv:
                handleShare();
                break;
            // 评论列表
            case R.id.see_more_tv:
                UiUtils.showBlogComment(getActivity(), (int) mId,
                        (int) mPresenter.getData().getAuthorId());
                break;
        }
    }

    @Override
    protected void initData(){

        QuestionDetail questionDetail=mPresenter.getData();
        if(questionDetail==null){
            return;
        }

        mId=mCommentId=questionDetail.getId();

        String bodyString=questionDetail.getBody();

        setCommentCount(questionDetail.getCommentCount());
        setBodyContent(bodyString);

        String title = questionDetail.getTitle();
        if (!TextUtils.isEmpty(title))
            mTitleTV.setText(title);

        List<QuestionTags> tags=questionDetail.getTags();
        if(tags!=null && tags.size()>0){
            for(QuestionTags tag:tags){
                String tagString=tag.getContent();
                TextView tagTV=(TextView) getActivity().getLayoutInflater().inflate(R.layout.item_flowlayout,mFlowLayout,false);
                if (!TextUtils.isEmpty(tagString))
                    tagTV.setText(tagString);
                mFlowLayout.addView(tagTV);
            }
        }

        String author=questionDetail.getAuthor();
        if(!TextUtils.isEmpty(author)){
            mNameTV.setText(author);
        }

        String time= TimeUtils.friendly_time(questionDetail.getPubDate());
        if(!TextUtils.isEmpty(time)){
            mPubDateTV.setText(time);
        }

        toFavoriteOk(questionDetail);

        setText(R.id.info_see_tv, String.valueOf(questionDetail.getViewCount()));
        setText(R.id.info_comment_tv, String.valueOf(questionDetail.getCommentCount()));

        mCommentQView.setTitle(String.format("回答 (%s)", questionDetail.getCommentCount()));
        mCommentQView.initComments(questionDetail.getId(), TeaScriptApi.COMMENT_QUESTION,
                questionDetail.getCommentCount(), getImageLoader(), null);

    }

    private boolean mInputDoubleEmpty = false;

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mInputET.getText().toString().trim())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
                    mInputET.setHint("我要回答");
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
        mPresenter.toSendComment(mId, mCommentId, mCommentAuthorId, mInputET.getText().toString().trim());
    }


    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(QuestionDetail questionDetail) {
        if (questionDetail.isFavorite())
            mFavoriteLB.setLiked(true);
        else
            mFavoriteLB.setLiked(false);
    }


    @Override
    public void toSendCommentOk(CommentQ comment) {
        AppContext.showToast(R.string.comment_publish_success);
        mInputET.setText("");
        mCommentQView.addComment(comment, getImageLoader(), null);
        TDevice.hideSoftKeyboard(mInputET);
    }

}
