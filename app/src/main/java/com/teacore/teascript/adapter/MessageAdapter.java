package com.teacore.teascript.adapter;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Message;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;

/**
 * 留言适配器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-6
 */

public class MessageAdapter extends BaseListAdapter<Message>{

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_message, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Message item = (Message) mDatas.get(position);

        vh.userAV.setAvatarUrl(item.getPortrait());
        vh.userAV.setUserInfo(item.getSenderId(), item.getSender());

        if (AppContext.getInstance().getLoginUid() == item.getSenderId()) {
            vh.senderTV.setVisibility(View.VISIBLE);
        } else {
            vh.senderTV.setVisibility(View.GONE);
        }

        vh.nameTV.setText(item.getFriendName());

        vh.contentTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
        vh.contentTTV.setFocusable(false);
        vh.contentTTV.setDispatchToParent(true);
        vh.contentTTV.setLongClickable(false);
        Spanned span = Html.fromHtml(item.getContent());
        vh.contentTTV.setText(span);
        MyURLSpan.parseLinkText(vh.contentTTV, span);

        vh.timeTV.setText(TimeUtils.friendly_time(item.getPubDate()));
        vh.countTV.setText(parent.getResources().getString(
                R.string.message_count, item.getMessageCount()));

        return convertView;
    }

    static class ViewHolder{

        AvatarView userAV;
        TextView senderTV;
        TextView nameTV;
        TeatimeTextView contentTTV;
        TextView timeTV;
        TextView countTV;

        ViewHolder(View view){
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            senderTV=(TextView) view.findViewById(R.id.sender_tv);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);
            timeTV=(TextView) view.findViewById(R.id.time_tv);
            countTV=(TextView) view.findViewById(R.id.count_tv);
        }

    }














}
