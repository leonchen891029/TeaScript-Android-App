package com.teacore.teascript.widget.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.bean.Report;
import com.teacore.teascript.util.TDevice;

/**
 * 举报对话框
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */

public class ReportDialog extends BaseDialog implements View.OnClickListener{

    private TextView mReasonTV;
    private TextView mUrlTV;
    private EditText mContentET;
    private String[] reasons;
    private int reasonIndex;
    private String mUrl;
    private long mObjId;
    private byte mObjType;


    public ReportDialog(Context context, String url, long objId, byte objType) {
        this(context, R.style.dialog_common, url, objId, objType);
    }

    private ReportDialog(Context context, int defStyle, String url,
                         long objId, byte objType) {
        super(context, defStyle);
        this.mUrl = url;
        this.mObjId = objId;
        this.mObjType = objType;
        initViews(context);
    }

    private ReportDialog(Context context, boolean flag,
                         OnCancelListener listener) {
        super(context, flag, listener);
    }

    private void initViews(Context context){
        reasons=getContext().getResources().getStringArray(R.array.report_reason);

        View view=getLayoutInflater().inflate(R.layout.dialog_report,null);

        mReasonTV=(TextView) view.findViewById(R.id.reason_tv);
        mReasonTV.setText(reasons[0]);
        mReasonTV.setOnClickListener(this);

        mUrlTV=(TextView) view.findViewById(R.id.link_tv);
        mUrlTV.setText(mUrl);

        mContentET=(EditText) view.findViewById(R.id.content_et);
        super.setContent(view,0);
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.reason_tv){
            selectReason();
        }
    }

    AlertDialog alertDialog=null;
    private void selectReason() {
        alertDialog = DialogUtils.getSingleChoiceDialog(getContext(), "举报原因", reasons, reasonIndex, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mReasonTV.setText(reasons[i]);
                reasonIndex = i;
                alertDialog.dismiss();
            }
        }).show();
    }

    public Report getReport(){
        String text=mContentET.getText().toString();
        TDevice.hideSoftKeyboard(mContentET);

        Report report=new Report();
        report.setObjId(mObjId);
        report.setObjType(mObjType);
        report.setUrl(mUrl);
        report.setReason(reasonIndex);
        report.setOtherReason(text);
        return report;
    }


}
