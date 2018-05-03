package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**作者实体类(帖子，任务，讨论的创建者)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */
@XStreamAlias("author")
public class Author extends Entity{
    @XStreamAlias("name")
    private String name;

    @XStreamAlias("portrait")
    private String portrait;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
