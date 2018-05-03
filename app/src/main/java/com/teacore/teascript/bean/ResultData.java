package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**网络数据结果实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */
@XStreamAlias("teascript")
public class ResultData extends Base{

    @XStreamAlias("result")
    private Result result;

    @XStreamAlias("notice")
    private Notice notice;

    @XStreamAlias("comment")
    private Comment comment;

    //现在pub_message接口返回的是comment对象。
    //@XStreamAlias("message")
    private ChatMessage message;

    @XStreamAlias("relation")
    private int relation;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ChatMessage getMessage() {
        //现在pub_message接口返回的是comment对象。所以要转成message
        message = new ChatMessage();
        if(comment!=null) {
            message.setId(comment.getId());
            message.setPortrait(comment.getPortrait());
            message.setAuthor(comment.getAuthor());
            message.setAuthorId(comment.getId());
            message.setContent(comment.getContent());
            message.setPubDate(comment.getPubDate());
        }
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

}
