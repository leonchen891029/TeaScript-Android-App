package com.teacore.teascript.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.ChatMessage;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

/**
 * 聊天记录适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class ChatMessageAdapter extends BaseListAdapter<ChatMessage>{

    private int maxWidth;
    private int maxHeight;
    private int minWidth;
    private int minHeight;
    private Context mContext;

    private OnRetrySendMessageListener mOnRetrySendMessageListener;

    public ChatMessageAdapter() {

            //设置图片的最大宽高和最小宽高
            int maxWidth = TDevice.getDisplayMetrics().widthPixels / 2;
            int maxHeight = maxWidth;
            int minWidth = maxWidth / 2;
            int minHeight = minWidth;
            
    }

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {

        mContext=parent.getContext();

        final ChatMessage item = mDatas.get(mDatas.size() - position - 1);

        int itemType = 0;
        if (item.getAuthorId() == AppContext.getInstance().getLoginUid()) {
            itemType = 1;
        }

        boolean needCreateView = false;
        ViewHolder vh = null;
        if (convertView == null) {
            needCreateView = true;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (vh == null || vh.type != itemType) {
            needCreateView = true;
        }

        if (needCreateView) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    itemType == 0 ? R.layout.list_cell_chat_from : R.layout.list_cell_chat_to,
                    null);
            vh = new ViewHolder(convertView);
            vh.type = itemType;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.userAV.setAvatarUrl(item.getPortrait());
        vh.userAV.setUserInfo(item.getAuthorId(), item.getAuthor());

        TypefaceUtils.setTypeface(vh.errorTV);

        //判断是不是图片
        if (item.getBtype() == 3) {
            showImage(vh, item);
        } else{
            //文本消息
            showText(vh, item);
        }
        showStatus(vh, item);

        //检查是否需要显示时间
        if (item.isShowDate()) {
            vh.timeTV.setText(TimeUtils.friendly_time(item.getPubDate()));
            vh.timeTV.setVisibility(View.VISIBLE);
        } else {
            vh.timeTV.setVisibility(View.GONE);
        }

        vh.contentTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
        vh.contentTTV.setFocusable(false);
        vh.contentTTV.setDispatchToParent(true);
        vh.contentTTV.setLongClickable(false);

        vh.imgIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() != null) {
                    String url = (String) view.getTag();
                    PreviewImageActivity.showImagePreview(view.getContext(), url);
                }
            }
        });

        vh.errorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() != null && mOnRetrySendMessageListener != null) {
                    mOnRetrySendMessageListener.onRetrySendMessage((int) view.getTag());
                }
            }
        });

        return convertView;
    }

    //显示文字消息
    private void showText(ViewHolder vh, ChatMessage msg) {
        vh.imgIV.setVisibility(View.GONE);
        vh.contentTTV.setVisibility(View.VISIBLE);
        Spanned span = Html.fromHtml(msg.getContent());
        span = EmojiInputUtils.displayEmoji(vh.contentTTV.getResources(), span);
        vh.contentTTV.setText(span);
        MyURLSpan.parseLinkText(vh.contentTTV, span);
    }

    //显示图片
    private void showImage(ViewHolder vh, ChatMessage msg) {

        RequestManager mImageLoader= Glide.with(mContext);

        vh.contentTTV.setVisibility(View.GONE);
        vh.imgIV.setVisibility(View.VISIBLE);
        //加载图片
        vh.imgIV.setImageResource(R.drawable.icon_img_loading);

        GlideImageLoader.loadImage(mImageLoader,vh.imgIV,msg.getContent(),R.drawable.loading);
    }

    //显示消息状态
    private void showStatus(ViewHolder vh, ChatMessage msg) {

        if (msg.getStatus() != null && msg.getStatus() != ChatMessage.MessageStatus.NORMAL) {
            vh.msgStatusRL.setVisibility(View.VISIBLE);
            if (msg.getStatus() == ChatMessage.MessageStatus.SENDING) {
                //sending 正在发送
                vh.chatPB.setVisibility(View.VISIBLE);
                vh.errorTV.setVisibility(View.GONE);
                vh.errorTV.setTag(null);
            } else {
                //error 发送出错
                vh.chatPB.setVisibility(View.GONE);
                vh.errorTV.setVisibility(View.VISIBLE);
                //设置tag为msg id，以便点击重试发送
                vh.errorTV.setTag(msg.getId());

            }
        } else {
            //注意，此处隐藏要用INVISIBLE，不能使用GONE
            vh.msgStatusRL.setVisibility(View.INVISIBLE);
            vh.errorTV.setTag(null);
        }
    }

    public OnRetrySendMessageListener getOnRetrySendMessageListener() {
        return mOnRetrySendMessageListener;
    }

    public void setOnRetrySendMessageListener(OnRetrySendMessageListener
                                                      onRetrySendMessageListener) {
        this.mOnRetrySendMessageListener = onRetrySendMessageListener;
    }

    @Override
    protected boolean hasFooterView() {
        return false;
    }

    class ViewHolder {

        int type;

        AvatarView userAV;

        TextView timeTV;

        TeatimeTextView contentTTV;

        ImageView imgIV;

        ProgressBar chatPB;

        RelativeLayout msgStatusRL;

        TextView errorTV;

        ViewHolder(View view) {

            userAV=(AvatarView) view.findViewById(R.id.user_av);
            timeTV=(TextView) view.findViewById(R.id.time_tv);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);
            imgIV=(ImageView) view.findViewById(R.id.img_riv);
            chatPB=(ProgressBar) view.findViewById(R.id.chat_pb);
            msgStatusRL=(RelativeLayout) view.findViewById(R.id.msg_status_rl);
            errorTV=(TextView) view.findViewById(R.id.error_itv);

        }

    }

    public interface OnRetrySendMessageListener {
        void onRetrySendMessage(int msgId);
    }

}
