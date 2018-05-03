package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**TeamReply详情类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-6
 */
@XStreamAlias("teascript")
public class TeamReplyDetail extends Entity{

    @XStreamAlias("reply")
    private TeamReply teamReply;

    public TeamReply getTeamReply() {
        return teamReply;
    }

    public void setTeamReply(TeamReply teamReply) {
        this.teamReply = teamReply;
    }

}
