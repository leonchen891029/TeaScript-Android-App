package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**团队任务详情类
 *@author 陈晓帆
 *@version 1.0
 * Created 2017-4-10
 */
@XStreamAlias("teascript")
public class TeamDiscussDetail extends Entity {

    @XStreamAlias("discuss")
    private TeamDiscuss discuss;

    public TeamDiscuss getDiscuss() {
        return discuss;
    }

    public void setDiscuss(TeamDiscuss discuss) {
        this.discuss = discuss;
    }
}