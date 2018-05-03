package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**TeamIssue详情类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */
@XStreamAlias("teascript")
public class TeamIssueDetail extends Entity{
    @XStreamAlias("issue")
    private TeamIssue teamIssue;

    public TeamIssue getTeamIssue() {
        return teamIssue;
    }

    public void setTeamIssue(TeamIssue teamIssue) {
        this.teamIssue = teamIssue;
    }
}
