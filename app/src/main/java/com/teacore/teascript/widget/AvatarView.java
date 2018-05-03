package com.teacore.teascript.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UiUtils;

/**头像view
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-28
 */

public class AvatarView extends CircleImageView{

    public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
    public static final String MIDDLE_SIZE = "_100";
    public static final String LARGE_SIZE = "_200";

    private long id;
    private String name;
    private Activity activity;
    private Context mContext;
    private RequestManager mImageLoader;

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;
        mImageLoader= Glide.with(context);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        mImageLoader=Glide.with(context);
        init(context);
    }

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        activity = (Activity) context;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name)) {
                    UiUtils.showUserCenter(getContext(), id, name);
                }
            }
        });
    }

    public void setUserInfo(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setAvatarUrl(String url) {

        if (StringUtils.isEmpty(url)) {
            setImageResource(R.drawable.widget_dface);
            return;
        }
        // 由于头像地址默认加了一段参数需要去掉
        int end = url.indexOf('?');
        final String headUrl;
        if (end > 0) {
            headUrl = url.substring(0, end);
        } else {
            headUrl = url;
        }

        GlideImageLoader.loadImage(mImageLoader,this,headUrl,R.drawable.loading,R.drawable.widget_dface,false);
    }

    public static String getSmallAvatar(String source) {
        return source;
    }

    public static String getMiddleAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
    }

    public static String getLargeAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
    }

}
