package com.teacore.teascript.team.fragment.viewpagerfragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamDiaryPagerAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamDiaryList;
import com.teacore.teascript.util.TimeUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 团队周报ViewPagerFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-26
 */

public class TeamDiaryViewPagerFragment extends BaseFragment {

    public static String CACHE_KEY_TEAM_DIARY="cache_key_team_diary";
    public static String TEAM_DIARY_KEY = "team_diary_key";
    public static String TEAM_ID_KEY = "team_id_key";

    private ImageView mLeftIV;
    private TextView  mTitleTV;
    private ImageView mRightIV;
    private ImageView mCalendarIV;
    private ViewPager mViewPager;

    private Activity aty;
    private Team mTeam;
    private int mCurrentWeek=1;
    private int mCurrentYear=2017;

    private Map<Integer, TeamDiaryList> dataBundleList;

    private final Calendar calendar = Calendar.getInstance();

    private TeamDiaryPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTeam = (Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        }
        if (mTeam != null) {
            CACHE_KEY_TEAM_DIARY += mTeam.getId();
        }

        aty=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(aty, R.layout.fragment_team_diary_view_pager, null);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        mLeftIV=(ImageView) view.findViewById(R.id.left_iv);
        mTitleTV=(TextView) view.findViewById(R.id.title_tv);
        mRightIV=(ImageView) view.findViewById(R.id.right_iv);
        mCalendarIV=(ImageView) view.findViewById(R.id.calendar_iv);
        mViewPager=(ViewPager) view.findViewById(R.id.diary_view_pager);

        mAdapter = new TeamDiaryPagerAdapter(aty, mCurrentYear, mTeam.getId());
        mViewPager.setAdapter(mAdapter);

        changeUI(mCurrentWeek, mAdapter.getCount());

        mLeftIV.setOnClickListener(this);
        mRightIV.setOnClickListener(this);
        mCalendarIV.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                changeUI(arg0, mViewPager.getAdapter().getCount());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void initData() {

        mCurrentWeek = TimeUtils.getWeekOfYear() - 1;
        dataBundleList = new HashMap<>(mCurrentWeek + 5);

        // 异步读缓存
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mCurrentWeek; i++) {
                    TeamDiaryList dataBundle = ((TeamDiaryList) CacheManager
                            .readObject(aty, CACHE_KEY_TEAM_DIARY + i));
                    if (dataBundle != null) {
                        if (dataBundleList.get(i) != null) {
                            dataBundleList.remove(i);
                        }
                        dataBundleList.put(i, dataBundle);
                    }
                }
            }
        });
    }

    /**
     * 改变Title
     * @param currentPage 当前页(从0开始)
     * @param totalPage   总共有多少页(从0开始)
     */
    private void changeUI(int currentPage, int totalPage) {
        mViewPager.setCurrentItem(currentPage);

        if (currentPage <= 0) {
            mLeftIV.setImageResource(R.drawable.icon_diary_unback);
        } else {
            mLeftIV.setImageResource(R.drawable.icon_diary_back);
        }

        if (currentPage >= mAdapter.getCount() - 1) {
            mRightIV.setImageResource(R.drawable.icon_diary_unforward);
        } else {
            mRightIV.setImageResource(R.drawable.icon_diary_forward);
        }

        mTitleTV.setText(String.format("第%d周周报总览", currentPage + 1));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.left_iv:
                int currentPage1 = mViewPager.getCurrentItem();
                if (currentPage1 > 0) {
                    mViewPager.setCurrentItem(currentPage1 - 1);
                }
                break;
            case R.id.right_iv:
                int currentPage2 = mViewPager.getCurrentItem();
                if (currentPage2 < mViewPager.getAdapter().getCount()) {
                    mViewPager.setCurrentItem(currentPage2 + 1);
                }
                break;
            case R.id.calendar_iv:
            /*    final DatePickerDialog dialog = DatePickerDialog.Builder.with(aty)
                        .setMinYear(2017)
                        .setMaxYear(2049)
                        .setButtonCallback(new DatePickerDialog.ButtonCallback() {
                            @Override
                            public void onPositive(DialogInterface dialog, int year, int month, int day) {
                                int[] dateData=StringAndTimeUtils.getCurrentDate();
                                if ((dateData[0] == year && dateData[1] <= month)
                                        || (dateData[0] == year && dateData[1] == month + 1 && dateData[2] < day)) {
                                    AppContext.showToast("这天怎么会有周报呢");
                                } else {
                                    mCurrentYear = year;
                                    mCurrentWeek = StringAndTimeUtils.getWeekOfYear(new Date(year, month, day)) - 1;
                                    mViewPager.setAdapter(new TeamDiaryPagerAdapter(aty, year, mTeam.getId()));
                                    changeUI(mCurrentWeek, mViewPager.getAdapter().getCount());
                                }
                            }

                            @Override
                            public void onNegative(DialogInterface dialog) {

                            }
                        })
                        .build();
                dialog.show();*/
                break;
            default:
                break;
        }
    }

}
