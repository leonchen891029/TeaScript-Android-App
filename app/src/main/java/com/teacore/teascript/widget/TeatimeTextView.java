package com.teacore.teascript.widget;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**自定义TextView控件
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-1
*/
public class TeatimeTextView extends TextView{

    private final static String AT_HOST_PRE = "https://my.teacrpt.com";
    private final static String MAIN_HOST = "https://www.teacript.com";

    private boolean dispatchToParent;

    public TeatimeTextView(Context context) {
        super(context);
    }

    public TeatimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeatimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //是否处理touch事件
    public boolean handleTouchEvent(MotionEvent event){
        int action=event.getAction();
        if(action !=MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP){
            return true;
        }else{
            //单击按下或者是抬起情况
            int x=(int) event.getX();
            int y=(int) event.getY();

            //获取相对于文本区域的x和y坐标
            x-=getTotalPaddingLeft();
            y-=getTotalPaddingTop();

            //x，y随着scroll变化
            x+=getScrollX();
            y+=getScrollY();

            Layout layout=getLayout();
            //获取触摸点在垂直方向上的行数值
            int line=layout.getLineForVertical(y);
            //获取触摸点在某一个行水平方向的偏移量
            int offset=layout.getOffsetForHorizontal(line,x);
            //该行的宽度
            float width=layout.getLineWidth(line);

            if(y>width){
                offset=y;
            }

            if (!(getText() instanceof Spannable)) {
                return true;
            } else {


                Spannable span = (Spannable) getText();

                ClickableSpan[] clickSpan = span.getSpans(offset, offset,
                        ClickableSpan.class);

                if (clickSpan == null || clickSpan.length == 0) {

                    SmallCardSpan[] scss = span.getSpans(offset, offset, SmallCardSpan.class);

                    if (scss != null && scss.length != 0)
                        return false;

                    return true;

                } else {
                    return false;
                }

            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MovementMethod movementMethod = getMovementMethod();
        CharSequence text = getText();
        if (movementMethod != null && (text instanceof Spannable)
                && handleTouchEvent(event)) {
            movementMethod.onTouchEvent(this, (Spannable) text, event);
            if (dispatchToParent) {
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performLongClick() {
        MovementMethod movementMethod = getMovementMethod();

        if (movementMethod != null && (movementMethod instanceof TSLinkMovementMethod))
            ((TSLinkMovementMethod) movementMethod).a(this);
        return super.performLongClick();
    }

    public void setDispatchToParent(boolean flag) {
        dispatchToParent = flag;
    }

    public static String modifyPath(String message) {
        message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
                + AT_HOST_PRE + "/$2\"");
        message = message.replaceAll(
                "(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"
                        + MAIN_HOST + "$2\"");
        return message;
    }


}
