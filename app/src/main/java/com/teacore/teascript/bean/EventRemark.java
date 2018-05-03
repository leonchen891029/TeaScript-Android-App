package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;

/**活动备注类
 *@author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
 */
@XStreamAlias("eventremark")
public class EventRemark implements Serializable{


    @XStreamAlias("remarktip")
    private String remarkTip;

    @XStreamAlias("remarkselect")
    private RemarksSelet select;

    public class RemarksSelet implements Serializable {

        @XStreamImplicit(itemFieldName = "select")
        private List<String> list;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

    }

    public String getRemarkTip() {
        return remarkTip;
    }

    public void setRemarkTip(String remarkTip) {
        this.remarkTip = remarkTip;
    }

    public RemarksSelet getSelect() {
        return select;
    }

    public void setSelect(RemarksSelet select) {
        this.select = select;
    }



}
