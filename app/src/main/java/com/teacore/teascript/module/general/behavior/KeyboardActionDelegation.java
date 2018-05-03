package com.teacore.teascript.module.general.behavior;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.teacore.teascript.widget.emoji.EmojiIcon;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

/**
 * KeyboardActionDelegation
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-2
 */

public class KeyboardActionDelegation {

    private Context mContext;
    private ImageView mEmojiBtn;
    private EditText mInputET;
    private ViewGroup mEmojiPanel;

    private boolean isShowSoftInput;

    private OnActionChangeListener mOnActionClickListener;

    private KeyboardActionDelegation(Context context,EditText inputET,ImageView button,ViewGroup viewGroup,OnActionChangeListener listener){
        this.mContext=context;
        this.mInputET=inputET;
        this.mEmojiBtn=button;
        this.mEmojiPanel=viewGroup;
        this.mOnActionClickListener=listener;
        init();
    }

    public static KeyboardActionDelegation delegate(Context context, EditText input, ImageView button, ViewGroup view) {
        return new KeyboardActionDelegation(context, input, button, view, null);
    }


    public static KeyboardActionDelegation delegate(Context context, EditText input, ImageView button, ViewGroup view, OnActionChangeListener listener) {
        return new KeyboardActionDelegation(context, input, button, view, listener);
    }

    private void init(){
        mInputET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    hideEmojiPanel();
                }else{
                    hideSoftKeyboard();
                }
            }
        });
        mInputET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmojiPanelShowing()) return;
                hideEmojiPanel();
            }
        });
        mEmojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmojiPanelShowing()) {
                    hideEmojiPanel();
                } else {
                    showEmojiPanel();
                }
            }
        });
    }

    public void showEmojiPanel(){
        mEmojiBtn.setSelected(true);
        hideSoftKeyboard();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEmojiPanel.setVisibility(View.VISIBLE);
                if(mOnActionClickListener!=null){
                    mOnActionClickListener.onShowEmojiPanel(KeyboardActionDelegation.this);
                }
            }
        },300);
    }

    private boolean isEmojiPanelShowing(){
        return mEmojiPanel.getVisibility()==View.VISIBLE;
    }

    private void hideSoftKeyboard(){
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mInputET.getWindowToken(), 0);
        isShowSoftInput = false;
    }

    private void hideEmojiPanel(){
        mEmojiPanel.setVisibility(View.GONE);
        mEmojiBtn.setSelected(false);

        if(mOnActionClickListener!=null){
            mOnActionClickListener.onHideEmojiPanel(this);
        }
    }

    private boolean isShowSoftInput(){
        return isShowSoftInput;
    }

    public void onEmojiItemSelected(EmojiIcon emojiIcon){
        if(mInputET==null || emojiIcon==null){
            return;
        }

        int start = mInputET.getSelectionStart();
        int end = mInputET.getSelectionEnd();
        if (start == end) {
            mInputET.append(EmojiInputUtils.displayEmoji(mContext.getResources(), emojiIcon.getRemote()));
        } else {
            Spannable str = EmojiInputUtils.displayEmoji(mContext.getResources(), emojiIcon.getRemote());
            mInputET.getText().replace(Math.min(start, end), Math.max(start, end), str, 0, str.length());
        }

    }

    public boolean onTurnBack() {
        if (isEmojiPanelShowing()) {
            hideEmojiPanel();
            return false;
        }
        if (isShowSoftInput()) {
            hideEmojiPanel();
            return false;
        }
        return true;
    }

    public interface OnActionChangeListener {

        void onHideEmojiPanel(KeyboardActionDelegation delegation);

        void onShowEmojiPanel(KeyboardActionDelegation delegation);
    }

}
