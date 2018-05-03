package com.teacore.teascript.widget.emoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.teacore.teascript.R;

;

/**
 * 表情选择界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-12
 */

public class EmojiKeyboardFragment extends Fragment implements SoftKeyboardStateObservable.SoftKeyboardStateListener {

    public static int EMOJI_TAB_CONTENT;

    private LinearLayout mEmojiContent;
    private RadioGroup mEmojiBottom;
    private View[] mEmojiTabs;
    private ViewPager mEmojiPager;
    private EmojiPagerAdapter adapter;
    private LinearLayout mRootView;
    private OnEmojiClickListener listener;
    private boolean isDelegate;
    private SoftKeyboardStateObservable mKeyboardObservable;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        if(mRootView!=null){
            ViewGroup parent=(ViewGroup) mRootView.getParent();
            if(parent!=null){
                parent.removeView(mRootView);
            }
        }else{
            mRootView = (LinearLayout) inflater.inflate(R.layout.fragment_emoji_keyboard, container, false);
            initWidget(mRootView);
        }

        return mRootView;
    }

    private void initWidget(View rootView){

        //emoji bottom
        mEmojiBottom=(RadioGroup) rootView.findViewById(R.id.emoji_bottom);
        //mEmojiBottom.setVisibility(View.VISIBLE);
        //这里减一是因为其中有一个是删除按钮
        EMOJI_TAB_CONTENT=mEmojiBottom.getChildCount()-1;
        mEmojiTabs=new View[EMOJI_TAB_CONTENT];
        //如果只有一个分类emojibottom不显示
        if(EMOJI_TAB_CONTENT<=1){
            mEmojiBottom.setVisibility(View.GONE);
        }
        for (int i = 0; i < EMOJI_TAB_CONTENT; i++) {
            mEmojiTabs[i] = mEmojiBottom.getChildAt(i);
            mEmojiTabs[i].setOnClickListener(getBottomBarClickListener(i));
        }
        mEmojiBottom.findViewById(R.id.emoji_bottom_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onDeleteButtonClick(view);
                }
            }
        });

        mEmojiContent = (LinearLayout) rootView.findViewById(R.id.emoji_content);
        mEmojiPager = (ViewPager) mEmojiContent.findViewById(R.id.emoji_pager);
        adapter = new EmojiPagerAdapter(getChildFragmentManager(), EMOJI_TAB_CONTENT, listener);
        mEmojiPager.setAdapter(adapter);
        //mEmojiContent.setVisibility(View.VISIBLE);

        mKeyboardObservable=new SoftKeyboardStateObservable(getActivity().getWindow().getDecorView());
        mKeyboardObservable.addSoftKeyboardStateListener(this);
    }

    //底部栏点击事件监听器
    private View.OnClickListener getBottomBarClickListener(final int index) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiPager.setCurrentItem(index);
            }
        };
    }

    public void setOnEmojiClickListener(OnEmojiClickListener onEmojiClickListener){
        this.listener=onEmojiClickListener;
    }

    public void hideAllKeyboard(){
        hideEmojiKeyboard();
        hideSoftKeyboard();
    }

    public boolean isShow(){
        return mEmojiContent.getVisibility()==View.VISIBLE;
    }

    //隐藏Emoji键盘并显示软键盘

    public void hideEmojiKeyboard(){
        if(!isDelegate){
            mEmojiContent.setVisibility(View.GONE);
            mEmojiBottom.setVisibility(View.GONE);
        }
    }

    //显示emoji键盘并隐藏软键盘
    public void showEmojiKeyBoard() {
        mEmojiContent.setVisibility(View.VISIBLE);
        if (EMOJI_TAB_CONTENT > 1) {
            mEmojiBottom.setVisibility(View.VISIBLE);
        }
    }

    //隐藏软键盘
    public void hideSoftKeyboard(){
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(mEmojiBottom.getWindowToken(),0);
    }

    //显示软键盘
    public void showSoftKeyboard(EditText et) {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(et,
                InputMethodManager.SHOW_FORCED);
    }

    //当软键盘显示时的回调
    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx){
        if(!isDelegate){
            mEmojiContent.setVisibility(View.GONE);
            mEmojiBottom.setVisibility(View.GONE);
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

    public void setDelegate(boolean delegate) {
        isDelegate = delegate;
    }


}
