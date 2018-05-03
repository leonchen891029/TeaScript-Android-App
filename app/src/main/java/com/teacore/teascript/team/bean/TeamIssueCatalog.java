package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**项目任务分组实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */
@XStreamAlias("catalog")
public class TeamIssueCatalog extends Entity{

    @XStreamAlias("title")
    private String title;

    // 1:归档;0:未归档
    @XStreamAlias("archive")
    private int archive;

    @XStreamAlias("description")
    private String description;

    // 未完成的任务数量
    @XStreamAlias("openedIssueCount")
    private int openedIssueCount;

    // 已完成的任务数量
    @XStreamAlias("closedIssueCount")
    private int closedIssueCount;

    // 所有任务数量
    @XStreamAlias("allIssueCount")
    private int allIssueCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOpenedIssueCount() {
        return openedIssueCount;
    }

    public void setOpenedIssueCount(int openedIssueCount) {
        this.openedIssueCount = openedIssueCount;
    }

    public int getClosedIssueCount() {
        return closedIssueCount;
    }

    public void setClosedIssueCount(int closedIssueCount) {
        this.closedIssueCount = closedIssueCount;
    }

    public int getAllIssueCount() {
        return allIssueCount;
    }

    public void setAllIssueCount(int allIssueCount) {
        this.allIssueCount = allIssueCount;
    }

}
