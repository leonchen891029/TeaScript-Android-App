package com.teacore.teascript.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.bean.Comment.Refer;

import java.util.List;

/**FloorView自定义控件
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-27
 */

public class FloorView extends LinearLayout{

    private Context context;
    private Drawable drawable;

    public FloorView(Context context) {
        this(context, null);
    }

    public FloorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        // 获取背景Drawable的资源文件
        drawable = ContextCompat.getDrawable(context,R.drawable.comment_background);
    }

    //设置Comment数据
    public void setComments(List<Refer> refers) {
        // 清除子View
        removeAllViews();

        // 获取评论数
        int count = refers.size();

		// 如果评论条数小于9条则直接显示，否则我们只显示评论的头两条和最后一条（这里的最后一条是相对于PostView中已经显示的一条评论来说的）
        initViewWithAll(refers);
    }

    //初始化所有的View
    private void initViewWithAll(List<Refer> refers) {
        for (int i = 0; i < refers.size(); i++) {
            View commentView = getView(refers.get(i), i, refers.size(),
                    false);
            addView(commentView);
        }
    }

    //初始化带有隐藏楼层的View
    private void initViewWithHide(final List<Refer> refers) {
        View commentView = null;

        // 初始化一楼
        commentView = getView(refers.get(0), 0, refers.size() - 1, false);
        addView(commentView);

        // 初始化二楼
        commentView = getView(refers.get(1), 1, refers.size() - 1, false);
        addView(commentView);

        // 初始化隐藏楼层标识
        commentView = getView(null, 2, refers.size() - 1, true);

        commentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initViewWithAll(refers);
            }
        });

        addView(commentView);

        // 初始化倒数第二楼
        commentView = getView(refers.get(refers.size() - 2), 3,
                refers.size() - 1, false);
        addView(commentView);
    }

    /**
     * 获取单个评论子视图
     * @param refer    评论对象
     * @param index   第几个评论
     * @param count   总共有几个评论
     * @param isHide  是否是隐藏显示
     * @return 一个评论子视图
     */
    private View getView(Refer refer, int index, int count, boolean isHide) {

        // 获取根布局
        View commentView = LayoutInflater.from(context).inflate(
                R.layout.list_cell_reply_name_content, null, false);

        // 获取控件
        TextView usernameTV = (TextView) commentView
                .findViewById(R.id.reply_name_tv);

        TeatimeTextView contentTV = (TeatimeTextView) commentView
                .findViewById(R.id.reply_content_tv);


		/*
		 * 判断是否是隐藏楼层
		 */
        if (isHide) {

			//是则显示“点击显示隐藏楼层”控件而隐藏其他的不相干控件
            usernameTV.setVisibility(GONE);
            contentTV.setVisibility(GONE);

        } else {

			 // 否则隐藏“点击显示隐藏楼层”控件而显示其他的不相干控件
            usernameTV.setVisibility(VISIBLE);
            contentTV.setVisibility(VISIBLE);

            // 设置显示数据
            usernameTV.setText(refer.getRefertitle());
            contentTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
            contentTV.setFocusable(false);
            contentTV.setDispatchToParent(true);
            contentTV.setLongClickable(false);
            Spanned rcontent = Html.fromHtml(refer.referbody);
            contentTV.setText(rcontent);
            MyURLSpan.parseLinkText(contentTV, rcontent);

        }

        // 设置布局参数
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

        // 计算margin指数，这个指数的意义在于将第一个的margin值设置为最大的，然后依次递减体现层叠效果
        int marginIndex = count - index;
        int margin = marginIndex * 8;

        params.setMargins(margin, margin, margin, 0);
        commentView.setLayoutParams(params);

        return commentView;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
		/*
		 * 在FloorView绘制子控件前先绘制层叠的背景图片
		 */
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View view = getChildAt(i);
            drawable.setBounds(view.getLeft(), view.getLeft(), view.getRight(),
                    view.getBottom());
            drawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }


}
