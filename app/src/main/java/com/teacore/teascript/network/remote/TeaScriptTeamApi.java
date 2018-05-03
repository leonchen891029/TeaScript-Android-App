package com.teacore.teascript.network.remote;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.network.TSHttpClient;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.bean.TeamProject;

import java.io.File;
import java.io.FileNotFoundException;

/**TeaScript Team api
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-7
 */

public class TeaScriptTeamApi {

    //获取团队项目列表
    public static void getTeamProjectList(int teamId, AsyncHttpResponseHandler handler){
        RequestParams params=new RequestParams();
        params.put("teamId",teamId);

        String partUrl="api/team_project_list";

        TSHttpClient.get(partUrl,params,handler);

    }

    //通过动态Id获取team评论列表
    public static void getTeamCommentList(int teamId, int activeId,
                                          int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("id", activeId);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 20);

        String partUrl="api/team_comment_list_by_activeid";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取团队指定项目的成员列表(包括管理者以及开发者)
    public static void getTeamProjectMemberList(int teamId, TeamProject teamProject, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("projectId", teamProject.getGit().getId());

        String source = teamProject.getSource();
        if (source != null && !TextUtils.isEmpty(source)) {
            params.put("source", source);
        }

        String partUrl="api/team_project_member_list";

        TSHttpClient.get(partUrl, params,
                handler);
    }

    //获取项目的动态列表
    public static void getTeamProjectActiveList(int teamId,
                                                TeamProject project, String type, int page,
                                                AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("projectId", project.getGit().getId());
        if (!TextUtils.isEmpty(project.getSource())) {
            params.put("source", project.getSource());
        }
        params.put("type", type);
        params.put("pageIndex", page);

        String partUrl="api/team_project_active_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取某项目的任务列表
    public static void getTeamProjectIssueList(int uId, int teamId,
                                               int projectId, String source, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uId);
        params.put("teamId", teamId);
        params.put("projectId", projectId);
        params.put("source", source);

        String partUrl="api/team_project_issue_list";

        TSHttpClient.get(partUrl, params,
                handler);
    }

    /**
     * 获取某项目下指定目录下的任务列表
     * @param projectId 项目id(-1获取非项目任务列表, 0获取所有任务列表)
     * @param catalogId 任务列表的的目录id
     * @param source "Team@TEASCRPT"(default),"Git@TEASCRIPT","GitHub",如果指定了projectid的值，这个值就是必须的
     * @param uid 如果指定该值，则获取该id用户相关的任务
     * @param state "all"(default),"opened","closed","outdate"
     * @param scope "tome"(default,指派给我的任务),"meto"(我指派的任务)
     */
    public static void getTeamIssueList(int teamId, int projectId,
                                        int catalogId, String source, int uid, String state, String scope,
                                        int pageIndex, int pageSize, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("projectId", projectId);
        params.put("catalogId", catalogId);
        params.put("source", source);
        params.put("uid", uid);
        params.put("state", state);
        params.put("scope", scope);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);

        String partUrl="api/team_project_issue_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    /***
     * 改变一个任务的状态
     * @param target 修改的属性（"state" : 状态, "assign" 指派人, "deadline" : 截止日期）
     */
    public static void changeIssueState(int teamId, TeamIssue issue,
                                        String target, AsyncHttpResponseHandler handler) {
        if (issue == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("teamId", teamId);
        params.put("target", target);
        params.put("issueId", issue.getId());
        if (target.equals("state")) {
            params.put("state", issue.getState());
        } else if (target.equals("assign")) {
            params.put("assignee", issue.getToUser().getId());
        } else if (target.equals("deadline")) {
            params.put("deadline", issue.getDeadlineTime());
        }

        String partUrl="api/update_team_issue";

        TSHttpClient.post(partUrl,params, handler);
    }

    //发布新的团队任务
    public static void pubTeamNewIssue(RequestParams params,
                                       AsyncHttpResponseHandler handler) {

        String partUrl="api/pub_team_issue";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取团队的讨论区列表
    public static void getTeamDiscussList(String type, int teamId, int uid,
                                          int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("teamId", teamId);
        params.put("uid", uid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/team_discuss_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取讨论帖详情
    public static void getTeamDiscussDetail(int teamId, int discussId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("discussId", discussId);

        String partUrl="api/team_discuss_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //对一个讨论帖进行评论
    public static void pubTeamDiscussReply(int uid, int teamId, int discussId,
                                           String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teamId", teamId);
        params.put("discussId", discussId);
        params.put("content", content);

        String partUrl="api/team_discuss_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    /*对一个Teatime进行回复
    *@param type： 普通动态-110，分享内容-114， 周报-118
     */
    public static void pubTeamTeatimeComment(int teamId, int type, long teatimeId,
                                         String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("type", type);
        params.put("teamId", teamId);
        params.put("teatimeId", teatimeId);
        params.put("content", content);

        String partUrl="api/team_teatime_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取团队任务详情
    public static void getTeamIssueDetail(int teamId, int issueId,
                                          AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("issueId", issueId);

        String partUrl="api/team_issue_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取团队的周报列表
    public static void getTeamDiaryList(int uid, int teamId, int year,
                                        int week, int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teamId", teamId);
        params.put("year", year);
        params.put("week", week);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/team_diary_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //任务、周报、讨论的回复列表
    public static void getTeamReplyList(int teamId, int id, String type,
                                        int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("id", id);
        params.put("type", type);
        params.put("pageIndex", pageIndex);

        String partUrl="api/team_comment_list_by_type";

        TSHttpClient
                .get(partUrl, params, handler);

    }

    //发表一个新的团队动态
    public static void pubTeamNewActive(int teamId, String content, File img,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("msg", content);
        params.put("appId", 3);

        if (img != null) {
            try {
                params.put("img", img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String partUrl="api/pub_team_active";

        TSHttpClient.post(partUrl, params, handler);
    }

    //更新子任务(状态或者标题)
    public static void updateChildIssue(int teamId, String target,
                                        TeamIssue childIssue, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("teamId", teamId);
        params.put("childIssueId", childIssue.getId());
        params.put("target", target);
        if (target.equals("state")) {
            params.put("state", childIssue.getState());
        } else {
            params.put("title", childIssue.getTitle());
        }

        String partUrl="api/update_team_child_issue";

        TSHttpClient.post(partUrl, params,
                handler);
    }

    //发表任务评论
    public static void pubTeamIssueComment(int teamId, int issueId,
                                         String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("teamId", teamId);
        params.put("content", content);
        params.put("issueId", issueId);

        String partUrl="api/team_issue_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

}
