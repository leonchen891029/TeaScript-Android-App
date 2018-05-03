package com.teacore.teascript.module.quickoption;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.teacore.teascript.R;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.module.quickoption.activity.FindUserActivity;
import com.teacore.teascript.module.quickoption.activity.ShakeActivity;
import com.teacore.teascript.module.quickoption.activity.TeatimePubActivity;
import com.teacore.teascript.team.fragment.NotebookEditFragment;
import com.teacore.teascript.util.UiUtils;

/**
 * 底部中间按键弹出的quickoptiondialog
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-16
 */

public class QuickOptionDialog extends Dialog implements View.OnClickListener{

    private OnQuickOptionClickListener mListener;

    public interface OnQuickOptionClickListener{
        void onQuickOptionClick(int id);
    }

    private QuickOptionDialog(Context context,boolean flag,OnCancelListener listener){
        super(context,flag,listener);
    }

    private QuickOptionDialog(Context context,int defStyle){
        super(context,defStyle);
        View contentView=View.inflate(context, R.layout.dialog_quick_option,null);
        contentView.findViewById(R.id.qo_text_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_album_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_photo_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_voice_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_scan_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_software_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_find_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_city_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_shake_ll).setOnClickListener(this);
        contentView.findViewById(R.id.qo_note_ll).setOnClickListener(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                QuickOptionDialog.this.dismiss();
                return true;
            }
        });

        super.setContentView(contentView);
    }

    public QuickOptionDialog(Context context) {
        this(context, R.style.quick_option_dialog);
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager wm=getWindow().getWindowManager();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.width=displayMetrics.widthPixels;
        getWindow().setAttributes(lp);
    }

    public void setOnQuickOptionClickListener(OnQuickOptionClickListener listener){
        this.mListener=listener;
    }

    @Override
    public void onClick(View view){
        int id=view.getId();
        switch (id) {
            case R.id.qo_text_ll:
                onClickTeatimePub(R.id.qo_text_ll);
                break;
            case R.id.qo_album_ll:
                onClickTeatimePub(R.id.qo_album_ll);
                break;
            case R.id.qo_photo_ll:
                onClickTeatimePub(R.id.qo_photo_ll);
                break;
            case R.id.qo_voice_ll:
                UiUtils.showSimpleBack(getContext(), BackFragmentEnum.VOICE_TEATIME);
                break;
            case R.id.qo_scan_ll:
                UiUtils.showScanActivity(getContext());
                break;
            case R.id.qo_software_ll:
                UiUtils.showSimpleBack(getContext(), BackFragmentEnum.OPENSOURCE_SOFTWARE);
                break;
            case R.id.qo_find_ll:
                showFindUser();
                break;
            case R.id.qo_city_ll:
                UiUtils.showSimpleBack(getContext(), BackFragmentEnum.SAME_CITY);
                break;
            case R.id.qo_note_ll:
                onClickNote();
                break;
            case R.id.qo_shake_ll:
                showShake();
                break;
            default:
                break;
        }
        if(mListener!=null){
            mListener.onQuickOptionClick(id);
        }
        dismiss();
    }

    private void showFindUser() {
        Intent intent = new Intent();
        intent.setClass(getOwnerActivity(), FindUserActivity.class);
        getOwnerActivity().startActivity(intent);
    }

    private void showShake() {
        Intent intent = new Intent();
        intent.setClass(getOwnerActivity(), ShakeActivity.class);
        getOwnerActivity().startActivity(intent);
    }

    private void onClickTeatimePub(int id){
        int type = -1;
        switch (id) {
            case R.id.qo_album_ll:
                type = TeatimePubActivity.ACTION_TYPE_ALBUM;
                break;
            case R.id.qo_photo_ll:
                type = TeatimePubActivity.ACTION_TYPE_PHOTO;
                break;
            default:
                break;
        }
        UiUtils.showTeatimeActivity(getContext(), type, null);
    }

    private void onClickNote() {
        Bundle bundle = new Bundle();
        bundle.putInt(NotebookEditFragment.NOTEBOOK_WHEREFROM_KEY,
                NotebookEditFragment.QUICK_DIALOG);
        UiUtils.showSimpleBack(getContext(), BackFragmentEnum.NOTE_EDIT, bundle);
    }

}
