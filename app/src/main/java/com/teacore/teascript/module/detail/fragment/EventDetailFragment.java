package com.teacore.teascript.module.detail.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseDetailFragment;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.Event;
import com.teacore.teascript.bean.EventApplyData;
import com.teacore.teascript.bean.FavoriteList;
import com.teacore.teascript.bean.Post;
import com.teacore.teascript.bean.PostDetail;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.UrlUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.dialog.EventApplyDialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

/**活动详情Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */

public class EventDetailFragment extends BaseDetailFragment<Post>{

    private TextView mTitleTV;
    private TextView mStartTimeTV;
    private TextView mEndTimeTV;
    private TextView mLocationTV;

    private Button mAttendBtn;
    private Button mApplyBtn;

    private TextView mEventTipTV;

    private EventApplyDialog mEventApplyDialog;

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_event_detail;
    }

    @Override
    public void initView(View view){
        super.initView(view);
        mTitleTV=(TextView) view.findViewById(R.id.event_title_tv);
        mStartTimeTV=(TextView) view.findViewById(R.id.event_start_time_tv);
        mEndTimeTV=(TextView) view.findViewById(R.id.event_end_time_tv);
        mLocationTV=(TextView) view.findViewById(R.id.event_location_tv);
        mAttendBtn=(Button) view.findViewById(R.id.event_attend_btn);
        mApplyBtn=(Button) view.findViewById(R.id.event_apply_btn);

        mAttendBtn.setOnClickListener(this);
        mApplyBtn.setOnClickListener(this);
    }

    @Override
    protected String getCacheKey(){
        return "post_"+mId;
    }

    @Override
    protected void sendRequestDataForNet() {
        TeaScriptApi.getPostDetail(mId, mDetailHandler);
    }

    @Override
    protected Post parseData(InputStream inputStream) {
        return XmlUtils.toBean(PostDetail.class, inputStream).getPost();
    }

    @Override
    protected String getWebViewBody(Post detail) {
        StringBuilder body = new StringBuilder();
        body.append(UiUtils.WEB_STYLE).append(UiUtils.WEB_LOAD_IMAGES);
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        // 添加title
        body.append(String.format("<div class='title'>%s</div>", mDetail.getTitle()));
        // 添加作者和时间
        String time = TimeUtils.friendly_time(mDetail.getPubDate());
        String author = String.format
                ("<a class='author' href='http://my.oschina.net/u/%s'>%s</a>", mDetail
                        .getAuthorId(), mDetail.getAuthor());
        body.append(String.format("<div class='authortime'>%s&nbsp;&nbsp;&nbsp;&nbsp;%s</div>",
                author, time));
        // 添加图片点击放大支持
        body.append(UiUtils.setHtmlCotentImagePreview(mDetail.getBody()));
        // 封尾
        body.append("</div></body>");
        return body.toString();
    }

    @Override
    protected void executeOnLoadDataSuccess(Post detail) {

        super.executeOnLoadDataSuccess(detail);
        mTitleTV.setText(mDetail.getTitle());
        mStartTimeTV.setText(String.format(
                getString(R.string.event_start_time), mDetail.getEvent()
                        .getStartTime()));
        mEndTimeTV.setText(String.format(getString(R.string.event_end_time),
                mDetail.getEvent().getEndTime()));

        mLocationTV.setText(String.format(getString(R.string.event_location), mDetail.getEvent().getCity() + " "
                + mDetail.getEvent().getSpot()));

        // 站外活动
        if (mDetail.getEvent().getCategory() == 4) {
            mApplyBtn.setVisibility(View.VISIBLE);
            mApplyBtn.setText("报名链接");
            mAttendBtn.setVisibility(View.GONE);
        } else {
            notifyEventStatus();
        }
    }

    // 显示活动 以及报名的状态
    private void notifyEventStatus() {
        int eventStatus = mDetail.getEvent().getStatus();
        int applyStatus = mDetail.getEvent().getApplyStatus();

        if (applyStatus == Event.APPLYSTATUS_ATTEND) {
            mAttendBtn.setVisibility(View.VISIBLE);
        } else {
            mAttendBtn.setVisibility(View.GONE);
        }

        if (eventStatus == Event.EVENT_STATUS_APPLYING) {
            mApplyBtn.setVisibility(View.VISIBLE);
            mApplyBtn.setEnabled(false);
            switch (applyStatus) {
                case Event.APPLYSTATUS_CHECKING:
                    mApplyBtn.setText("待确认");
                    break;
                case Event.APPLYSTATUS_CHECKED:
                    mApplyBtn.setText("已确认");
                    mApplyBtn.setVisibility(View.GONE);
                    mEventTipTV.setVisibility(View.VISIBLE);
                    break;
                case Event.APPLYSTATUS_ATTEND:
                    mApplyBtn.setText("已出席");
                    break;
                case Event.APPLYSTATUS_CANCLE:
                    mApplyBtn.setText("已取消");
                    mApplyBtn.setEnabled(true);
                    break;
                case Event.APPLYSTATUS_REJECT:
                    mApplyBtn.setText("已拒绝");
                    break;
                default:
                    mApplyBtn.setText("我要报名");
                    mApplyBtn.setEnabled(true);
                    break;
            }
        } else {
            mApplyBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void showCommentView() {
        if (mDetail != null) {
            UiUtils.showComment(getActivity(), mId, CommentList.CATALOG_POST);
        }
    }

    @Override
    protected int getCommentType() {
        return CommentList.CATALOG_POST;
    }

    @Override
    protected int getFavoriteType() {
        return FavoriteList.TYPE_POST;
    }

    @Override
    protected int getFavoriteState() {
        return mDetail.getFavorite();
    }

    @Override
    protected void updateFavoriteChanged(int newFavoritedState) {
        mDetail.setFavorite(newFavoritedState);
        saveCache(mDetail);
    }

    @Override
    protected int getCommentCount() {
        return mDetail.getAnswerCount();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.event_attend_btn:
                showEventApplies();
                break;
            case R.id.event_apply_btn:
                showEventApply();
                break;
            default:
                break;
        }
    }

    private void showEventApplies() {
        Bundle args = new Bundle();
        args.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, mDetail.getEvent()
                .getId());
        UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.EVENT_APPLY, args);
    }

    private final AsyncHttpResponseHandler mApplyHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            Result rs = XmlUtils.toBean(ResultData.class,
                    new ByteArrayInputStream(arg2)).getResult();

            if (rs.OK()) {
                AppContext.showToast("报名成功");
                mEventApplyDialog.dismiss();
                mDetail.getEvent().setApplyStatus(Event.APPLYSTATUS_CHECKING);
            } else {
                AppContext.showToast(rs.getMessage());
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            AppContext.showToast("报名失败");
        }

        @Override
        public void onFinish() {
            hideWaitDialog();
        }
    };

    /**
     * 显示活动报名对话框
     */
    private void showEventApply() {

        if (mDetail.getEvent().getCategory() == 4) {
            UiUtils.openSystemBrowser(getActivity(), mDetail.getEvent().getUrl());
            return;
        }

        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }

        if (mEventApplyDialog == null) {
            mEventApplyDialog = new EventApplyDialog(getActivity(), mDetail.getEvent());
            mEventApplyDialog.setCanceledOnTouchOutside(true);
            mEventApplyDialog.setCancelable(true);
            mEventApplyDialog.setTitle("活动报名");
            mEventApplyDialog.setCanceledOnTouchOutside(true);
            mEventApplyDialog.setNegativeButton(R.string.cancle, null);
            mEventApplyDialog.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface d, int which) {
                            EventApplyData data = null;
                            if ((data = mEventApplyDialog.getEventApplyData()) != null) {
                                data.setEventId(mId);
                                data.setUserId(AppContext.getInstance()
                                        .getLoginUid());
                                showWaitDialog(R.string.progress_submit);
                                TeaScriptApi.eventApply(data, mApplyHandler);
                            }
                        }
                    });
        }

        mEventApplyDialog.show();
    }

    @Override
    protected String getShareTitle() {
        return mDetail.getTitle();
    }

    @Override
    protected String getShareContent() {
        return StringUtils.getSubstring(0, 55,
                getFilterHtmlBody(mDetail.getBody()));
    }

    @Override
    protected String getShareUrl() {
        return String.format(UrlUtils.URL_MOBILE + "question/%s_%s", mDetail.getAuthorId(), mId);
    }



}
