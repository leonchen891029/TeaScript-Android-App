package com.teacore.teascript.module.general.adapter.generaladapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.adapter.ViewHolder;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.bean.News;
import com.teacore.teascript.module.general.fragment.generallistfragment.NewsGeneralListFragment;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TsImageSpan;

/**
 * 综合页面新闻的适配器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-1
 */

public class NewsAdapter extends BaseListAdapter<News>{

    private String systemTime;

    public NewsAdapter(Callback callback){
        super(callback);
    }

    @Override
    protected int getLayoutId(int position, News item){
        return R.layout.general_list_cell_news;
    }

    public void setSystemTime(String systemTime){
        this.systemTime=systemTime;
    }

    @Override
    protected void convert(ViewHolder vh, News item, int position) {

        if (AppContext.isOnReadedPostList(NewsGeneralListFragment.HISTORY_NEWS, item.getId() + "")) {
            vh.setTextColor(R.id.title_tv, ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light));
            vh.setTextColor(R.id.description_tv, ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light));
        } else {
            vh.setTextColor(R.id.title_tv, ContextCompat.getColor(mCallback.getContext(),R.color.title_text_color_light));
            vh.setTextColor(R.id.description_tv,  ContextCompat.getColor(mCallback.getContext(),R.color.ques_bt_text_color_dark));
        }

        vh.setText(R.id.description_tv, item.getBody());
        vh.setText(R.id.time_tv, TimeUtils.friendly_time(item.getPubDate()));
        vh.setText(R.id.comment_count_tv, String.valueOf(item.getCommentCount()));

        //设置是否是今日标签
        if (TimeUtils.isSameDay(systemTime, item.getPubDate())) {

            String text = "[icon] " + item.getTitle();
            Drawable drawable = ContextCompat.getDrawable(mCallback.getContext(),R.drawable.icon_label_today);

            //转换为相应大小的drawable(清晰)
            drawable= TsImageSpan.zoomDrawable(drawable,150,150);

            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

            SpannableString spannable = new SpannableString(text);

            //取代"[icon] "包括空格
            spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            vh.setText(R.id.title_tv,spannable);

        } else {
            vh.setText(R.id.title_tv,item.getTitle());
        }
    }

}
