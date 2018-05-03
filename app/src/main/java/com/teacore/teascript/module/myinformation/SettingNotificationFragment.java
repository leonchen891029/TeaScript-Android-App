package com.teacore.teascript.module.myinformation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.widget.TSSwitchButton;
import com.teacore.teascript.widget.TSSwitchButton.OnSwitchChanged;

/**
 * 消息提醒设置界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-21
 */

public class SettingNotificationFragment extends BaseFragment{

    private TSSwitchButton mAcceptSB;
    private TSSwitchButton mVoiceSB;
    private TSSwitchButton mVibrationSB;
    private TSSwitchButton mAppExitSB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view=inflater.inflate(R.layout.fragment_setting_notification,container,false);

        initView(view);

        initData();

        return view;
    }

    @Override
    public void initView(View view){
        mAcceptSB=(TSSwitchButton) view.findViewById(R.id.accept_sb);
        mVoiceSB=(TSSwitchButton) view.findViewById(R.id.voice_sb);
        mVibrationSB=(TSSwitchButton) view.findViewById(R.id.vibration_sb);
        mAppExitSB=(TSSwitchButton) view.findViewById(R.id.app_exit_sb);

        setSwitchChanged(mAcceptSB, AppConfig.KEY_NOTIFICATION_ACCEPT);
        setSwitchChanged(mVoiceSB, AppConfig.KEY_NOTIFICATION_SOUND);
        setSwitchChanged(mVibrationSB, AppConfig.KEY_NOTIFICATION_VIBRATION);
        setSwitchChanged(mAppExitSB, AppConfig.KEY_NOTIFICATION_DISABLE_WHEN_EXIT);

        view.findViewById(R.id.accept_rl).setOnClickListener(this);
        view.findViewById(R.id.voice_rl).setOnClickListener(this);
        view.findViewById(R.id.vibration_rl).setOnClickListener(this);
        view.findViewById(R.id.app_exit_rl).setOnClickListener(this);
    }

    @Override
    public void initData() {
        setToggle(AppContext.get(AppConfig.KEY_NOTIFICATION_ACCEPT, true), mAcceptSB);
        setToggle(AppContext.get(AppConfig.KEY_NOTIFICATION_SOUND, true), mVoiceSB);
        setToggle(AppContext.get(AppConfig.KEY_NOTIFICATION_VIBRATION, true), mVibrationSB);
        setToggle(AppContext.get(AppConfig.KEY_NOTIFICATION_DISABLE_WHEN_EXIT, true), mAppExitSB);
    }

    private void setSwitchChanged(TSSwitchButton sb, final String key) {

        sb.setOnSwitchChanged(new OnSwitchChanged() {

            @Override
            public void onSwitch(boolean on) {
                AppContext.set(key, on);
            }
        });

    }

    private void setToggle(boolean value, TSSwitchButton sb) {
        if (value)
            sb.setSwitchOn();
        else
            sb.setSwitchOff();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.accept_rl:
                mAcceptSB.beginSwitch();
                break;
            case R.id.voice_rl:
                mVoiceSB.beginSwitch();
                break;
            case R.id.vibration_rl:
                mVibrationSB.beginSwitch();
                break;
            case R.id.app_exit_rl:
                mAppExitSB.beginSwitch();
                break;
        }

    }

}
