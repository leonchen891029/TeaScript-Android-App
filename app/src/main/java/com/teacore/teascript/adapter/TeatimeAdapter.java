package com.teacore.teascript.adapter;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.DensityUtils;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.PlatformUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class TeatimeAdapter extends BaseListAdapter<Teatime> {

    private Context context;
    private Bitmap recordBitmap;
    private RequestManager mImageLoader;

    final private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
        }
    };

    private void initRecordBitmap(Context context){
        recordBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_audio3);
        recordBitmap = ImageUtils.zoomBitmap(recordBitmap,
               DensityUtils.dp2px(context,20f),    DensityUtils.dp2px(context,20f));
    }

    static class ViewHolder{

        AvatarView mAuthorAV;
        TextView mNameTV;
        TeatimeTextView mContentTTV;
        ImageView mTeatimeIV;
        TextView mLikeUsersTV;
        TextView mTimeTV;
        TextView mPlatformTV;
        ImageView mLikeStateIV;
        TextView mLikeCountTV;
        TextView mCommentCountTV;

        public ViewHolder(View view){
            mAuthorAV=(AvatarView) view.findViewById(R.id.teatime_av);
            mNameTV=(TextView) view.findViewById(R.id.teatime_name_tv);
            mContentTTV=(TeatimeTextView) view.findViewById(R.id.teatime_content_ttv);
            mTeatimeIV=(ImageView) view.findViewById(R.id.teatime_iv);
            mLikeUsersTV=(TextView) view.findViewById(R.id.teatime_likeusers_tv);
            mTimeTV=(TextView) view.findViewById(R.id.teatime_time_tv);
            mPlatformTV=(TextView) view.findViewById(R.id.teatime_platform_tv);
            mLikeStateIV=(ImageView) view.findViewById(R.id.teatime_likestate_iv);
            mLikeCountTV=(TextView) view.findViewById(R.id.teatime_like_count_tv);
            mCommentCountTV=(TextView) view.findViewById(R.id.teatime_comment_count_tv);
        }

    }

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent){
        context=parent.getContext();
        mImageLoader= Glide.with(context);
        final ViewHolder viewHolder;
        if(convertView==null||convertView.getTag()==null){
            convertView=View.inflate(context,R.layout.list_cell_teatime,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        final Teatime Teatime=mDatas.get(position);

        viewHolder.mAuthorAV.setUserInfo(Teatime.getAuthorId(),Teatime.getAuthor());
        viewHolder.mAuthorAV.setAvatarUrl(Teatime.getAuthorPortrait());
        viewHolder.mNameTV.setText(Teatime.getAuthor());
        viewHolder.mTimeTV.setText(TimeUtils.friendly_time(Teatime.getPubDate()));
        viewHolder.mContentTTV.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
        viewHolder.mContentTTV.setFocusable(false);
        viewHolder.mContentTTV.setDispatchToParent(true);
        viewHolder.mContentTTV.setLongClickable(false);
        viewHolder.mLikeCountTV.setText(String.valueOf(Teatime.getLikeCount()));

        Spanned span= Html.fromHtml(Teatime.getBody().trim());

        if(!StringUtils.isEmpty(Teatime.getAttach())){

            if(recordBitmap==null){
                initRecordBitmap(context);
            }

            ImageSpan recordImgSpan=new ImageSpan(context,recordBitmap);

            SpannableString spannableString=new SpannableString("c");

            spannableString.setSpan(recordImgSpan,0,1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            viewHolder.mContentTTV.setText(spannableString);

            span= EmojiInputUtils.displayEmoji(context.getResources(),span);

            viewHolder.mContentTTV.append(span);
        }else{
            span= EmojiInputUtils.displayEmoji(context.getResources(),span);
            viewHolder.mContentTTV.setText(span);
        }

        viewHolder.mCommentCountTV.setText(String.valueOf(Teatime.getCommentCount()));

        if(TextUtils.isEmpty(Teatime.getImgSmall())){
            viewHolder.mTeatimeIV.setVisibility(View.GONE);
        }else{
            viewHolder.mTeatimeIV.setVisibility(View.VISIBLE);

            GlideImageLoader.loadImage(mImageLoader,viewHolder.mTeatimeIV,Teatime.getImgSmall(),R.drawable.pic_bg);

            viewHolder.mTeatimeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreviewImageActivity.showImagePreview(context, Teatime.getImgBig());
                }
            });

        }

        Teatime.setLikeUsers(context,viewHolder.mLikeUsersTV,true);

        viewHolder.mLikeStateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppContext.getInstance().isLogin()) {
                    updateLikeState(viewHolder, Teatime);
                } else {
                    AppContext.showToast("先登陆再赞~");
                    UiUtils.showLoginActivity(context);
                }
            }
        });

        if (Teatime.getIsLike() == 1) {
            viewHolder.mLikeStateIV.setImageResource(R.drawable.icon_thumbup_selected);
        } else {
            viewHolder.mLikeStateIV.setImageResource(R.drawable.icon_thumbup_normal);
        }
        PlatformUtils.setPlatFromString(viewHolder.mPlatformTV, Teatime.getAppClient());
        return convertView;

    }

    private void updateLikeState(ViewHolder vh, Teatime Teatime) {
        if (Teatime.getIsLike() == 1) {
            Teatime.setIsLike(0);
            Teatime.setLikeCount(Teatime.getLikeCount() - 1);
            if (!Teatime.getLikeUsers().isEmpty()) {
                Teatime.getLikeUsers().remove(0);
            }
            TeaScriptApi.pubUnlikeTeatime(Teatime.getId(), Teatime.getAuthorId(),
                    handler);
            vh.mLikeStateIV.setImageResource(R.drawable.icon_thumbup_normal);
        } else {
            Teatime.getLikeUsers().add(0, AppContext.getInstance().getLoginUser());
            TeaScriptApi.pubLikeTeatime(Teatime.getId(), Teatime.getAuthorId(), handler);
            vh.mLikeStateIV.setImageResource(R.drawable.icon_thumbup_selected);
            Teatime.setIsLike(1);
            Teatime.setLikeCount(Teatime.getLikeCount() + 1);
        }
        vh.mLikeCountTV.setText(String.valueOf(Teatime.getLikeCount()));
        Teatime.setLikeUsers(context, vh.mLikeUsersTV, true);
    }

    private void deleteTeatime(Context context, final Teatime Teatime, final int position) {

        DialogUtils.getConfirmDialog(context, "确定删除吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TeaScriptApi.deleteTeatime(Teatime.getAuthorId(), Teatime.getId(),
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int arg0, Header[] arg1,
                                                  byte[] arg2) {
                                mDatas.remove(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(int arg0, Header[] arg1,
                                                  byte[] arg2, Throwable arg3) {
                            }
                        });
            }
        }).show();
    }

}
