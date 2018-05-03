package com.teacore.teascript.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.teacore.teascript.R;

/**
 * 分享界面Dialog
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class ShareMenuDialog extends Dialog implements View.OnClickListener{

    public interface OnMenuClickListener{
        void onClick(TextView menuItem);
    }

    private OnMenuClickListener mListener;

    private ShareMenuDialog(Context context,int defStyle){
        super(context,defStyle);
        View view=getLayoutInflater().inflate(R.layout.dialog_share_menu,null);
        view.findViewById(R.id.menu1).setOnClickListener(this);
        view.findViewById(R.id.menu2).setOnClickListener(this);
        view.findViewById(R.id.menu3).setOnClickListener(this);
        super.setContentView(view);
    }

    public ShareMenuDialog(Context context){
        this(context,R.style.dialog_bottom);
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        DisplayMetrics dm=new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams lp=getWindow().getAttributes();

        lp.width=dm.widthPixels;
        getWindow().setAttributes(lp);
    }

    public void setOnMenuClickListener(OnMenuClickListener listen) {
        mListener = listen;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onClick((TextView) view);
        }
    }

}
