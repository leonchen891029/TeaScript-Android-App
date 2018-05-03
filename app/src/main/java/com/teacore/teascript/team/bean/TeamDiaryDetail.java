package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 周报实体类的包装类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-25
 */
@XStreamAlias("teascript")
public class TeamDiaryDetail extends Entity{

    @XStreamAlias("teamdiary")
    private TeamDiary teamDiary;

    public TeamDiary getTeamDiary() {
        return teamDiary;
    }

    public void setTeamDiary(TeamDiary teamDiary) {
        this.teamDiary = teamDiary;
    }

}
