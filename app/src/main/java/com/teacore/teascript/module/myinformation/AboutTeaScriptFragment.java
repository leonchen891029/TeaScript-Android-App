package com.teacore.teascript.module.myinformation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UpdateManager;

/**
 * 关于TeaScript Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-23
 */

public class AboutTeaScriptFragment extends BaseFragment {

    private TextView mVersionTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view=inflater.inflate(R.layout.fragment_about_teascript,container,false);

        initView(view);
        initData();

        return view;
    }

    @Override
    public void initView(View view){
        mVersionTV=(TextView) view.findViewById(R.id.version_tv);

        view.findViewById(R.id.check_update_rl).setOnClickListener(this);
        view.findViewById(R.id.grade_rl).setOnClickListener(this);
        view.findViewById(R.id.git_app_rl).setOnClickListener(this);
        view.findViewById(R.id.teascript_site_tv).setOnClickListener(this);
        view.findViewById(R.id.know_more_tv).setOnClickListener(this);
    }

    @Override
    public void initData() {
        mVersionTV.setText("V " + TDevice.getVersionName());
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.check_update_rl:
                onClickUpdate();
                break;
            case R.id.grade_rl:
                TDevice.openAppInMarket(getActivity());
                break;
            case R.id.git_app_rl:
                boolean res = TDevice.openAppActivity(getActivity(),
                        "com.teascript.gitapp", "com.teascript.gitapp.WelcomePage");

                if (!res) {
                    if (!TDevice.isHaveMarket(getActivity())) {
                        UiUtils.openSystemBrowser(getActivity(),
                                "http://git.teascript.net/appclient");
                    } else {
                        TDevice.gotoMarket(getActivity(), "net.teascript.gitapp");
                    }
                }
                break;
            case R.id.teascript_site_tv:
                UiUtils.openBrowser(getActivity(), "https://www.teascript.com");
                break;
            case R.id.know_more_tv:
                UiUtils.openBrowser(getActivity(),
                        "https://www.teascript.net/home/aboutteascript");
                break;
            default:
                break;
        }
    }

    private void onClickUpdate() {
        new UpdateManager(getActivity(), true).checkUpdate();
    }

}
