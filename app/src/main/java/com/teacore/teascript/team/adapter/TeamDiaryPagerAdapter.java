package com.teacore.teascript.team.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.widget.DiaryPagerView;

/**
 * 团队周报ViewPagerFragment所使用的PagerAdapter
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-22
 */

public class TeamDiaryPagerAdapter extends PagerAdapter{

    private final Context mContext;
    private final int mCurrentYear;
    private final int mTeamId;

    public  TeamDiaryPagerAdapter(Context context,int currentYear,int teamId){
        this.mContext=context;
        this.mCurrentYear=currentYear;
        this.mTeamId=teamId;
    }

    @Override
    public int getCount(){
        return 52;
    }

    @Override
    public boolean isViewFromObject(View arg0,Object arg1){
        return arg0==arg1;
    }

    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position){

        View pagerView=new DiaryPagerView(mContext,mTeamId,mCurrentYear,position+1).getView();

        container.addView(pagerView);

        return pagerView;
    }

}
