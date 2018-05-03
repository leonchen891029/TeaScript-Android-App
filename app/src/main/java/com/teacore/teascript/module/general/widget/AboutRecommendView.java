package com.teacore.teascript.module.general.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.module.general.bean.About;
import com.teacore.teascript.util.UiUtils;

import java.util.List;

/**
 * 相关推荐View
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-28
 */

public class AboutRecommendView extends LinearLayout{

    private int mType;
    private LinearLayout mAboutsLL;
    private TextView mTitleTV;

    public AboutRecommendView(Context context){
        super(context);
        init();
    }

    public AboutRecommendView(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public AboutRecommendView(Context context,@Nullable AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    private void init(){
        setOrientation(VERTICAL);
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View view=inflater.inflate(R.layout.view_about_recommend,this,true);

        mTitleTV=(TextView) view.findViewById(R.id.about_recommend_tv);
        mAboutsLL=(LinearLayout) view.findViewById(R.id.about_recommend_ll);
    }

    public void setTitle(String title){
        mTitleTV.setText(title);
    }

    public void setAbouts(List<About> abouts,int type){
        this.mType=type;
        final LayoutInflater inflater=LayoutInflater.from(getContext());
        if(abouts!=null && abouts.size()>0){
            boolean clearLine=true;
            for(final About about:abouts){
                if(about==null)
                    continue;;
                View view=inflater.inflate(R.layout.item_about_recommend,null,false);

                ((TextView) view.findViewById(R.id.title_tv)).setText(about.getTitle());

                LinearLayout infoLL=(LinearLayout) view.findViewById(R.id.info_see_comment_ll);
                ((TextView) infoLL.findViewById(R.id.info_see_tv)).setText(String.valueOf(about.getViewCount()));
                ((TextView) infoLL.findViewById(R.id.info_comment_tv)).setText(String.valueOf(about.getCommentCount()));

                if (clearLine) {
                    clearLine = false;
                    view.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                }

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int type = mType;
                        if (about.getType() != 0) {
                            type = about.getType();
                        }
                        UiUtils.showDetail(v.getContext(), type, about.getId(), null);
                    }
                });

                mAboutsLL.addView(view, 0);

            }

        }else{
            setVisibility(View.GONE);
        }

    }

}
