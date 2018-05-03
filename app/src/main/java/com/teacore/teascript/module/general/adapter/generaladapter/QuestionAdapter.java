package com.teacore.teascript.module.general.adapter.generaladapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.adapter.ViewHolder;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.bean.Question;
import com.teacore.teascript.module.general.fragment.generallistfragment.QuestionGeneralListFragment;
import com.teacore.teascript.util.TimeUtils;

/**
 * 综合界面下的问答适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-13
 */

public class QuestionAdapter extends BaseListAdapter<Question>{

    private int actionPosition=0;

    public void setActionPosition(int actionPosition){
        this.actionPosition=actionPosition;
    }

    public QuestionAdapter(Callback callback){
        super(callback);
    }

    @Override
    protected int getLayoutId(int position, Question item) {
        return R.layout.general_list_cell_question;
    }

    private String verifyFileName() {
        switch (actionPosition) {
            case 1:
                return QuestionGeneralListFragment.QUES_ASK;
            case 2:
                return QuestionGeneralListFragment.QUES_SHARE;
            case 3:
                return QuestionGeneralListFragment.QUES_COMPOSITE;
            case 4:
                return QuestionGeneralListFragment.QUES_PROFESSION;
            case 5:
                return QuestionGeneralListFragment.QUES_WEBSITE;
            default:
                return QuestionGeneralListFragment.QUES_ASK;
        }
    }

    @Override
    protected void convert(ViewHolder vh,Question item,int position) {

        String authorPortrait=item.getAuthorPortrait();

        if(authorPortrait!=null){
            vh.setImageFromNet(R.id.question_author_civ,authorPortrait.trim(),R.drawable.widget_dface);
        }
        TextView titleTV=vh.getView(R.id.question_title_tv);
        String questionTitle=item.getTitle();
        if(questionTitle!=null){
            titleTV.setText(questionTitle.trim());
        }
        TextView contentTV=vh.getView(R.id.question_content_tv);
        String questionBody=item.getBody();
        if(questionBody!=null){
            questionBody=questionBody.trim();
            if(questionBody.length()>0&&!questionBody.equals("")){
                contentTV.setText(questionBody);
                contentTV.setVisibility(View.VISIBLE);
            }else{
                contentTV.setVisibility(View.GONE);
            }
        }
        String fileName=verifyFileName();

        if(AppContext.isOnReadedPostList(fileName,item.getId()+"")){
            titleTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light));
            contentTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light));
        }else{
            titleTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.title_text_color_light));
            contentTV.setTextColor(ContextCompat.getColor(mCallback.getContext(),R.color.ques_bt_text_color_dark));
        }
        TextView historyTV = vh.getView(R.id.question_history_tv);
        String author = item.getAuthor();
        if (author != null) {
            author = author.trim();
            historyTV.setText((author.length() > 9 ? author.substring(0, 9) : author.trim()) + "  " + TimeUtils.friendly_time(item.getPubDate().trim()));
        }
        TextView see = vh.getView(R.id.info_see_tv);
        see.setText(item.getViewCount() + "");
        TextView answer = vh.getView(R.id.info_comment_tv);
        answer.setText(item.getCommentCount() + "");
    }


}
