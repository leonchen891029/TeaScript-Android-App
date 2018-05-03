package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * 消息实体类
 * @author  陈晓帆
 * @version  1.0
 * @created  2017.1.1

 */

@XStreamAlias("notice")

public class Notice implements Serializable{

       public final static String UTF8="UTF-8";
       public final static String NODE_ROOT="teacore";

       public final static int  TYPE_ATME=1;
       public final static int  TYPE_MESSAGE=2;
       public final static int  TYPE_COMMENT=3;
       public final static int  TYPE_NEWFAN=4;
       public final static int  TYPE_NEWLIKE=5;

       @XStreamAlias("atmeCount")
       private int atmeCount;

       @XStreamAlias("msgCount")
       private int msgCount;

       @XStreamAlias("commentCount")
       private int commentCount;

       @XStreamAlias("newFansCount")
       private int newFansCount;

       @XStreamAlias("newLikeCount")
       private int  newLikeCount;

       public int getAtmeCount(){
           return atmeCount;
       }

       public void setAtmeCount(int atmeCount){
           this.atmeCount=atmeCount;
       }

       public int  getMsgCount(){
           return msgCount;
       }

       public void setMsgCount(int msgCount){
           this.msgCount=msgCount;
       }

       public int getCommentCount(){
           return commentCount;
       }

       public void setCommentCount(int commentCount){
           this.commentCount=commentCount;
       }

       public int getNewFansCount() {
           return newFansCount;
       }

       public void setNewFansCount(int newFansCount) {
             this.newFansCount = newFansCount;
       }

       public int getNewLikeCount(){
           return newLikeCount;
       }

       public void setNewLikeCount(int newLikeCount) {
           this.newLikeCount = newLikeCount;
       }

}
