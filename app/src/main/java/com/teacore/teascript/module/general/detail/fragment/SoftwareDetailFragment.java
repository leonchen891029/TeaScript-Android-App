package com.teacore.teascript.module.general.detail.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.module.general.bean.SoftwareDetail;
import com.teacore.teascript.module.general.detail.constract.SoftwareDetailContract;
import com.teacore.teascript.module.general.widget.AboutRecommendView;
import com.teacore.teascript.util.UiUtils;

/**
 * 与SoftwareDetailActivity相关联的Fragment
 * @author 陈晓帆
 * @version 1.0
 */

public class SoftwareDetailFragment extends DetailFragment<SoftwareDetail,SoftwareDetailContract.SoftwareView,SoftwareDetailContract.SoftwarePresenter>
       implements SoftwareDetailContract.SoftwareView,View.OnClickListener{

    private long mId;

    private ImageView mRecommendIV;
    private ImageView mIconIV;
    private TextView mSoftNameTV;
    private TextView mHomeBtn;
    private TextView mDocumentsBtn;
    private TextView mAuthorNameTV;
    private TextView mProtocolTV;
    private TextView mLanguageTV;
    private TextView mSystemTV;
    private TextView mRecordTimeTV;
    private AboutRecommendView mAboutView;
    private LinearLayout mCommentLL;
    private TextView mCommentCountTV;
    private LinearLayout mFavoriteLL;
    private ImageView mFavoriteIV;
    private LinearLayout mShareLL;

    @Override
    protected int getLayoutId(){
        return R.layout.general_fragment_software_detail;
    }

    @Override
    protected void initView(View view){
        super.initView(view);

        mRecommendIV=(ImageView) view.findViewById(R.id.recommend_iv);
        mIconIV=(ImageView) view.findViewById(R.id.software_icon_iv);
        mSoftNameTV=(TextView) view.findViewById(R.id.software_name_tv);
        mHomeBtn=(TextView) view.findViewById(R.id.software_home_btn);
        mDocumentsBtn=(TextView) view.findViewById(R.id.software_document_btn);
        mAuthorNameTV=(TextView) view.findViewById(R.id.software_author_tv);
        mProtocolTV=(TextView) view.findViewById(R.id.software_protocol_tv);
        mLanguageTV=(TextView) view.findViewById(R.id.software_language_tv);
        mSystemTV=(TextView) view.findViewById(R.id.software_system_tv);
        mRecordTimeTV=(TextView) view.findViewById(R.id.software_record_time_tv);
        mAboutView=(AboutRecommendView) view.findViewById(R.id.about_recommend_view);
        mCommentLL=(LinearLayout) view.findViewById(R.id.software_comment_ll);
        mCommentCountTV=(TextView) view.findViewById(R.id.comment_count_tv);
        mFavoriteLL=(LinearLayout) view.findViewById(R.id.favorite_ll);
        mFavoriteIV=(ImageView) view.findViewById(R.id.favorite_iv);
        mShareLL=(LinearLayout) view.findViewById(R.id.share_ll);

        mHomeBtn.setOnClickListener(this);
        mDocumentsBtn.setOnClickListener(this);
        mCommentLL.setOnClickListener(this);
        mFavoriteLL.setOnClickListener(this);
        mShareLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_ll:
                handleShare();
                break;
            case R.id.favorite_ll:
                handleFavorite();
                break;
            case R.id.software_home_btn:
                //进入官网
                UiUtils.showUrlRedirect(getActivity(), mPresenter.getData().getHomePage());
                break;
            case R.id.software_document_btn:
                //软件文档
                UiUtils.showUrlRedirect(getActivity(), mPresenter.getData().getDocument());
                break;
            case R.id.software_comment_ll:
                // 评论列表
                UiUtils.showSoftWareTeatimes(getActivity(), (int) mId);
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        final SoftwareDetail softwareDetail = mPresenter.getData();
        if (softwareDetail == null)
            return;

        mId = softwareDetail.getId();

        if (softwareDetail.isRecommend()) {
            mRecommendIV.setVisibility(View.VISIBLE);
        } else {
            mRecommendIV.setVisibility(View.INVISIBLE);
        }

        String name = softwareDetail.getName();
        String extName = softwareDetail.getExtName();
        mSoftNameTV.setText(String.format("%s%s", name, (TextUtils.isEmpty(extName)) ? "" : " " + extName.trim()));

        String author = softwareDetail.getAuthor();
        mAuthorNameTV.setText(TextUtils.isEmpty(author) ? "匿名" : author.trim());
        String license = softwareDetail.getLicense();
        mProtocolTV.setText(TextUtils.isEmpty(license) ? "无" : license.trim());
        mLanguageTV.setText(softwareDetail.getLanguage());
        mSystemTV.setText(softwareDetail.getSupportOS());
        mRecordTimeTV.setText(softwareDetail.getCollectionDate());

        setCommentCount(softwareDetail.getCommentCount());
        setBodyContent(softwareDetail.getBody());

        getImageLoader().load(softwareDetail.getLogo())
                .error(R.drawable.logo_software_default)
                .placeholder(R.drawable.logo_software_default)
                .into(mIconIV);

        toFavoriteOk(softwareDetail);

        mAboutView.setAbouts(softwareDetail.getAbouts(), 1);
    }

    @Override
    void setCommentCount(int count) {
        mCommentCountTV.setText(String.format("评论 (%s)", count));
    }

    private void handleFavorite() {
        mPresenter.toFavorite();
    }

    private void handleShare() {
        mPresenter.toShare();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(SoftwareDetail softwareDetail) {
        if (softwareDetail.isFavorite())
            mFavoriteIV.setImageDrawable(getResources().getDrawable(R.drawable.icon_favorited));
        else
            mFavoriteIV.setImageDrawable(getResources().getDrawable(R.drawable.icon_unfavorite));
    }


}
