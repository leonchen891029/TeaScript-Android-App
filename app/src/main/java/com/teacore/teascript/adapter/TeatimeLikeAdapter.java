package com.teacore.teascript.adapter;

import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.TeatimeLike;
import com.teacore.teascript.util.PlatformUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;

/**
 * 被点赞的teatime的适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeatimeLikeAdapter extends BaseListAdapter<TeatimeLike>{

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_teatime_like, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final TeatimeLike item = (TeatimeLike) mDatas.get(position);

        vh.userAV.setUserInfo(item.getUser().getId(), item.getUser().getName());
        vh.userAV.setAvatarUrl(item.getUser().getPortrait());

        vh.nameTV.setText(item.getUser().getName().trim());

        vh.timeTV.setText(TimeUtils.friendly_time(item.getDataTime().trim()));

        vh.actionTV.setText("赞了我的动弹");

        vh.replyTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
        vh.replyTTV.setFocusable(false);
        vh.replyTTV.setDispatchToParent(true);
        vh.replyTTV.setLongClickable(false);
        Spanned span = UiUtils.parseActiveReply(item.getTeatime().getAuthor(),
                item.getTeatime().getBody());
        vh.replyTTV.setText(span);
        MyURLSpan.parseLinkText(vh.replyTTV, span);

        PlatformUtils.setPlatFromString(vh.fromTV, item.getAppClient());

        return convertView;
    }

    static class ViewHolder {

        AvatarView userAV;
        TextView nameTV;
        TextView timeTV;
        TextView actionTV;
        TeatimeTextView replyTTV;
        TextView fromTV;

        public ViewHolder(View view) {

            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            timeTV=(TextView) view.findViewById(R.id.time_tv);
            actionTV=(TextView) view.findViewById(R.id.action_tv);
            replyTTV=(TeatimeTextView) view.findViewById(R.id.reply_ttv);
            fromTV=(TextView) view.findViewById(R.id.from_tv);

        }
    }
}
