package com.teacore.teascript.widget.emoji;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.widget.EditText;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;

/**emoji解析类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-20
 */

public class EmojiInputUtils {

    //删除Emoji标签
    public static void backspace(EditText editText) {
        if (editText == null) {
            return;
        }
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
                0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    //获取name对应的资源
    public static int getEmojiResId(String name) {
        Integer res = EmojiDisplayRules.getMapAll().get(name);
        if (res != null) {
            return res;
        } else {
            return -1;
        }
    }

    public static Spannable displayEmoji(Resources res, CharSequence s) {
        CharSequence tempStr = " " + s;
        String str = tempStr.toString().trim();
        Spannable spannable;

        if (tempStr instanceof Spannable) {
            spannable = (Spannable) tempStr;
        } else {
            // 构建文字span
            spannable = new SpannableString(str);
        }

        if (!str.contains(":") && !str.contains("[")) {
            return spannable;
        }

        for (int i = 0; i < str.length(); i++) {
            int index1 = str.indexOf("[", i);
            int length1 = str.indexOf("]", index1 + 1);
            int index2 = str.indexOf(":", i);
            int length2 = str.indexOf(":", index2 + 1);
            int bound = (int) res.getDimension(R.dimen.space_20);

            try {
                if (index1 > 0) {
                    String emojiStr = str.substring(index1, length1 + "]".length());
                    int resId = getEmojiResId(emojiStr);
                    if (resId > 0) {
                        // 构建图片span
                        Drawable drawable = res.getDrawable(resId);

                        drawable.setBounds(0, 20, bound, bound + 20);
                        ImageSpan span = new ImageSpan(drawable,
                                ImageSpan.ALIGN_BASELINE);
                        spannable.setSpan(span, index1, length1 + "]".length(),
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
                if (index2 > 0) {
                    String emojiStr2 = str
                            .substring(index2, length2 + ":".length());
                    int resId2 = getEmojiResId(emojiStr2);
                    if (resId2 > 0) {
                        Drawable emojiDrawable = ContextCompat.getDrawable(AppContext.getInstance().getApplicationContext(),resId2);
                        emojiDrawable.setBounds(0, 0, bound, bound);
                        // 构建图片span
                        ImageSpan imageSpan = new ImageSpan(emojiDrawable, str);
                        spannable.setSpan(imageSpan, index2,
                                length2 + ":".length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {
            }
        }
        return spannable;
    }

    //在EditText中显示emoji
    public static void input2OSC(EditText editText, EmojiIcon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            // 没有多选时，直接在当前光标处添加
            editText.append(displayEmoji(editText.getResources(),
                    emojicon.getRemote()));
        } else {
            // 将已选中的部分替换为表情(当长按文字时会多选刷中很多文字)
            Spannable str = displayEmoji(editText.getResources(),
                    emojicon.getRemote());
            editText.getText().replace(Math.min(start, end),
                    Math.max(start, end), str, 0, str.length());
        }
    }
}
