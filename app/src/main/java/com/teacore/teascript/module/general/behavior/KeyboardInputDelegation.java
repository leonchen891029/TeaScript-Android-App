package com.teacore.teascript.module.general.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.widget.emoji.EmojiIcon;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;
import com.teacore.teascript.widget.emoji.EmojiKeyboardFragment;
import com.teacore.teascript.widget.emoji.OnEmojiClickListener;

/**
 * 底部键盘输入的操作类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-3
 */

public class KeyboardInputDelegation {

    private Context mContext;
    private boolean isLastEmpty=true;

    private CoordinatorLayout mCoordinatorLayout;
    private View mScrollerView;
    private View mWrapperView;
    private EditText mInputET;
    private ImageView mShareIV;
    private ImageView mFavouriteIV;
    private ImageView mEmojiIV;
    private FrameLayout mKeyboardFL;

    private KeyboardActionDelegation mActionDelegation;

    private KeyboardInputDelegation(Context context) {
        this.mContext = context;
    }

    public static KeyboardInputDelegation delegate(Context context,CoordinatorLayout coordinatorLayout,View scrollerView){
        KeyboardInputDelegation delegation=new KeyboardInputDelegation(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_keyboard_input, coordinatorLayout, false);
        coordinatorLayout.addView(view);

        delegation.setWrapperView(view);
        delegation.setCoorLayout(coordinatorLayout);
        delegation.setScrollerView(scrollerView);
        return delegation;
    }

    private void setWrapperView(View view) {
        this.mWrapperView = view;
        mInputET = (EditText) this.mWrapperView.findViewById(R.id.input_et);
    }

    private void setCoorLayout(CoordinatorLayout view) {
        this.mCoordinatorLayout = view;
    }

    private void setScrollerView(View view) {
        this.mScrollerView = view;
    }

    public void showEmoji(FragmentManager fragmentManager){
        if(mKeyboardFL==null){
            mKeyboardFL=(FrameLayout) mWrapperView.findViewById(R.id.emoji_keyboard_fl);
        }
        if(mEmojiIV==null){
            mEmojiIV=(ImageView) mWrapperView.findViewById(R.id.emoji_iv);
        }
        mEmojiIV.setVisibility(View.VISIBLE);

        final EmojiKeyboardFragment mKeyboardFragment=new EmojiKeyboardFragment();
        mKeyboardFragment.setDelegate(true);
        mKeyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(EmojiIcon v) {
                mActionDelegation.onEmojiItemSelected(v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                EmojiInputUtils.backspace(mInputET);
            }

        });

        mCoordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    onTurnBack();
                }
                return false;
            }
        });

        fragmentManager.beginTransaction()
                .replace(R.id.emoji_keyboard_fl,mKeyboardFragment)
                .commit();

        mActionDelegation = KeyboardActionDelegation.delegate(mContext, mInputET, mEmojiIV, mKeyboardFL, new KeyboardActionDelegation.OnActionChangeListener() {
            @Override
            public void onHideEmojiPanel(KeyboardActionDelegation delegation) {
                mKeyboardFragment.hideEmojiKeyboard();
            }

            @Override
            public void onShowEmojiPanel(KeyboardActionDelegation delegation) {
                mKeyboardFragment.showEmojiKeyBoard();
            }
        });

    }

    public void showShare(View.OnClickListener listener) {
        if (mShareIV == null)
            mShareIV = (ImageView) mWrapperView.findViewById(R.id.share_iv);
        if (listener != null){
            mShareIV.setOnClickListener(listener);
        }
        mShareIV.setVisibility(View.VISIBLE);
    }

    public void showFavor(View.OnClickListener listener) {
        if (mFavouriteIV == null)
            mFavouriteIV = (ImageView) mWrapperView.findViewById(R.id.favorite_iv);
        if (listener != null) {
            mFavouriteIV.setOnClickListener(listener);
        }
        mFavouriteIV.setVisibility(View.VISIBLE);
    }

    public void setAdapter(final KeyboardInputAdapter mInputAdapter) {

        mInputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mInputAdapter.onSubmit(view, view.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mInputET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    mInputAdapter.onBackSpace(view);
                    if (!TextUtils.isEmpty(mInputET.getText().toString())) {
                        isLastEmpty = false;
                        return false;
                    }
                    if (TextUtils.isEmpty(mInputET.getText().toString()) && !isLastEmpty) {
                        isLastEmpty = true;
                        return false;
                    }
                    mInputAdapter.onFinalBackSpace(view);
                    return false;
                }
                return false;
            }
        });
    }

    public EditText getInputView() {
        return mInputET;
    }

    public void notifyWrapper() {
        ScrollingAutoHideBehavior.showBottomLayout(mCoordinatorLayout, mScrollerView, mWrapperView);
    }

    public boolean onTurnBack() {
        return mActionDelegation == null || mActionDelegation.onTurnBack();
    }

    public void onBackSpace() {

    }

    public static abstract class KeyboardInputAdapter {

        public abstract void onSubmit(TextView v, String content);

        public void onBackSpace(View v) {
        }

        public void onFinalBackSpace(View v) {
        }
    }

}
