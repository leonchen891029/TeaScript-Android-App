package com.teacore.teascript.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.bean.Banner;
import com.teacore.teascript.widget.indicator.CirclePagerIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 综合ViewPagerFragment里面的新闻小窗口
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public class NewsHeaderView extends RelativeLayout implements ViewPager.OnPageChangeListener{

    private ViewPager news_vp;
    private List<NewsBannerView> banners=new ArrayList<>();
    private NewsPagerAdapter mAdapter;
    private TSRefreshLayout mRefreshLayout;
    private CirclePagerIndicator mIndicator;
    private TextView news_title_tv;
    private ScheduledExecutorService mSchedule;
    private int mCurrentItem=0;
    private Handler mHandler;
    private boolean isMoving=false;
    private boolean isScroll=false;

    public NewsHeaderView(Context context){
        super(context);
        init(context);
    }

    public NewsHeaderView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_news_header,this,true);
        news_vp=(ViewPager) findViewById(R.id.news_vp);
        mIndicator=(CirclePagerIndicator) findViewById(R.id.news_indicator);
        news_title_tv=(TextView) findViewById(R.id.news_title_tv);
        mAdapter=new NewsPagerAdapter();
        news_vp.setAdapter(mAdapter);

        mIndicator.bindViewPager(news_vp);
        new SmoothScroller(getContext()).bindViewPager(news_vp);
        news_vp.addOnPageChangeListener(this);

        mHandler=new Handler(){
          @Override
          public void handleMessage(Message msg) {
              super.handleMessage(msg);
              news_vp.setCurrentItem(mCurrentItem);
          }
        };

        mSchedule= Executors.newSingleThreadScheduledExecutor();
        mSchedule.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!isMoving && !isScroll) {
                    mCurrentItem = (mCurrentItem + 1) % banners.size();
                    mHandler.obtainMessage().sendToTarget();
                }
            }
        }, 2, 4, TimeUnit.SECONDS);


        news_vp.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        mRefreshLayout.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isMoving = false;
                        mRefreshLayout.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mRefreshLayout.setEnabled(false);
                        isMoving = true;
                        break;
                }
                return false;
            }
        });

    }

    public void initData(RequestManager manager, List<Banner> banners) {

        this.banners.clear();

        for (Banner banner : banners) {
            NewsBannerView newsBanner = new NewsBannerView(getContext());
            newsBanner.initData(manager, banner);
            this.banners.add(newsBanner);
        }

        mAdapter.notifyDataSetChanged();
        mIndicator.notifyDataSetChanged();
    }

    public void setRefreshLayout(TSRefreshLayout refreshLayout){
        this.mRefreshLayout=refreshLayout;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        isMoving = mCurrentItem != position;
    }

    @Override
    public void onPageSelected(int position) {
        isMoving = false;
        mCurrentItem = position;
        mRefreshLayout.setEnabled(true);
        isScroll = false;

        news_title_tv.setText(banners.get(position).getTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        isMoving = state != ViewPager.SCROLL_STATE_IDLE;
        isScroll = state != ViewPager.SCROLL_STATE_IDLE;
        mRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }

    private class NewsPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(banners.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(banners.get(position));
            return banners.get(position);
        }
    }

}
