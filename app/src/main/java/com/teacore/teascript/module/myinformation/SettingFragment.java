package com.teacore.teascript.module.myinformation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.app.AppManager;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.util.FileUtils;
import com.teacore.teascript.util.MethodsCompat;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.TSSwitchButton;
import com.teacore.teascript.widget.TSSwitchButton.OnSwitchChanged;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.File;

/**
 * 系统设置界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class SettingFragment extends BaseFragment {

    private TSSwitchButton mLoadingImgSB;
    private TextView mCacheSizeTV;
    private TSSwitchButton mDoubleClickExitSB;
    private TextView mExitTV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mLoadingImgSB=(TSSwitchButton) view.findViewById(R.id.loading_img_sb);
        mCacheSizeTV=(TextView) view.findViewById(R.id.clean_cache_tv);
        mDoubleClickExitSB=(TSSwitchButton) view.findViewById(R.id.double_click_exit_sb);
        mExitTV=(TextView) view.findViewById(R.id.exit_tv);

        mLoadingImgSB.setOnSwitchChanged(new OnSwitchChanged() {
            @Override
            public void onSwitch(boolean on) {
                AppContext.setLoadImage(on);
            }

        });

        mDoubleClickExitSB.setOnSwitchChanged(new OnSwitchChanged() {
            @Override
            public void onSwitch(boolean on) {
                AppContext.set(AppConfig.KEY_DOUBLE_CLICK_EXIT, on);
            }
        });

        view.findViewById(R.id.loading_img_rl).setOnClickListener(this);
        view.findViewById(R.id.notification_rl).setOnClickListener(
                this);
        view.findViewById(R.id.clean_cache_rl).setOnClickListener(this);
        view.findViewById(R.id.double_click_exit_rl).setOnClickListener(this);
        view.findViewById(R.id.about_ts_rl).setOnClickListener(this);
        view.findViewById(R.id.exit_rl).setOnClickListener(this);

        if (!AppContext.getInstance().isLogin()) {
            mExitTV.setText("退出");
        }
    }

    @Override
    public void initData() {

        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)) {
            mLoadingImgSB.setSwitchOn();
        } else {
            mLoadingImgSB.setSwitchOff();
        }

        if (AppContext.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
            mDoubleClickExitSB.setSwitchOn();
        } else {
            mDoubleClickExitSB.setSwitchOff();
        }

        caculateCacheSize();
    }

    //计算缓存的大小
    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtils.getDirSize(externalCacheDir);
            fileSize += FileUtils.getDirSize(new File(
                    FileUtils.getSDRoot()
                            + File.separator + AppContext.cachePath));
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        mCacheSizeTV.setText(cacheSize);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.loading_img_rl:
                mLoadingImgSB.beginSwitch();
                break;
            case R.id.notification_rl:
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.SETTING_NOTIFICATION);
                break;
            case R.id.clean_cache_rl:
                onClickCleanCache();
                break;
            case R.id.double_click_exit_rl:
                mDoubleClickExitSB.beginSwitch();
                break;
            case R.id.about_ts_rl:
                UiUtils.showSimpleBack(getActivity(),BackFragmentEnum.ABOUT_TEASCRIPT);
                break;
            case R.id.exit_rl:
                onClickExit();
                break;
            default:
                break;
        }

    }

    private void onClickCleanCache() {
        DialogUtils.getConfirmDialog(getActivity(), "是否清空缓存?", new DialogInterface.OnClickListener
                () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UiUtils.cleanAppCache(getActivity());
                mCacheSizeTV.setText("0KB");
            }
        }).show();
    }

    private void onClickExit() {
        AppContext
                .set(AppConfig.KEY_NOTIFICATION_DISABLE_WHEN_EXIT,
                        false);
        AppManager.getAppManager().AppExit();
        getActivity().finish();
    }



}












