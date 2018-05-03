package com.teacore.teascript.team.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseHeaderListFragment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.adapter.TeamReplyAdapter;
import com.teacore.teascript.team.bean.TeamActive;
import com.teacore.teascript.team.bean.TeamActiveDetail;
import com.teacore.teascript.team.bean.TeamReply;
import com.teacore.teascript.team.bean.TeamReplyList;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.OnSendClickListener;

import net.qiujuer.genius.ui.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**Team动态详情界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamTeatimeDetailFragment extends BaseHeaderListFragment<TeamReply,TeamActiveDetail> implements
        OnItemClickListener,OnItemLongClickListener,OnSendClickListener{

    private static final String CACHE_KEY_PREFIX="team_Teatime_";

    private AvatarView mHeadAV;
    private TextView mNameTV;
    private TextView mCommentCountTV;
    private TeatimeTextView mContentTTV;
    private TextView mClientTV;
    private ImageView mPicIV;
    private TextView mDateTV;

    private TeamActive mTeamActive;
    private int mTeamId;
    private static int rectSize;

    private DetailActivity detailActivity;
    private RequestManager mImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mImageLoader= Glide.with(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        /*
        使用SimpleBackActivity传递Bundle时候使用
        Bundle bundle=getActivity().getIntent().getBundleExtra(BackActivity.BUNDLE_KEY_ARGS);
         */
        Bundle args=getActivity().getIntent().getExtras();
        mTeamActive=(TeamActive) args.getSerializable(TeamActiveListFragment.ACTIVE_FRAGMENT_KEY);
        mTeamId=args.getInt(TeamActiveListFragment.ACTIVE_FRAGMENT_TEAM_KEY,0);

        detailActivity=(DetailActivity) getActivity();
        super.onViewCreated(view,savedInstanceState);
    }

    @Override
    protected View initHeaderView(){
        View headerView=View.inflate(getActivity(), R.layout.fragment_team_active_detail,null);
        initImageSize(activity);

        mHeadAV=(AvatarView) headerView.findViewById(R.id.avatar_av);
        mNameTV=(TextView) headerView.findViewById(R.id.name_tv);
        mCommentCountTV=(TextView) headerView.findViewById(R.id.comment_count_tv);
        mContentTTV=(TeatimeTextView) headerView.findViewById(R.id.content_ttv);
        mClientTV=(TextView) headerView.findViewById(R.id.from_tv);
        mDateTV=(TextView) headerView.findViewById(R.id.time_tv);
        mPicIV=(ImageView) headerView.findViewById(R.id.pic_iv);

        mContentTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
        mContentTTV.setFocusable(false);
        mContentTTV.setDispatchToParent(true);
        mContentTTV.setLongClickable(false);
        Spanned span= Html.fromHtml(mTeamActive.getBody().getDetail().trim(),FROM_HTML_MODE_LEGACY);
        MyURLSpan.parseLinkText(mContentTTV,span);

        mHeadAV.setAvatarUrl(mTeamActive.getAuthor().getPortrait());
        mNameTV.setText(mTeamActive.getAuthor().getName());
        mDateTV.setText(TimeUtils.friendly_time(mTeamActive.getCreateTime()));
        mClientTV.setText("Android");
        mClientTV.setVisibility(View.GONE);

        String imagePath=mTeamActive.getBody().getImage();
        if(!StringUtils.isEmpty(imagePath)){
            mPicIV.setVisibility(View.VISIBLE);
            setTeatimeImage(mPicIV,imagePath,mTeamActive.getBody().getImageOrigin());
        }else{
            mPicIV.setVisibility(View.GONE);
        }

        return headerView;
    }

    //处理评论
    private void handleComment(String text){
        showWaitDialog(R.string.progress_submit);
        if(!AppContext.getInstance().isLogin()){
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        TeaScriptTeamApi.pubTeamTeatimeComment(mTeamId,mTeamActive.getType(),mTeamActive.getId(),text,mCommentHandler);
    }

    //删除评论
    private void handleDeleteComment(TeamReply teamReply){
        if(!AppContext.getInstance().isLogin()){
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        AppContext.showToast(R.string.deleting);
        TeaScriptApi.deleteComment(mTeamActive.getId(), CommentList.CATALOG_Teatime,teamReply.getId(),teamReply.getAuthor().getId(),
                new OperationResponseHandler(teamReply));
    }

    //初始化图片的大小
    private void initImageSize(Context context){
        if (context != null && rectSize == 0) {
            rectSize =(int) context.getResources().getDimension(R.dimen.space_100);
        } else {
            rectSize = 300;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        detailActivity.emojiFragment.hideFlagButton();
    }

    /*
    设置图片显示样式
    @param url 缩略图地址
    @param realUrl 点击图片后要显示的大图图片地址
     */
    private void setTeatimeImage(final ImageView pic,final String url,final String realUrl){
        pic.setVisibility(View.VISIBLE);

        GlideImageLoader.loadImage(mImageLoader,pic,url,R.drawable.pic_bg);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewImageActivity.showImagePreview(activity,realUrl);
            }
        });

    }

    @Override
    protected void requestDetailData(boolean isRefresh){
        TeaScriptApi.getDynamicDetail(mTeamActive.getId(),mTeamId,AppContext.getInstance().getLoginUid(),mDetailHander);
    }

    @Override
    protected String getDetailCacheKey(){
        return CACHE_KEY_PREFIX+mTeamActive.getId();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTeamActive.getId() + mCurrentPage;
    }

    @Override
    protected TeamReplyList readList(Serializable seri){
        super.readList(seri);
        return (TeamReplyList) seri;
    }

    @Override
    protected EntityList<TeamReply> parseList(InputStream inputStream) throws Exception{
        super.parseList(inputStream);
        TeamReplyList list= XmlUtils.toBean(TeamReplyList.class,inputStream);
        return list;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamActiveDetail detailBean){
        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        this.mTeamActive=detailBean.getTeamActive();
        //stripTags取出html标签
        mContentTTV.setText(stripTags(this.mTeamActive.getBody().getTitle()));
        Spanned spanned=Html.fromHtml(this.mTeamActive.getBody().getDetail(),FROM_HTML_MODE_LEGACY);
        MyURLSpan.parseLinkText(mContentTTV,spanned);
        mAdapter.setNoDataText(R.string.comment_empty);
    }

    @Override
    protected TeamActiveDetail getDetailBean(ByteArrayInputStream inputStream){
        return XmlUtils.toBean(TeamActiveDetail.class,inputStream);
    }

    @Override
    protected TeamReplyAdapter getListAdapter(){
        return new TeamReplyAdapter();
    }

    @Override
    protected void sendRequestData(){
        TeaScriptTeamApi.getTeamCommentList(mTeamId,mTeamActive.getId(),mCurrentPage,mHandler);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){

        if(position-1==-1){
            return false;
        }
        final TeamReply item=mAdapter.getItem(position-1);
        if(item==null)
            return false;
        int len=item.getAuthor().getId()==AppContext.getInstance().getLoginUid()?2:1;
        String[] items=new String[len];
        items[0]=getResources().getString(R.string.copy);
        if(len==2){
            items[1]=getResources().getString(R.string.delete);
        }
        DialogUtils.getSelectDialog(getActivity(),items,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(item
                            .getContent()));
                } else if (i == 1) {
                    handleDeleteComment(item);
                }
            }
        }).show();
        return true;
    }

    class DeleteOperationResponseHandler extends OperationResponseHandler{

        DeleteOperationResponseHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args) {
            try {
                Result res = XmlUtils.toBean(ResultData.class, is).getResult();
                if (res.OK()) {
                    AppContext.showToast(R.string.delete_success);
                    mAdapter.removeItem((TeamReply) args[0]);
                } else {
                    AppContext.showToast(res.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailureOperation(code, e.getMessage(), args);
            }
        }

        @Override
        public void onFailureOperation(int code, String errorMessage, Object[] args) {
            AppContext.showToast(R.string.delete_fail);
        }
    }

    private final AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            onRefresh();
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            AppContext.showToast(R.string.comment_publish_faile);
        }

        @Override
        public void onFinish() {
            hideWaitDialog();
        }

        ;
    };

    @Override
    protected void executeOnLoadDataSuccess(List<TeamReply> data) {
        super.executeOnLoadDataSuccess(data);
        if (mCommentCountTV != null && data != null) {
            mCommentCountTV.setText((mAdapter.getCount() - 1) + "");
        }
    }

    //移除字符串中的html标签
    public static Spanned stripTags(final String string) {
        // String str = pHTMLString.replaceAll("\\<.*?>", "");
        String str = string.replaceAll("\\t*", "");
        str = str.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "  ");
        return Html.fromHtml(str,FROM_HTML_MODE_LEGACY);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (position < 1) { // header view
            return;
        }
        final TeamReply comment = mAdapter.getItem(position - 1);
        if (comment == null) {
            return;
        }
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        if (TextUtils.isEmpty(str)) {
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }
        handleComment(str.toString());
    }

    @Override
    public void onClickFlagButton() {
    }




}
