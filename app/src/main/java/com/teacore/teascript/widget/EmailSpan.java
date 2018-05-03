package com.teacore.teascript.widget;

import android.app.Activity;
import android.support.v4.app.ShareCompat;
import android.text.style.ClickableSpan;
import android.view.View;

/**Email Span类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-5
 */
public class EmailSpan extends ClickableSpan{

    private String email;

    public EmailSpan(String email){
        this.email=email;
    }

    @Override
    public void onClick(View widget) {
        try {
            //IntentBuilder是一个用来构造ACTION_SEND和ACTION_SEND_MULTIPLE操作接口意图的辅助类
            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
                    .from((Activity) widget.getContext());
            builder.setType("message/rfc822");
            builder.addEmailTo(email);
            builder.setSubject("");
            builder.setChooserTitle("");
            builder.startChooser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
