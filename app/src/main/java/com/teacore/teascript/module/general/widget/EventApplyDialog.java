package com.teacore.teascript.module.general.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.EventApplyData;
import com.teacore.teascript.module.general.bean.EventDetail;
import com.teacore.teascript.widget.dialog.BaseDialog;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 综合下的活动申请对话框
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-13
 */

public class EventApplyDialog extends BaseDialog implements View.OnClickListener{

    private EditText mNameET;
    private RadioButton mMaleRB;
    private EditText mPhoneET;
    private EditText mCompanyET;
    private EditText mJobET;
    //备注提示
    private TextView mRemarkTipTV;
    //备注选择
    private TextView mRemarkSelectorTV;

    private String[] genders;

    private EventDetail mEvent;

    private EventApplyDialog(Context context, int defStyle, EventDetail event){
        super(context,defStyle);
        View view=View.inflate(context, R.layout.dialog_event_detail_apply,null);
        setContent(view,0);
        this.mEvent=event;
        initView(view);
    }

    public EventApplyDialog(Context context, EventDetail event) {
        this(context, R.style.dialog_bottom, event);
    }

    private void initView(View view) {
        mNameET=(EditText) view.findViewById(R.id.name_et);
        mMaleRB=(RadioButton) view.findViewById(R.id.male_rb);
        mPhoneET=(EditText) view.findViewById(R.id.phone_et);
        mCompanyET=(EditText) view.findViewById(R.id.company_et);
        mJobET=(EditText) view.findViewById(R.id.job_et);
        mRemarkTipTV=(TextView) view.findViewById(R.id.remarks_tip_tv);
        mRemarkSelectorTV=(TextView) view.findViewById(R.id.remarks_selector_tv);

        genders = getContext().getResources().getStringArray(R.array.gender);
        mMaleRB.setChecked(true);

        if (mEvent.getRemark() != null) {
            mRemarkTipTV.setVisibility(View.VISIBLE);
            mRemarkTipTV.setText(mEvent.getRemark().getTip());
            mRemarkTipTV.setVisibility(View.VISIBLE);

            mRemarkSelectorTV.setOnClickListener(this);
            String[] selects = mEvent.getRemark().getSelect().split(",");
            mRemarkSelectorTV.setText(selects.length > 0 ? selects[0] : mEvent.getRemark().getSelect());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.remarks_selector_tv:
                selectRemarkSelector();
                break;
            default:
                break;
        }
    }


    private void selectRemarkSelector() {
        List<String> stringList = Arrays.asList(mEvent.getRemark().getSelect().split(","));
        final String[] remarkSelects = new String[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            remarkSelects[i] = stringList.get(i);
        }
        DialogUtils.getSelectDialog(getContext(), remarkSelects, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRemarkSelectorTV.setText(remarkSelects[i]);
            }
        }).show();
    }

    public EventApplyData getApplyData() {
        String name = mNameET.getText().toString();
        String gender = genders[mMaleRB.isChecked() ? 0 : 1];
        String phone = mPhoneET.getText().toString();
        String company = mCompanyET.getText().toString();
        String job = mJobET.getText().toString();
        String remark = mRemarkSelectorTV.getText().toString();

        if (TextUtils.isEmpty(name)) {
            AppContext.showToast("请填写姓名");
            return null;
        }

        if (TextUtils.isEmpty(phone)) {
            AppContext.showToast("请填写电话");
            return null;
        }

        if (mEvent.getRemark() != null && TextUtils.isEmpty(remark)) {
            AppContext.showToast("请" + mEvent.getRemark().getTip());
            return null;
        }

        EventApplyData data = new EventApplyData();

        data.setName(name);
        data.setGender(gender);
        data.setPhone(phone);
        data.setCompany(company);
        data.setJob(job);
        data.setRemark(remark);
        dismiss();
        return data;
    }

}
