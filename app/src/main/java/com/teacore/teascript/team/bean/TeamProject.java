package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**团队项目实体类
 * @author陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */
@XStreamAlias("project")
public class TeamProject extends Entity {

    public final static String GITOSC = "Git@TEASCRIPT";
    public final static String GITHUB = "GitHub";

    @XStreamAlias("source")
    private String source;

    @XStreamAlias("team")
    private String team;

    @XStreamAlias("gitpush")
    private boolean gitpush;

    @XStreamAlias("git")
    private TeamGit git;

    @XStreamAlias("issue")
    private Issue issue;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isGitpush() {
        return gitpush;
    }

    public void setGitpush(boolean gitpush) {
        this.gitpush = gitpush;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public TeamGit getGit() {
        return git;
    }

    public void setGit(TeamGit git) {
        this.git = git;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @XStreamAlias("issue")
    public class Issue extends Entity {
        @XStreamAlias("opened")
        private int opened;
        @XStreamAlias("all")
        private int all;

        public int getOpened() {
            return opened;
        }

        public void setOpened(int opened) {
            this.opened = opened;
        }

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }

    }

}
