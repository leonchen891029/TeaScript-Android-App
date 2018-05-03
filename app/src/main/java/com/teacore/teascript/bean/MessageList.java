package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 留言列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */
@XStreamAlias("teascript")
public class MessageList extends Entity implements EntityList<Message>{

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("messagecount")
    private int messageCount;

    @XStreamAlias("messages")
    private List<Message> messageList = new ArrayList<Message>();

    public int getPageSize() {
        return pageSize;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public List<Message> getList() {
        return messageList;
    }

}
