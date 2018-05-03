package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;


/**团队Git实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */

public class TeamGit extends Entity {

    @XStreamAlias("name")
    private String name;

    @XStreamAlias("path")
    private String path;

    @XStreamAlias("ownerName")
    private String ownerName;

    @XStreamAlias("ownerUserName")
    private String ownerUserName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

}
