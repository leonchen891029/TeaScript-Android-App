package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Team动态detail bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */
@XStreamAlias("teascript")
public class TeamActiveDetail extends Entity{
    @XStreamAlias("active")
    private TeamActive teamActive;

    public TeamActive getTeamActive() {
        return teamActive;
    }

    public void setTeamActive(TeamActive teamActive) {
        this.teamActive = teamActive;
    }
}
