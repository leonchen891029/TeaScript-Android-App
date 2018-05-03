package com.teacore.teascript.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.Comment.Refer;
import com.teacore.teascript.bean.Comment.Reply;
import com.teacore.teascript.util.PlatformUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.FloorView;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

import java.util.List;

/**评论适配类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-28
 */

public class CommentAdapter extends BaseListAdapter<Comment> {

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {

        ViewHolder vh = null;

        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_comment, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        try {

            final Comment item = mDatas.get(position);


            vh.userAV.setAvatarUrl(item.getPortrait());
            vh.userAV.setUserInfo(item.getAuthorId(), item.getAuthor());

            // 若Authord为0，则显示非会员
            vh.nameTV.setText(item.getAuthor()
                    + (item.getAuthorId() == 0 ? "(非会员)" : ""));

            vh.contentTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
            vh.contentTTV.setFocusable(false);
            vh.contentTTV.setDispatchToParent(true);
            vh.contentTTV.setLongClickable(false);

            Spanned span = Html.fromHtml(TeatimeTextView.modifyPath(item
                    .getContent()));

            span = EmojiInputUtils.displayEmoji(parent.getContext().getResources(),
                    span.toString());

            vh.contentTTV.setText(span);

            MyURLSpan.parseLinkText(vh.contentTTV, span);

            vh.timeTV.setText(TimeUtils.friendly_time(item.getPubDate()));

            PlatformUtils.setPlatFromString(vh.fromTV, item.getAppClient());

            //初始化引用
            setupRefers(parent.getContext(), vh, item.getRefers());

            //初始化回复
            setupReplies(parent.getContext(), vh, item.getReplies());


        } catch (Exception e) {
        }
        return convertView;
    }

    private void setupRefers(Context context, ViewHolder vh, List<Refer> refers) {

        vh.refersFV.removeAllViews();

        if (refers == null || refers.size() <= 0) {

            vh.refersFV.setVisibility(View.GONE);
        } else {

            vh.refersFV.setVisibility(View.VISIBLE);

            vh.refersFV.setComments(refers);
        }
    }


    private void setupReplies(Context context, ViewHolder vh,
                              List<Reply> replies) {

        vh.reliesLL.removeAllViews();

        if (replies == null || replies.size() <= 0) {
            vh.reliesLL.setVisibility(View.GONE);
        } else {
            vh.reliesLL.setVisibility(View.VISIBLE);

            //增加一个统计回复数的countView
            View countView = getLayoutInflater(context).inflate(
                    R.layout.list_cell_reply_count, null, false);

            TextView count = (TextView) countView
                    .findViewById(R.id.comment_reply_count_tv);

            count.setText(context.getResources().getString(
                    R.string.comment_reply_count, replies.size()));

            vh.reliesLL.addView(countView);

            //添加一系列的replies
            for (Reply reply : replies) {

                LinearLayout replyItemView = (LinearLayout) getLayoutInflater(
                        context).inflate(R.layout.list_cell_reply_name_content,
                        null, false);

                replyItemView.setOrientation(LinearLayout.HORIZONTAL);

                replyItemView.setBackgroundResource(R.drawable.comment_background);

                TextView name = (TextView) replyItemView
                        .findViewById(R.id.reply_name_tv);

                name.setText(reply.rauthor + ":");

                TeatimeTextView replyContent = (TeatimeTextView) replyItemView
                        .findViewById(R.id.reply_content_tv);

                replyContent.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
                replyContent.setFocusable(false);
                replyContent.setDispatchToParent(true);
                replyContent.setLongClickable(false);
                Spanned rcontent = Html.fromHtml(reply.rcontent);
                replyContent.setText(rcontent);
                MyURLSpan.parseLinkText(replyContent, rcontent);

                vh.reliesLL.addView(replyItemView);
            }
        }
    }

    static class ViewHolder{

        AvatarView userAV;
        TextView nameTV;
        TextView timeTV;
        TextView fromTV;
        TeatimeTextView contentTTV;
        LinearLayout reliesLL;
        FloorView refersFV;

        ViewHolder(View view){
            userAV=(AvatarView) view.findViewById(R.id.avatar_iv);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            timeTV=(TextView) view.findViewById(R.id.time_tv);
            fromTV=(TextView) view.findViewById(R.id.from_tv);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_tv);
            reliesLL=(LinearLayout) view.findViewById(R.id.relies_ll);
            refersFV=(FloorView) view.findViewById(R.id.refers_fv);
        }

    }

}
