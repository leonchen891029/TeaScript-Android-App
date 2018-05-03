package com.teacore.teascript.module.general.detail.fragment;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.EventApplyData;
import com.teacore.teascript.module.general.bean.Event;
import com.teacore.teascript.module.general.bean.EventDetail;
import com.teacore.teascript.module.general.detail.constract.EventDetailContract;
import com.teacore.teascript.module.general.widget.EventApplyDialog;
import com.teacore.teascript.util.UiUtils;

/**
 * 与EventDetailActivity相关联的Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-11
 */

public class EventDetailFragment extends DetailFragment<EventDetail,EventDetailContract.BaseView,EventDetailContract.Operator>
       implements EventDetailContract.BaseView, View.OnClickListener{

    private ImageView eventImgIV;
    private TextView eventTitleTV;
    private TextView eventOriginatorTV;
    private TextView eventTypeTV;
    private TextView eventMemberTV;
    private TextView eventStatusTV;
    private TextView eventPubDateTV;
    private TextView eventCostTV;
    private TextView eventLocationTV;
    private LinearLayout mFavoriteLL;
    private ImageView mFavoriteIV;
    private TextView mFavoriteTV;
    private LinearLayout mSignLL;
    private ImageView mSignIV;
    private TextView mApplyStatusTV;

    private EventApplyDialog mApplyDialog;

    @Override
    protected int getLayoutId(){
        return R.layout.general_fragment_event_detail;
    }

    @Override
    protected void initView(View view){
        super.initView(view);

        eventImgIV=(ImageView) view.findViewById(R.id.event_img_iv);
        eventTitleTV=(TextView) view.findViewById(R.id.event_title_tv);
        eventOriginatorTV=(TextView) view.findViewById(R.id.event_originator_tv);
        eventTypeTV=(TextView) view.findViewById(R.id.event_type_tv);
        eventMemberTV=(TextView) view.findViewById(R.id.event_member_tv);
        eventStatusTV=(TextView) view.findViewById(R.id.event_status_tv);
        eventPubDateTV=(TextView) view.findViewById(R.id.event_pub_date_tv);
        eventCostTV=(TextView) view.findViewById(R.id.event_cost_tv);
        eventLocationTV=(TextView) view.findViewById(R.id.event_location_tv);
        mFavoriteLL=(LinearLayout) view.findViewById(R.id.favorite_ll);
        mFavoriteIV=(ImageView) view.findViewById(R.id.favorite_iv);
        mFavoriteTV=(TextView) view.findViewById(R.id.favorite_tv);
        mSignLL=(LinearLayout) view.findViewById(R.id.sign_ll);
        mSignIV=(ImageView) view.findViewById(R.id.sign_iv);
        mApplyStatusTV=(TextView) view.findViewById(R.id.apply_status_tv);

        mFavoriteLL.setOnClickListener(this);
        mSignLL.setOnClickListener(this);
    }

    @Override
    protected void initData(){

        final EventDetail mDetail = mPresenter.getData();
        if (mDetail == null) return;

        getImageLoader().load(mDetail.getImg()).into(eventImgIV);
        eventTitleTV.setText(mDetail.getTitle());
        eventOriginatorTV.setText(String.format("发起人：%s", mDetail.getAuthor()));
        eventMemberTV.setText(String.format("%s人参与", mDetail.getApplyCount()));
        eventCostTV.setText(mDetail.getCostDesc());
        eventLocationTV.setText(mDetail.getSpot());
        eventPubDateTV.setText(mDetail.getPubDate());

        mFavoriteIV.setImageResource(mDetail.isFavorite() ? R.drawable.icon_favorited : R.drawable.icon_unfavorite);
        mFavoriteTV.setText(mDetail.isFavorite() ? getResources().getString(R.string.event_is_fav) : getResources().getString(R.string.event_un_fav));

        switch (mDetail.getStatus()) {
            case Event.STATUS_END:
                eventStatusTV.setText(getResources().getString(R.string.event_status_end));
                break;
            case Event.STATUS_ING:
                eventStatusTV.setText(getResources().getString(R.string.event_status_ing));
                break;
            case Event.STATUS_DEADLINE_FOR_REGISTER:
                eventStatusTV.setText(getResources().getString(R.string.event_status_dfr));
                break;
        }

        int typeStr = R.string.tssite;
        switch (mDetail.getType()) {
            case Event.EVENT_TYPE_TEASCRIPT:
                typeStr = R.string.event_type_teascript;
                break;
            case Event.EVENT_TYPE_TEC:
                typeStr = R.string.event_type_tec;
                break;
            case Event.EVENT_TYPE_OTHER:
                typeStr = R.string.event_type_other;
                break;
            case Event.EVENT_TYPE_OUTSIDE:
                typeStr = R.string.event_type_outside;
                break;
        }
        eventTypeTV.setText(String.format("类型：%s", getResources().getString(typeStr)));
        mApplyStatusTV.setText(getResources().getString(getApplyStatusStrId(mDetail.getApplyStatus())));

        if(mDetail.getApplyStatus() != EventDetail.APPLY_STATUS_UNSIGN){
            setSignUnable();
        }

        setBodyContent(mDetail.getBody());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorite_ll:
                mPresenter.toFavorite();
                break;
            case R.id.sign_ll:
                final EventDetail mDetail = mPresenter.getData();
                if (mDetail.getApplyStatus() == EventDetail.APPLY_STATUS_UNSIGN && mDetail.getStatus() == Event.STATUS_ING) {
                    if (AppContext.getInstance().isLogin()) {
                        if (mApplyDialog == null) {
                            mApplyDialog = new EventApplyDialog(getActivity(), mDetail);
                            mApplyDialog.setCanceledOnTouchOutside(true);
                            mApplyDialog.setCancelable(true);
                            mApplyDialog.setTitle("活动报名");
                            mApplyDialog.setCanceledOnTouchOutside(true);
                            mApplyDialog.setNegativeButton(R.string.cancle, null);
                            mApplyDialog.setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface d, int which) {
                                            EventApplyData data;
                                            if ((data = mApplyDialog.getApplyData()) != null) {
                                                data.setEventId(Integer.parseInt(String.valueOf(mDetail.getId())));
                                                data.setUserId(AppContext.getInstance()
                                                        .getLoginUid());
                                                mPresenter.toSignUp(data);
                                            }

                                        }
                                    });
                        }
                        mApplyDialog.show();
                    } else {
                        UiUtils.showLoginActivity(getActivity());
                    }
                }
                break;
        }
    }

    //添加收藏成功
    @Override
    public void toFavoriteOk(EventDetail detail) {
        mPresenter.getData().setFavorite(detail.isFavorite());
        final EventDetail mDetail = mPresenter.getData();
        mFavoriteIV.setImageResource(mDetail.isFavorite() ? R.drawable.icon_favorited : R.drawable.icon_unfavorite);
        mFavoriteTV.setText(mDetail.isFavorite() ? getResources().getString(R.string.event_is_fav) : getResources().getString(R.string.event_un_fav));
    }

    //报名成功
    @Override
    public void toSignUpOk(EventDetail detail) {
        mPresenter.getData().setApplyStatus(detail.getApplyStatus());
        final EventDetail mDetail = mPresenter.getData();
        mApplyStatusTV.setText(getResources().getString(getApplyStatusStrId(mDetail.getApplyStatus())));
        setSignUnable();
        mApplyDialog.dismiss();
    }

    public int getApplyStatusStrId(int status) {
        int strId = R.string.event_status_ing;
        switch (status) {
            case EventDetail.APPLY_STATUS_UNSIGN:
                strId = R.string.event_apply_status_unsign;
                break;
            case EventDetail.APPLY_STATUS_AUDIT:
                strId = R.string.event_apply_status_audit;
                break;
            case EventDetail.APPLY_STATUS_CONFIRMED:
                strId = R.string.event_apply_status_confirmed;
                break;
            case EventDetail.APPLY_STATUS_PRESENTED:
                strId = R.string.event_apply_status_presented;
                break;
            case EventDetail.APPLY_STATUS_CANCELED:
                strId = R.string.event_apply_status_canceled;
                break;
            case EventDetail.APPLY_STATUS_REFUSED:
                strId = R.string.event_apply_status_refused;
                break;
        }
        return strId;
    }

    private void setSignUnable(){
        mApplyStatusTV.setEnabled(false);
        mSignLL.setEnabled(false);
        mSignIV.setEnabled(false);
    }


}
