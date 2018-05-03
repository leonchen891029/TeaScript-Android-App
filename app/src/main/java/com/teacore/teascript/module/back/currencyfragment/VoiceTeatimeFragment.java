package com.teacore.teascript.module.back.currencyfragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.service.TeatimeTaskUtils;
import com.teacore.teascript.util.DensityUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.recordbutton.RecordButton;
import com.teacore.teascript.widget.recordbutton.RecordButtonUtils;

import java.io.File;

/**
 * 语音Teatime发布界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-23
 */

public class VoiceTeatimeFragment extends BaseFragment{

    public static int MAX_LEN=160;

    private EditText mDescET;
    private TextView mDescLengthTV;
    private RelativeLayout mVoiceTeatimeRL;
    private ImageView mVolumnIV;
    private TextView mTimeTV;
    private RecordButton mRecordBtn;
    //录音播放时的动画背景
    private AnimationDrawable mDrawable;
    private String strSpeech="#语音动弹#";
    private int currentRecordTime=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pub_teatime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pub_menu_send:
                handleSubmit(mRecordBtn.getCurrentAudioPath());
                break;
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_voice_teatime,
                container, false);

        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mDescET=(EditText) view.findViewById(R.id.description_et);
        mDescLengthTV=(TextView) view.findViewById(R.id.desc_length_tv);
        mVoiceTeatimeRL=(RelativeLayout) view.findViewById(R.id.voice_teatime_rl);
        mVolumnIV=(ImageView) view.findViewById(R.id.voice_volume_iv);
        mTimeTV=(TextView) view.findViewById(R.id.voice_time_tv);
        mRecordBtn=(RecordButton) view.findViewById(R.id.voice_teatime_rb);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRecordBtn
                .getLayoutParams();
        params.width = DensityUtils.getScreenWidth(getActivity());
        params.height = (int) (DensityUtils.getScreenHeight(getActivity()) * 0.4);

        mRecordBtn.setLayoutParams(params);
        mVoiceTeatimeRL.setOnClickListener(this);

        mRecordBtn.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                currentRecordTime = recordTime;
                mVoiceTeatimeRL.setVisibility(View.VISIBLE);
                if (recordTime < 10) {
                    mTimeTV.setText("0" + recordTime + "\"");
                } else {
                    mTimeTV.setText(recordTime + "\"");
                }
            }

            @Override
            public void onCancleRecord() {
                mVoiceTeatimeRL.setVisibility(View.GONE);
            }
        });

        mDrawable = (AnimationDrawable) mVolumnIV.getBackground();
        mRecordBtn.getAudioUtils().setOnPlayListener(new RecordButtonUtils.OnPlayListener() {
            @Override
            public void stopPlay() {
                mDrawable.stop();
                mVolumnIV.setBackground(mDrawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                mVolumnIV.setBackground(mDrawable);
                mDrawable.start();
            }
        });

        mDescET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > MAX_LEN) {
                    mDescLengthTV.setText("已达到最大长度");
                } else {
                    mDescLengthTV.setText("您还可以输入" + (MAX_LEN - s.length())
                            + "个字符");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_LEN) {
                    mDescET.setText(s.subSequence(0, MAX_LEN));
                    CharSequence text = mDescET.getText();
                    if (text instanceof Spannable)
                        Selection.setSelection((Spannable) text, MAX_LEN);
                }
            }
        });
    }

    //发布动弹
    private void handleSubmit(String audioPath) {
        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        if (StringUtils.isEmpty(audioPath)) {
            AppContext.showToast(R.string.record_sound_notfound);
            return;
        }
        File file = new File(audioPath);
        if (!file.exists()) {
            AppContext.showToast(R.string.record_sound_notfound);
            return;
        }

        String body = mDescET.getText().toString();
        if (!StringUtils.isEmpty(body)) {
            strSpeech = body;
        }
        Teatime teatime = new Teatime();
        teatime.setAuthorId(AppContext.getInstance().getLoginUid());
        teatime.setAudioPath(audioPath);
        teatime.setBody(strSpeech);
        TeatimeTaskUtils.pubTeatime(getActivity(), teatime);
        getActivity().finish();
    }

    @Override
    public void onClick(View view) {
        final int id=view.getId();
        if (id == R.id.voice_teatime_rl) {
            mRecordBtn.playRecord();
        }
    }

}
