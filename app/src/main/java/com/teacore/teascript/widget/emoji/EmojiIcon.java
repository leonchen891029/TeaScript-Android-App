package com.teacore.teascript.widget.emoji;

/**表情实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-20
 */

public class EmojiIcon {

    //图片资源地址
    private final int resId;
    //一个emoji对应唯一的一个值
    private final int value;
    //emoji在互联网传递的字符串
    private final String emojiStr;
    private final String remote;

    public EmojiIcon(int id,int value,String emojiStr,String remote){
        this.resId=id;
        this.value=value;
        this.emojiStr=emojiStr;
        this.remote=remote;
    }

    public int getResId() {
        return resId;
    }

    public String getRemote() {
        return remote;
    }

    public int getValue() {
        return value;
    }

    public String getEmojiStr() {
        return emojiStr;
    }

}

