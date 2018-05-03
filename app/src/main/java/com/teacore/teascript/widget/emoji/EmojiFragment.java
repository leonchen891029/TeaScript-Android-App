package com.teacore.teascript.widget.emoji;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.teacore.teascript.R;

/**表情Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-18
 */

public class EmojiFragment extends Fragment implements SoftKeyboardStateObservable.SoftKeyboardStateListener{

    private LinearLayout mRootView;

    private View emojiTitle;
    private LinearLayout emojiContent;
    private RadioGroup emojiBottom;
    //emoji bottom 子视图中的个数
    private View[] emojiTabs;
    private ViewPager emojiPager;
    private EmojiPagerAdapter adapter;
    //emoji bottom radiogroup 元素个数
    public static int EMOJI_TABS;

    private OnSendClickListener listener;
    private EditText mET;
    private CheckBox mCheckBox;
    private CheckBox mCheckBoxFlag;
    private SoftKeyboardStateObservable softKeyboardStateObservable;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != mRootView) {

            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }

        }else {

            mRootView = (LinearLayout) inflater.inflate(R.layout.fragment_emoji, container,false);
            initWidget(mRootView);

        }
        return mRootView;
    }

    public LinearLayout getRootView(){
        return mRootView;
    }

    private void initWidget(View rootView){

        //title view设置
        emojiTitle=rootView.findViewById(R.id.emoji_title);

        mCheckBoxFlag=(CheckBox) emojiTitle.findViewById(R.id.flag_checkbox);

        mCheckBoxFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null){
                    listener.onClickFlagButton();
                }

            }
        });

        mET=(EditText) emojiTitle.findViewById(R.id.emoji_titile_et);
        mCheckBox=(CheckBox) emojiTitle.findViewById(R.id.emoji_title_checkbox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    showEmojiKeyboard();
                    hideSoftKeyboard();
                }else {
                    showSoftKeyboard();
                }


            }
        });

        //emoji bottom设置
        emojiBottom=(RadioGroup) rootView.findViewById(R.id.emoji_bottom);
        //这里需要减1是因为这里有一个删除按钮
        EMOJI_TABS=emojiBottom.getChildCount()-1;
        emojiTabs=new View[EMOJI_TABS];

        if (EMOJI_TABS <= 1) { // 只有一个分类的时候就不显示了
            emojiBottom.setVisibility(View.GONE);
        }

        for (int i = 0; i < EMOJI_TABS; i++) {
            emojiTabs[i] = emojiBottom.getChildAt(i);
            emojiTabs[i].setOnClickListener(getBottomBarClickListener(i));
        }

        emojiBottom.findViewById(R.id.emoji_bottom_del).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EmojiInputUtils.backspace(mET);
                    }
                });

        // content必须放在bottom下面初始化
        emojiPager = (ViewPager) rootView.findViewById(R.id.emoji_pager);

        EmojiPagerAdapter adapter = new EmojiPagerAdapter(getFragmentManager());

        emojiPager.setAdapter(adapter);

        softKeyboardStateObservable = new SoftKeyboardStateObservable(getActivity().getWindow()
                .getDecorView());

        softKeyboardStateObservable.addSoftKeyboardStateListener(this);

        if (getActivity() instanceof OnSendClickListener) {

            listener = (OnSendClickListener) getActivity();

        }

        if (listener != null) {

            emojiTitle.findViewById(R.id.emoji_title_iv).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClickSendButton(mET.getText());
                            mET.setHint("说点什么吧");
                            hideAllKeyBoard();
                        }
                    });
        }

    }

    //底部栏点击事件监听器
    private View.OnClickListener getBottomBarClickListener(final int index) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPager.setCurrentItem(index);
            }
        };
    }


    public void setOnSendClickListener(OnSendClickListener sendClickListener) {
        this.listener = sendClickListener;
    }

    public void clean() {
        mET.setText(null);
        mET.setTag(null);
    }

    public void hideAllKeyBoard() {
        hideEmojiKeyBoard();
        hideSoftKeyboard();
    }

    /**
     * 隐藏Emoji并显示软键盘
     */
    public void hideEmojiKeyBoard() {
        emojiBottom.setVisibility(View.GONE);
        emojiPager.setVisibility(View.GONE);
        mCheckBox.setChecked(false);
    }

    /**
     * 显示Emoji并隐藏软键盘
     */
    public void showEmojiKeyboard() {
        emojiPager.setVisibility(View.VISIBLE);

        if (EMOJI_TABS > 1) {
            emojiBottom.setVisibility(View.VISIBLE);
        }
        mCheckBox.setChecked(true);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mET.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        mET.requestFocus();
        ((InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mET, InputMethodManager.SHOW_FORCED);
    }

    public View getEmojiTitle() {
        return emojiTitle;
    }

    public Editable getTextString() {
        return mET.getText();
    }

    public EditText getEditText() {
        return mET;
    }

    public boolean isShowEmojiKeyBoard() {
        if (mCheckBox == null) {
            return false;
        } else {
            return mCheckBox.isChecked();
        }
    }

    /**
     * 当软键盘显示时回调
     */
    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {

        if (emojiBottom != null && emojiPager != null) {
            emojiBottom.setVisibility(View.GONE);
            emojiPager.setVisibility(View.GONE);
        }
        if (mCheckBox != null) {
            mCheckBox.setChecked(false);
        }
    }

    public void hideFlagButton() {
        if (mCheckBoxFlag != null) {
            mCheckBoxFlag.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftKeyboard();
    }

}
