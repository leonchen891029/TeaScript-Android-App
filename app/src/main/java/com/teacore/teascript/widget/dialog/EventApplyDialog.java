package com.teacore.teascript.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;
import com.teacore.teascript.bean.Event;
import com.teacore.teascript.bean.EventApplyData;

import java.util.List;

import static com.teacore.teascript.widget.dialog.DialogUtils.getSelectDialog;

/**活动申请Dialog
 * @陈晓帆
 * @version
 * Created 2017-3-20
*/
public class EventApplyDialog extends BaseDialog implements View.OnClickListener{

    private EditText mNameET;
    private TextView mGenderTV;
    private String[] genders;
    private EditText mPhoneET;
    private EditText mCompanyET;
    private EditText mJobET;
    private TextView mRemarksTipTV;
    private TextView mRemarksSelectorTV;


    private Event mEvent;

    //无Event参数
    private EventApplyDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    //这个private构造函数供下面的public构造函数使用
    private EventApplyDialog(Context context, int defStyle, Event event) {
        super(context, defStyle);
        this.mEvent = event;
        View shareView = View.inflate(context, R.layout.dialog_event_apply, null);
        setContent(shareView, 0);
        initView(shareView);
    }

    public EventApplyDialog(Context context, Event event) {
        this(context, R.style.dialog_bottom, event);
    }

    private void initView(View shareView){
        mNameET=(EditText)  shareView.findViewById(R.id.name_et);
        mGenderTV=(TextView) shareView.findViewById(R.id.gender_tv);
        mPhoneET=(EditText) shareView.findViewById(R.id.phone_et);
        mCompanyET=(EditText) shareView.findViewById(R.id.company_et);
        mJobET=(EditText) shareView.findViewById(R.id.job_et);
        mRemarksTipTV=(TextView) shareView.findViewById(R.id.remarks_tip_tv);
        mRemarksSelectorTV=(TextView) shareView.findViewById(R.id.remarks_selector_tv);

        genders=getContext().getResources().getStringArray(R.array.gender);
        mGenderTV.setText(genders[0]);
        mGenderTV.setOnClickListener(this);

        if (mEvent.getEventRemark() != null) {
            mRemarksTipTV.setVisibility(View.VISIBLE);
            mRemarksTipTV.setText(mEvent.getEventRemark().getRemarkTip());

            mRemarksSelectorTV.setVisibility(View.VISIBLE);
            mRemarksSelectorTV.setOnClickListener(this);
            mRemarksSelectorTV.setText(mEvent.getEventRemark().getSelect().getList().get(0));
        }

    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        switch (id) {
            case R.id.gender_tv:
                selectGender();
                break;
            case R.id.remarks_selector_tv:
                selectRemark();
                break;
            default:
                break;
        }

    }

    private void selectGender() {
        String gender = mGenderTV.getText().toString();

        for (String gender1 : genders) {
            if (gender1.equals(gender)) {
                return;
            }
        }

        getSelectDialog(getContext(), genders, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mGenderTV.setText(genders[i]);
            }
        }).show();
    }

    private void selectRemark() {
        List<String> stringList = mEvent.getEventRemark().getSelect().getList();
        final String[] remarkSelects = new String[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            remarkSelects[i] = stringList.get(i);
        }
        //第二个参数是一个数组
        DialogUtils.getSelectDialog(getContext(), remarkSelects, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRemarksSelectorTV.setText(remarkSelects[i]);
            }
        }).show();
    }

    public EventApplyData getEventApplyData() {
        String name = mNameET.getText().toString();
        String gender = mGenderTV.getText().toString();
        String phone = mPhoneET.getText().toString();
        String company = mCompanyET.getText().toString();
        String job = mJobET.getText().toString();
        String remark = mRemarksSelectorTV.getText().toString();

        if (TextUtils.isEmpty(name)) {
            AppContext.showToast("请填写姓名");
            return null;
        }

        if (TextUtils.isEmpty(phone)) {
            AppContext.showToast("请填写电话");
            return null;
        }

        if (mEvent.getEventRemark() != null && TextUtils.isEmpty(remark)) {
            AppContext.showToast("请" + mEvent.getEventRemark().getRemarkTip());
            return null;
        }

        EventApplyData data = new EventApplyData();

        data.setName(name);
        data.setGender(gender);
        data.setPhone(phone);
        data.setCompany(company);
        data.setJob(job);
        data.setRemark(remark);

        return data;
    }




}
