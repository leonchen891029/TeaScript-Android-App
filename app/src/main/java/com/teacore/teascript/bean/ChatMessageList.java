package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天信息列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */
@XStreamAlias("teascript")
public class ChatMessageList extends Entity implements EntityList<ChatMessage>{

    @XStreamAlias("allCount")
    private int allCount;

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("messageslist")
    private List<ChatMessage> messagesList = new ArrayList<ChatMessage>();

    public int getPageSize() {
        return pageSize;
    }

    public int getMessageCount() {
        return allCount;
    }

    public List<ChatMessage> getMessagelist() {
        return messagesList;
    }

    @Override
    public List<ChatMessage> getList() {
        return messagesList;
    }
}
