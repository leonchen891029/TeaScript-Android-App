package com.teacore.teascript.module.general.adapter.generaladapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.adapter.ViewHolder;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.bean.Blog;
import com.teacore.teascript.module.general.fragment.UserBlogFragment;
import com.teacore.teascript.module.general.fragment.generallistfragment.BlogGeneralListFragment;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TsImageSpan;

import java.util.List;

/**
 *综合界面下博客的适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-1
 */

public class BlogAdapter extends BaseListAdapter<Blog>{

    private boolean isUserBlog;

    public BlogAdapter(Callback callback){
        super(callback);
    }

    public void setUserBlog(boolean userBlog){
        this.isUserBlog=userBlog;
    }

    @Override
    protected void convert(ViewHolder vh,Blog item,int position){

        switch(getItemViewType(position)){

            case Blog.VIEW_TYPE_HEAT_LINE:
                vh.setText(R.id.blog_banner_tv,R.string.blog_list_title_heat);
                break;

            case Blog.VIEW_TYPE_LATELY_LINE:
                vh.setText(R.id.blog_banner_tv,R.string.blog_list_title_lately);
                break;

            case Blog.VIEW_TYPE_DATA:
                TextView titleTV=vh.getView(R.id.blog_title_tv);
                TextView contentTV=vh.getView(R.id.blog_body_tv);
                TextView historyTV=vh.getView(R.id.blog_history_tv);
                TextView seeTV=vh.getView(R.id.info_see_tv);
                TextView commentTV=vh.getView(R.id.info_comment_tv);

                String text="";

                SpannableStringBuilder spannable=new SpannableStringBuilder(text);

                if(item.isOriginal()){
                    spannable.append("[icon] ");
                    Drawable drawable= ContextCompat.getDrawable(mCallback.getContext(),R.drawable.icon_label_originate);
                    //转换为相应大小的drawable(清晰)
                    drawable= TsImageSpan.zoomDrawable(drawable,150,150);
                    drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                    ImageSpan imageSpan=new ImageSpan(drawable,ImageSpan.ALIGN_BOTTOM);
                    spannable.setSpan(imageSpan,0,6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                if(item.isRecommend()){
                    spannable.append("[icon] ");
                    Drawable drawable =ContextCompat.getDrawable(mCallback.getContext(),R.drawable.icon_label_recommend);
                    //转换为相应大小的drawable(清晰)
                    drawable= TsImageSpan.zoomDrawable(drawable,150,150);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    spannable.setSpan(imageSpan, 7, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                titleTV.setText( spannable.append(item.getTitle()) );

                String cacheName= BlogGeneralListFragment.HISTORY_BLOG;

                if(isUserBlog){
                    cacheName= UserBlogFragment.HISTORY_BLOG;
                }

                if(AppContext.isOnReadedPostList(cacheName,item.getId()+"")){
                    titleTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light));
                    contentTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light));
                } else {
                    titleTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.title_text_color_light));
                    contentTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.ques_bt_text_color_dark));
                }

                String body=item.getBody();
                if(body!=null){
                    contentTV.setText(body.trim());
                }

                String author=item.getAuthor();
                if(author!=null){
                    author=author.trim();
                    historyTV.setText((author.length() > 9 ? author.substring(0, 9) : author) + "  " + TimeUtils.friendly_time(item.getPubDate().trim()));
                }

                seeTV.setText(item.getViewCount() + "");
                commentTV.setText(item.getCommentCount() + "");

                break;

        }

    }

    @Override
    protected int getLayoutId(int position,Blog item){
        return item.getViewType() == Blog.VIEW_TYPE_DATA ? R.layout.general_list_cell_blog : R.layout.general_list_cell_blog_line;
    }

    @Override
    public int getItemViewType(int position){

        List<Blog> datas=getDatas();

        return datas.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


}
