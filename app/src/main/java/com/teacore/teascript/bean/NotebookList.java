package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 便签列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-6
 */
@XStreamAlias("teascript")
public class NotebookList extends Entity implements EntityList<Notebook>{

    @XStreamAlias("notebooklist")
    private List<Notebook> notebookList = new ArrayList<Notebook>();

    @Override
    public List<Notebook> getList() {
        return notebookList;
    }

    public void setList(List<Notebook> list) {
        this.notebookList = list;
    }

}
