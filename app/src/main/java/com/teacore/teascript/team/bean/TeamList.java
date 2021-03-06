package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.util.StringUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 *团队信息列表Bean类
 * @author 陈晓帆
 * @versin 1.0
 * Created 2017-4-11
 */
@XStreamAlias("teascript")
public class TeamList extends Entity implements EntityList<Team>{

    @XStreamAlias("teams")
    ArrayList<Team> teams = new ArrayList<Team>();

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    @Override
    public List<Team> getList() {
        return teams;
    }

    public String toCacheData() {
        StringBuilder str = new StringBuilder();
        if (teams != null) {
            for (Team data : teams) {
                str.append(toTeamCacheData(data)).append(" <->");
            }
        }
        return str.toString();
    }

    public String toTeamCacheData(Team team) {
        StringBuilder str = new StringBuilder();
        str.append(team.getId()).append(">->").append(team.getType())
                .append(">->").append(team.getStatus()).append(">->")
                .append(team.getName()).append(">->").append(team.getIdent())
                .append(">->").append(team.getCreateTime()).append(">->")
                .append(team.getAbout().getSign()).append(">->")
                .append(team.getAbout().getAddress()).append(">->")
                .append(team.getAbout().getTelephone()).append(">->")
                .append(team.getAbout().getEmail()).append(">->")
                .append(team.getAbout().getQq()).append(">->");
        return str.toString();
    }

    public static Team toTeam(String cacheData) {
        Team team = new Team();
        if (!StringUtils.isEmpty(cacheData)) {
            String[] fields = cacheData.split(">->");
            if (fields.length >= 11 && fields[0] != null) { // 如果id都没有，表示是一个非法数据
                team.setId(StringUtils.toInt(fields[0]));
                team.setType(fields[1]);
                team.setStatus(fields[2]);
                team.setName(fields[3]);
                team.setIdent(fields[4]);
                team.setCreateTime(fields[5]);
                team.getAbout().setSign(fields[6]);
                team.getAbout().setAddress(fields[7]);
                team.getAbout().setTelephone(fields[8]);
                team.getAbout().setEmail(fields[9]);
                team.getAbout().setQq(fields[10]);
            }
        }
        return team;
    }

    public static List<Team> toTeamList(String cacheData) {
        List<Team> datas = new ArrayList<Team>();
        if (!StringUtils.isEmpty(cacheData)) {
            String[] teams = cacheData.split("<->");
            if (teams != null) {
                for (int i = 0; i < teams.length; i++) {
                    datas.add(toTeam(teams[i]));
                }
            }
        }
        return datas;
    }

}
