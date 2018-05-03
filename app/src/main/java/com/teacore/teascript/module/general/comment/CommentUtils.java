package com.teacore.teascript.module.general.comment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;


/**
 * 评论帮助类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-1
 */

public class CommentUtils {

    public static View getReferLayout(LayoutInflater inflater, Comment.Refer refer,int count){

        Context context=inflater.getContext();
        ViewGroup viewGroup=(ViewGroup) inflater.inflate(R.layout.item_comment_refer,null,false);

        ShapeDrawable drawable=new ShapeDrawable(new BorderShape(new RectF(Ui.dipToPx(context.getResources(), 1), 0, 0, 0)));
        drawable.getPaint().setColor(0xffd7d6da);
        viewGroup.findViewById(R.id.comment_refer_ll).setBackground(drawable);

        TextView textView = ((TextView) viewGroup.findViewById(R.id.comment_refer_tv));
        drawable = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, 1)));
        drawable.getPaint().setColor(0xffd7d6da);
        textView.setBackground(drawable);

        formatHtml(context.getResources(),textView,refer.author + ":<br>" + refer.content);

        if (refer.refer != null && (--count) > 0) {
            View view = getReferLayout(inflater, refer.refer, count);
            viewGroup.addView(view, viewGroup.indexOfChild(textView));
        }

        return viewGroup;

    }

    public static void formatHtml(Resources resources,TextView textView,String str){
        textView.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
        textView.setFocusable(false);
        textView.setLongClickable(false);

        if(textView instanceof TeatimeTextView){
            ((TeatimeTextView) textView).setDispatchToParent(true);
        }

        str=TeatimeTextView.modifyPath(str);

        Spanned spanned;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(str,Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(str);
        }

        spanned = EmojiInputUtils.displayEmoji(resources, spanned.toString());
        textView.setText(spanned);
        MyURLSpan.parseLinkText(textView, spanned);
    }

}
