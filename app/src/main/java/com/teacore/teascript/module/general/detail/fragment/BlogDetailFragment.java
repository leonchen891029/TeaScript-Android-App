package com.teacore.teascript.module.general.detail.fragment;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.like.LikeButton;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.bean.BlogDetail;
import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.behavior.ScrollingAutoHideBehavior;
import com.teacore.teascript.module.general.comment.CommentsView;
import com.teacore.teascript.module.general.comment.OnCommentClickListener;
import com.teacore.teascript.module.general.detail.constract.BlogDetailContract;
import com.teacore.teascript.module.general.widget.AboutRecommendView;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;

/**
 * 与BlogDetailActivity相关联的Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-10
 */

public class BlogDetailFragment extends DetailFragment<BlogDetail,BlogDetailContract.BlogView,BlogDetailContract.BlogPresenter>
       implements BlogDetailContract.BlogView,View.OnClickListener,OnCommentClickListener {

    private long mId;
    private long mCommentId;
    private long mCommentAuthorId;

    private CoordinatorLayout mCoordinatorLayout;
    private NestedScrollView mContentNSV;
    private AvatarView mUserAV;
    private TextView mNameTV;
    private TextView mPubDateTV;
    private Button mFollowBtn;
    private ImageView mRecommendIV;
    private ImageView mOriginateIV;
    private TextView mTitleTV;
    private LinearLayout mBlogAbstractLL;
    private TextView mBlogAbstractTV;
    private AboutRecommendView mAboutView;
    private CommentsView mCommentsView;
    private LinearLayout mBottomLL;
    private EditText mInputET;
    private LikeButton mFavoriteLB;
    private ImageView mShareIV;

    @Override
    public int getLayoutId(){
        return R.layout.general_fragment_blog_detail;
    }

    @Override
    protected  void initView(View view){
        super.initView(view);

        mCoordinatorLayout=(CoordinatorLayout) view.findViewById(R.id.general_fragment_blog_detail);
        mContentNSV=(NestedScrollView) view.findViewById(R.id.content_nsv);
        mUserAV=(AvatarView) view.findViewById(R.id.user_av);
        mNameTV=(TextView) view.findViewById(R.id.name_tv);
        mPubDateTV=(TextView) view.findViewById(R.id.pub_date_tv);
        mFollowBtn=(Button) view.findViewById(R.id.follow_btn);
        mRecommendIV=(ImageView) view.findViewById(R.id.recommend_iv);
        mOriginateIV=(ImageView) view.findViewById(R.id.originate_iv);
        mTitleTV=(TextView) view.findViewById(R.id.title_tv);
        mBlogAbstractLL=(LinearLayout) view.findViewById(R.id.blog_abstract_ll);
        mBlogAbstractTV=(TextView) view.findViewById(R.id.blog_abstract_tv);
        mAboutView=(AboutRecommendView) view.findViewById(R.id.about_recommend_view);
        mCommentsView=(CommentsView) view.findViewById(R.id.comments_view);
        mBottomLL=(LinearLayout) view.findViewById(R.id.bottom_ll);
        mInputET=(EditText) view.findViewById(R.id.input_et);
        mFavoriteLB=(LikeButton) view.findViewById(R.id.favorite_lb);
        mShareIV=(ImageView) view.findViewById(R.id.share_iv);

        mFollowBtn.setOnClickListener(this);
        mFavoriteLB.setOnClickListener(this);
        mShareIV.setOnClickListener(this);
        mCommentsView.setOnClickListener(this);

        mAboutView.setTitle(getString(R.string.about_articles));
        registerScroller(mContentNSV,mCommentsView);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mFollowBtn.setElevation(0);
        }

        mInputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_SEND){
                    handleSendComment();
                    return true;
                }
                return false;
            }
        });
        mInputET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.follow_btn:
                handleFollow();
                break;
            case R.id.favorite_lb:
                handleFavorite();
                break;
            case R.id.share_iv:
                handleShare();
                break;
            case R.id.see_more_tv:
                UiUtils.showBlogComment(getActivity(), (int) mId,
                        (int) mPresenter.getData().getAuthorId());
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData(){

        BlogDetail blog=mPresenter.getData();

        if(blog==null){
            return;
        }

        mId=mCommentId=blog.getId();

        setCommentCount(blog.getCommentCount());
        setBodyContent(blog.getBody());

        getImageLoader().load(blog.getAuthorPortrait()).error(R.drawable.widget_dface).into(mUserAV);

        mNameTV.setText(blog.getAuthor());

        String time= TimeUtils.friendly_time(blog.getPubDate());
        mPubDateTV.setText(time);

        mRecommendIV.setVisibility(blog.isRecommend()?View.VISIBLE:View.GONE);
        mOriginateIV.setImageDrawable(blog.isOriginal()?getResources().getDrawable(R.drawable.icon_label_originate) :
                getResources().getDrawable(R.drawable.icon_label_reprint));

        mTitleTV.setText(blog.getTitle());

        if(TextUtils.isEmpty(blog.getAbstract())){
            mBlogAbstractLL.setVisibility(View.GONE);
        }else{
            mBlogAbstractTV.setText(blog.getAbstract());
            mBlogAbstractLL.setVisibility(View.VISIBLE);
        }

        toFollowOk(blog);
        toFavoriteOk(blog);

        setText(R.id.info_see_tv,String.valueOf(blog.getViewCount()));
        setText(R.id.info_comment_tv,String.valueOf(blog.getCommentCount()));

        mAboutView.setAbouts(blog.getAbouts(),3);

        mCommentsView.setTitle(String.format("评论 (%s)", blog.getCommentCount()));
        mCommentsView.initComments(blog.getId(), TeaScriptApi.COMMENT_BLOG,blog.getCommentCount(),getImageLoader(),this);
    }

    private boolean mInputDoubleEmpty=false;

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

    private void handleFollow() {
        mPresenter.toFollow();
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
    public void toFavoriteOk(BlogDetail blogDetail) {
        if (blogDetail.isFavorite())
            mFavoriteLB.setLiked(true);
        else
            mFavoriteLB.setLiked(false);
    }

    @Override
    public void toFollowOk(BlogDetail blogDetail) {
        if (blogDetail.getAuthorRelation() <= 2) {
            mFollowBtn.setText("已关注");
        } else {
            mFollowBtn.setText("关注");
        }
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
