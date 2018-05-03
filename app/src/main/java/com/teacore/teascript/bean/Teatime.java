package com.teacore.teascript.bean;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.util.UiUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Teatime实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
 */
@XStreamAlias("teatime")
public class Teatime extends Entity implements Parcelable{


    private String authorPortrait;

    private String author;

    private long authorId;

    private String body;

    @XStreamAlias("appclient")
    private int appClient;

    private int commentCount;

    private String pubDate;

    private String imgSmall;

    private String imgBig;

    private String attach;

    private int likeCount;

    private int isLike;

    @XStreamImplicit()
    private List<User> likeUsers = new ArrayList<User>();

    private String imageFilePath;

    private String audioPath;

    private int pageIndex;

    private int catalog;

    public Teatime() {
    }

    public Teatime(Parcel dest) {
        id = dest.readInt();
        authorPortrait = dest.readString();
        author = dest.readString();
        authorId = dest.readInt();
        body = dest.readString();
        appClient = dest.readInt();
        commentCount = dest.readInt();
        pubDate = dest.readString();
        imgSmall = dest.readString();
        imgBig = dest.readString();
        attach = dest.readString();
        imageFilePath = dest.readString();
        audioPath = dest.readString();
        isLike = dest.readInt();
        likeCount = dest.readInt();
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getAuthorPortrait() {
        return authorPortrait;
    }

    public void setAuthorPortrait(String authorPortrait) {
        this.authorPortrait = authorPortrait;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorid) {
        this.authorId = authorid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getAppClient() {
        return appClient;
    }

    public void setAppClient(int appclient) {
        this.appClient = appclient;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getImgSmall() {
        return imgSmall;
    }

    public void setImgSmall(String imgSmall) {
        this.imgSmall = imgSmall;
    }

    public String getImgBig() {
        return imgBig;
    }

    public void setImgBig(String imgBig) {
        this.imgBig = imgBig;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public List<User> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<User> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getPageIndex(){
        return pageIndex;
    }

    public void setPageIndex(int pageIndex){
        this.pageIndex=pageIndex;
    }

    public int getCatalog(){
        return catalog;
    }

    public void setCatalog(int catalog){
        this.catalog=catalog;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(authorPortrait);
        dest.writeString(author);
        dest.writeLong(authorId);
        dest.writeString(body);
        dest.writeInt(appClient);
        dest.writeInt(commentCount);
        dest.writeString(pubDate);
        dest.writeString(imgSmall);
        dest.writeString(imgBig);
        dest.writeString(attach);
        dest.writeString(imageFilePath);
        dest.writeString(audioPath);
        dest.writeInt(isLike);
        dest.writeInt(likeCount);
    }

    public static final Parcelable.Creator<Teatime> CREATOR = new Creator<Teatime>() {

        @Override
        public Teatime[] newArray(int size) {
            return new Teatime[size];
        }

        @Override
        public Teatime createFromParcel(Parcel source) {
            return new Teatime(source);
        }
    };

    public void setLikeUsers(Context context, TextView textView, boolean limit){

        //构造多个超链接的html，通过选中的位置来获取用户名
        if(getLikeCount()>0 && getLikeUsers()!=null && !getLikeUsers().isEmpty()){
            textView.setVisibility(View.VISIBLE);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setFocusable(false);
            textView.setLongClickable(false);
            textView.setText(addClickablePart(context,limit), TextView.BufferType.SPANNABLE);
        }else{
            textView.setVisibility(View.GONE);
            textView.setText("");
        }
        textView.setVisibility(View.GONE);
    }

    private SpannableStringBuilder addClickablePart(final Context context, boolean limit){
        StringBuilder stringBuilder=new StringBuilder();
        int showCount=getLikeUsers().size();
        if(limit && showCount>4){
            showCount=4;
        }

        //如果已经点赞了，并且该用户已经登录，则让用户始终在首位
        if(getIsLike()==1){

            for (int i = 0; i < getLikeUsers().size(); i++) {

                if (getLikeUsers().get(i).getId() == AppContext.getInstance()
                        .getLoginUid()) {
                    getLikeUsers().remove(i);
                }

            }

            getLikeUsers().add(0, AppContext.getInstance().getLoginUser());
        }

        for (int i = 0; i < showCount; i++) {
            stringBuilder.append(getLikeUsers().get(i).getName()).append("、");
        }

        String likeUsersString = stringBuilder.substring(0, stringBuilder.lastIndexOf("、"));

        SpannableString spannableString=new SpannableString("");
        SpannableStringBuilder ssb = new SpannableStringBuilder(spannableString);

        ssb.append(likeUsersString);

        String[] likeUsers = likeUsersString.split("、");

        if (likeUsers.length > 0) {
            // 最后一个
            for (int i = 0; i < likeUsers.length; i++) {

                final String name = likeUsers[i];

                final int start = likeUsersString.indexOf(name) + spannableString.length();

                final int index = i;

                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {

                        User user = getLikeUsers().get(index);

                        UiUtils.showUserCenter(context, user.getId(),
                                user.getName());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {

                        super.updateDrawState(ds);

                        ds.setUnderlineText(false);

                    }
                }, start, start + name.length(), 0);

            }
        }

        if (likeUsers.length < getLikeCount()) {
            ssb.append("等");
            int start = ssb.length();
            String more = getLikeCount() + "人";
            ssb.append(more);
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, getId());
                    UiUtils.showSimpleBack(context,
                            BackFragmentEnum.TEATIME_LIKE_USER, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // ds.setColor(R.color.link_color); // 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, start, start + more.length(), 0);
            return ssb.append("觉得很赞");
        } else {
            return ssb.append("觉得很赞");
        }
    }

}
