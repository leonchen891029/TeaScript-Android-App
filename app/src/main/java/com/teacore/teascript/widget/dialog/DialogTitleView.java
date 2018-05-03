package com.teacore.teascript.widget.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;

/**
 * Created by apple on 17/10/15.
 */
public class DialogTitleView extends FrameLayout{

    public static final int MODE_REGULAR=0;
    public static final int MODE_SMALL=1;

    public LinearLayout buttonWells;
    public TextView subTitleTv;
    public TextView titleTv;
    public View titleDivider;

    public DialogTitleView(Context context){
        super(context);
        init();
    }

    public DialogTitleView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    public DialogTitleView(Context context,AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_dialog_header,this);
        titleTv=(TextView) findViewById(R.id.title_tv);
        subTitleTv=(TextView) findViewById(R.id.subtitle_tv);
        buttonWells=(LinearLayout) findViewById(R.id.button_wells);
        titleDivider=findViewById(R.id.title_divider);
    }

    public void addAction(View view,
                          android.view.View.OnClickListener listener) {
        view.setOnClickListener(listener);
        buttonWells.addView(view);
    }

    public void setMode(int mode) {
        int padding = (int) getContext().getResources().getDimension(R.dimen.global_dialog_padding);
        if (mode == MODE_SMALL) {
            buttonWells.removeAllViews();
            buttonWells.setVisibility(View.VISIBLE);
            titleTv.setTextSize(1, 16F);
            padding /= 2;
        } else {
            titleTv.setTextSize(1, 22F);
        }
        titleTv.setPadding(padding, padding, padding, padding);
    }

}
