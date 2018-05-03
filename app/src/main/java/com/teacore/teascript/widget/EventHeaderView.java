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
 * 活动页面的HeaderView
 * Created by apple on 17/11/15.
 */

public class EventHeaderView extends RelativeLayout implements ViewPager.OnPageChangeListener{

    private ViewPager eventVP;
    private List<EventBannerView> banners=new ArrayList<>();
    private EventPagerAdapter mAdapter;
    private TSRefreshLayout mRefreshLayout;
    private CirclePagerIndicator mIndicator;
    private ScheduledExecutorService mSchedule;
    private int mCurrentItem=0;
    private Handler handler;
    private boolean isMoving=false;
    private boolean isScroll=false;

    public EventHeaderView(Context context){
        super(context);
        init(context);
    }

    public EventHeaderView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    public void setRefreshLayout(TSRefreshLayout refreshLayout){
        this.mRefreshLayout=refreshLayout;
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_event_header,this,true);
        eventVP=(ViewPager) findViewById(R.id.event_vp);
        mIndicator=(CirclePagerIndicator) findViewById(R.id.event_indicator);
        mAdapter=new EventPagerAdapter();
        eventVP.setAdapter(mAdapter);
        mIndicator.bindViewPager(eventVP);
        new SmoothScroller(getContext()).bindViewPager(eventVP);
        eventVP.addOnPageChangeListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                eventVP.setCurrentItem(mCurrentItem);
            }
        };
        mSchedule = Executors.newSingleThreadScheduledExecutor();
        mSchedule.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!isMoving && !isScroll) {
                    mCurrentItem = (mCurrentItem + 1) % banners.size();
                    handler.obtainMessage().sendToTarget();
                }
            }
        }, 2, 4, TimeUnit.SECONDS);

        eventVP.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        mRefreshLayout.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isMoving = false;
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
            EventBannerView eventBanner = new EventBannerView(getContext());
            eventBanner.initData(manager, banner);
            this.banners.add(eventBanner);
        }
        mAdapter.notifyDataSetChanged();
        mIndicator.notifyDataSetChanged();
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        isMoving = state != ViewPager.SCROLL_STATE_IDLE;
        isScroll = state != ViewPager.SCROLL_STATE_IDLE;
        mRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }

    private class EventPagerAdapter extends PagerAdapter{
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
