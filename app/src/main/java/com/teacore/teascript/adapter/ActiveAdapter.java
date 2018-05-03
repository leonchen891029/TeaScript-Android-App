package com.teacore.teascript.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Active;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.util.DensityUtils;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.PlatformUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

/**
 *动态适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-12
 */
public class ActiveAdapter extends BaseListAdapter{

    //个人主页地址
    private final static String AT_HOST_PRE="http://my.teascript.com";
    //网站地址
    private final static String MAIN_HOST="http://www.teascript.com";

    //record图片
    private Bitmap recordBitmap;
    //record图片大小
    private int imageSize;

    private RequestManager mImageLoader;

    public ActiveAdapter(){}

    //初始化Record图片
    private void initRecordBitmap(Context context){
        recordBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_audio3);
        recordBitmap= ImageUtils.zoomBitmap(recordBitmap, DensityUtils.dp2px(context,20f),DensityUtils.dp2px(context,20f));
    }

    //初始化图片大小
    private void initImageSize(Context context){
        if(context!=null && imageSize==0){
            imageSize=(int) context.getResources().getDimension(R.dimen.space_100);
        }else{
            imageSize=300;
        }
    }

    //加载active视图
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh;

        initImageSize(parent.getContext());

        mImageLoader= Glide.with(parent.getContext());

        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_active, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Active active = (Active) mDatas.get(position);

        vh.userAV.setUserInfo(active.getAuthorId(), active.getAuthor());
        vh.userAV.setAvatarUrl(active.getPortrait());

        vh.nameTV.setText(active.getAuthor());

        vh.pubDateTV.setText(TimeUtils.friendly_time(active.getPubDate()));

        vh.actionTV.setText(UiUtils.parseActiveAction(active.getObjectType(),
                active.getObjectCatalog(), active.getObjectTitle()));

        if (TextUtils.isEmpty(active.getMessage())) {
            vh.contentTTV.setVisibility(View.GONE);
        } else {
            vh.contentTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
            vh.contentTTV.setFocusable(false);
            vh.contentTTV.setDispatchToParent(true);
            vh.contentTTV.setLongClickable(false);

            Spanned span = Html.fromHtml(modifyPath(active.getMessage()));

            if (!StringUtils.isEmpty(active.getTeatimeAttach())) {
                if (recordBitmap == null) {
                    initRecordBitmap(parent.getContext());
                }
                ImageSpan recordImg = new ImageSpan(parent.getContext(),
                        recordBitmap);
                SpannableString str = new SpannableString("c");
                str.setSpan(recordImg, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                vh.contentTTV.setText(str);
                span = EmojiInputUtils.displayEmoji(parent.getContext()
                        .getResources(), span);
                vh.contentTTV.append(span);
            } else {
                span = EmojiInputUtils.displayEmoji(parent.getContext()
                        .getResources(), span);
                vh.contentTTV.setText(span);
            }
            MyURLSpan.parseLinkText(vh.contentTTV, span);
        }

        Active.ObjectReply reply = active.getObjectReply();

        if (reply != null) {
            vh.replyTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
            vh.replyTTV.setFocusable(false);
            vh.replyTTV.setDispatchToParent(true);
            vh.replyTTV.setLongClickable(false);
            Spanned span = UiUtils.parseActiveReply(reply.objectName,
                    reply.objectBody);
            vh.replyTTV.setText(span);
            MyURLSpan.parseLinkText(vh.replyTTV, span);
            vh.replyLL.setVisibility(TextView.VISIBLE);
        } else {
            vh.replyTTV.setText("");
            vh.replyLL.setVisibility(TextView.GONE);
        }

        if (!TextUtils.isEmpty(active.getTeatimeImage())) {
            setTeatimeImage(parent, vh, active);
        } else {
            vh.picIV.setVisibility(View.GONE);
            vh.picIV.setImageBitmap(null);
        }

        PlatformUtils.setPlatFromString(vh.fromTV, active.getAppClient());

        vh.commentCountTV.setText(active.getCommentCount() + "");

        return convertView;
    }

    private void setTeatimeImage(final ViewGroup parent, final ViewHolder vh,
                               final Active active) {

        vh.picIV.setVisibility(View.VISIBLE);

        GlideImageLoader.loadImage(mImageLoader,vh.picIV,active.getTeatimeImage(),R.drawable.pic_bg);

        vh.picIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewImageActivity.showImagePreview(parent.getContext(), getOriginalUrl(active.getTeatimeImage()));
            }
        });
    }

    private String modifyPath(String message) {
        message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
                + AT_HOST_PRE + "/$2\"");
        message = message.replaceAll(
                "(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"
                        + MAIN_HOST + "$2\"");
        return message;
    }

    private String getOriginalUrl(String url) {
        return url.replaceAll("_thumb", "");
    }

    static class ViewHolder {

        AvatarView userAV;

        TextView nameTV;

        TextView pubDateTV;

        TextView actionTV;

        TextView actionNameTV;

        TeatimeTextView contentTTV;

        LinearLayout replyLL;

        TeatimeTextView replyTTV;

        ImageView picIV;

        TextView fromTV;

        TextView commentCountTV;

        public ViewHolder(View view) {

            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            pubDateTV=(TextView) view.findViewById(R.id.pub_date_tv);
            actionTV=(TextView) view.findViewById(R.id.action_tv);
            actionNameTV=(TextView) view.findViewById(R.id.action_name_tv);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);
            replyLL=(LinearLayout) view.findViewById(R.id.reply_ll);
            replyTTV=(TeatimeTextView) view.findViewById(R.id.reply_ttv);
            picIV=(ImageView) view.findViewById(R.id.pic_iv);
            fromTV=(TextView) view.findViewById(R.id.from_tv);
            commentCountTV=(TextView) view.findViewById(R.id.comment_count_tv);

        }
    }



}
