package com.teacore.teascript.widget.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**emoji ViewPager类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-18.
 */

public class EmojiPager extends ViewPager{

    public EmojiPager(Context context){
        super(context);
    }

    public EmojiPager(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    //重写ViewPager触屏操作，修正了系统ViewPager与Activity触摸屏事件冲突 return false
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        super.onTouchEvent(motionEvent);
        return false;
    }


}
