package com.teacore.teascript.network.remote;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.EventApplyData;
import com.teacore.teascript.bean.NewsList;
import com.teacore.teascript.bean.Report;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.network.TSHttpClient;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * TeaScript网络Api
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-16
 */
public class TeaScriptApi {

    //登录操作
    public static void login(String email,String password,AsyncHttpResponseHandler handler){

        //登录参数
        RequestParams params=new RequestParams();
        params.put("email",email);
        params.put("password",password);
        //登录的partUrl
        String partUrl="api/login";

        TSHttpClient.get(partUrl,params,handler);
    }

    /*
    *获取新闻列表
    * @param catalog 类别
    * @param pageIndex 页码
     */
    public static void getNewsList(int catalog,int pageIndex,AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        if (catalog == NewsList.CATALOG_WEEK) {
            params.put("show", "week");
        } else if (catalog == NewsList.CATALOG_MONTH) {
            params.put("show", "month");
        }

        String partUrl = "api/news_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    /**获取文章列表
    *@param catalog 1新闻 2帖子 3动弹 4消息中心
    */
    public static void getPostList(int catalog, int pageIndex,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/post_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //通过tag标签获取文章列表
    public static void getPostListByTag(String tag, int pageIndex,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("tag", tag);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/post_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    /*
    获取TeaTime列表
    如果catalog>0,代表请求的是用户自身的Teatime
    此时catalog就等于uid
     */
    public static void getTeatimeList(int catalog, int pageIndex,
                                    AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();

        params.put("catalog", catalog);

        params.put("pageIndex", pageIndex);

        String partUrl="api/teatime";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取TeaTimeTopic列表
    public static void getTeatimeTopicList(int pageIndex, String topic,
                                         AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("pageIndex", pageIndex);
        params.put("title", topic);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/teatime_topic_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取TeaTimeLike列表
    public static void getTeatimeLikeList(int pageIndex, AsyncHttpResponseHandler handler) {

        RequestParams params=new RequestParams();
        params.put("pageIndex",pageIndex);
        params.put("pageSize",AppContext.PAGE_SIZE);

        String partUrl="api/my_teatime_like_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    public static void getTeatimeLikeList(int teatimeId, int pageIndex,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teatimeId", teatimeId);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/teatime_like_list";

        TSHttpClient.get(partUrl, params, handler);

    }

    //喜欢该Teatime
    public static void pubLikeTeatime(int teatimeId, long authorId,
                                    AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teatimeId", teatimeId);
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("ownerOfTeatime", authorId);

        String partUrl="api/teatime_like";

        TSHttpClient.post(partUrl, params, handler);
    }

    //不喜欢该Teatime
    public static void pubUnlikeTeatime(int teatimeId, long authorId,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teatimeId", teatimeId);
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("ownerOfTeatime", authorId);

        String partUrl="api/teatime_unlike";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取动态列表
    public static void getActiveList(int uid, int catalog, int pageIndex,
                                     AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("catalog", catalog);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/active_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取朋友列表
    public static void getFriendList(int uid, int relation, int pageIndex,
                                     AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("relation", relation);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/friend_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取所有关注好友列表
    public static void getAllFriendsList(int uid, int relation, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("relation", relation);
        params.put("all", 1);

        String partUrl="api/all_friends_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    /**
     *  获取用户收藏
     *  @param type 0:全部收藏 1:软件 2:话题 3:博客 4:新闻 5：代码
    */
    public static void getFavoriteList(int uid, int type, int pageIndex,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("type", type);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/favorite_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取软件分类列表
    public static void getSoftwareCatalogList(int tag,
                                              AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("tag", tag);

        String partUrl="api/software_catalog_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取软件标签列表
    public static void getSoftwareTagList(int searchTag, int pageIndex,
                                          AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("searchTag", searchTag);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/software_tag_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取软件列表
    public static void getSoftwareList(String searchTag, int pageIndex,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("searchTag", searchTag);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/software_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取评论列表 这里是comment id不是comment uid
    public static void getCommentList(int id, int catalog, int pageIndex,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("id", id);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);
        params.put("clientType", "android");

        String partUrl="api/comment_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取博客的评论列表
    public static void getBlogCommentList(int id, int pageIndex,
                                          AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/blog_comment_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取聊天信息的列表
    public static void getChatMessageList(int friendId, int pageIndex, AsyncHttpResponseHandler
            handler) {
        RequestParams params = new RequestParams();
        params.put("friendId", friendId);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/chat_message_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取特定用户信息
    public static void getUserInformation(int uid,int hisUid,int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("hisUid",hisUid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/user_information";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取用户的博客列表
    public static void getUserBlogList(int authorUid, final String authorName,
                                       final int uid, final int pageIndex,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("authorUid", authorUid);
        params.put("authorName",authorName);
        params.put("uid",uid);
        params.put("pageIndex",pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/user_blog_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //更新关系
    public static void updateRelation(long uid, long hisUid, int newRelation,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("hisUid", hisUid);
        params.put("newRelation", newRelation);

        String partUrl="api/user_update_relation";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取用户自身的信息
    public static void getMyInformation(int uid,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);

        String partUrl="api/my_information";

        TSHttpClient.get(partUrl, params, handler);
    }

    //请求新闻详情
    public static void getNewsDetail(long id, String type, AsyncHttpResponseHandler handler) {
        if (id <= 0) return;
        RequestParams params = new RequestParams();
        params.put("id", id);

        String partUrl="api/" + type;

        TSHttpClient.get(partUrl, params, handler);
    }



    //获取软件详情
    public static void getSoftwareDetail(String identity,
                                         AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("identity",
                identity);

        String partUrl="api/software_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //通过id获取软件详情
    public static void getSoftwareDetail(int id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("id",
                id);

        String partUrl="api/software_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取文章详情
    public static void getPostDetail(int id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("id", id);

        String partUrl="api/post_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取Teatime详情
    public static void getTeatimeDetail(int id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("id", id);

        String partUrl="api/teatime_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    /**用户针对某个新闻，帖子，动弹，消息发表评论的接口，参数通过POST方式提交
    *@param content 发表的评论内容
    *@param isPostToMyZone 是否转发到我的空间 0不转发 1转发 (只对某条动弹进行评论时有效)
     */
    public static void pubComment(int catalog, long id, int uid,
                                     String content, int isPostToMyZone, AsyncHttpResponseHandler
                                             handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("id", id);
        params.put("uid", uid);
        params.put("content", content);
        params.put("isPostToMyZone", isPostToMyZone);

        String partUrl="api/pub_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //回复评论
    public static void replyComment(int id, int catalog, int replyId,
                                    int authorUid, int uid, String content,
                                    AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("id", id);
        params.put("uid", uid);
        params.put("content", content);
        params.put("replyId", replyId);
        params.put("authorUid", authorUid);

        String partUrl="api/reply_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //发布博客评论
    public static void pubBlogComment(long blogId, int uid, String content,
                                         AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("blogId", blogId);
        params.put("uid", uid);
        params.put("content", content);

        String partUrl="api/pub_blog_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //回复博客评论
    public static void replyBlogComment(long blogId,long uid,String content,
                                        long replyId,long authorId,AsyncHttpResponseHandler
                                                handler) {
        RequestParams params = new RequestParams();
        params.put("blogId", blogId);
        params.put("uid",uid);
        params.put("content", content);
        params.put("reply_id", replyId);
        params.put("author_id", authorId);

        String partUrl="api/reply_blog_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //发布Teatime
    public static void pubTeatime(Teatime teatime, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", teatime.getAuthorId());
        params.put("content", teatime.getBody());

        if (!TextUtils.isEmpty(teatime.getImageFilePath())) {
            try {
                params.put("img", new File(teatime.getImageFilePath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(teatime.getAudioPath())) {
            try {
                params.put("audio", new File(teatime.getAudioPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String partUrl="api/pub_teatime";

        TSHttpClient.post(partUrl, params, handler);
    }

    //发布SoftWareTeatime
    public static void pubSoftWareTeatime(Teatime teatime, int softId,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", teatime.getAuthorId());
        params.put("content", teatime.getBody());
        params.put("project", softId);

        String partUrl="api/pub_software_Teatime";

        TSHttpClient.post(partUrl, params, handler);
    }

    //删除Teatime
    public static void deleteTeatime(long uid, int TeatimeId,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("TeatimeId", TeatimeId);

        String partUrl="api/delete_teamtime";

        TSHttpClient.post(partUrl, params, handler);
    }

    //删除评论
    public static void deleteComment(int id, int catalog, int replyid,
                                     int authorid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("catalog", catalog);
        params.put("replyid", replyid);
        params.put("authorid", authorid);

        String partUrl="api/delete_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //删除blog
    public static void deleteBlog(int uid, int authorUid, int id,
                                  AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("authorUid", authorUid);
        params.put("id", id);

        String partUrl="api/delete_user_blog";

        TSHttpClient.post(partUrl, params, handler);
    }

    //删除博客评论
    public static void deleteBlogComment(int uid, int blogId, int replyid,
                                         int authorUid, int ownerUid, AsyncHttpResponseHandler
                                                 handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("blogId", blogId);
        params.put("replyId", replyid);
        params.put("authorId", authorUid);
        params.put("ownerUid", ownerUid);

        String partUrl="api/delete_blog_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    /*
    *添加收藏
    *objId 比如新闻ID 或者问答ID 或者是动弹ID
    *type 1：软件 2：话题 3：博客 4：新闻 5：代码
     */
    public static void addFavorite(int uid, long objId, int type,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("objId", objId);
        params.put("type", type);

        String partUrl="api/add_favorite";

        TSHttpClient.post(partUrl, params, handler);
    }

    //取消收藏
    public static void delFavorite(int uid, long objId, int type,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();

        params.put("uid", uid);
        params.put("objId", objId);
        params.put("type", type);

        String partUrl="api/delete_favorite";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取搜索列表
    public static void getSearchList(String catalog, String content,
                                     int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("content", content);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/search_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //发消息
    public static void pubMessage(int uid, int receiverId, String content,
                                     AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("receiver", receiverId);
        params.put("content", content);

        String partUrl="api/pub_message";

        TSHttpClient.post(partUrl, params, handler);
    }

    public static void pubMessage(int uid, String receiverName,
                                  String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("receiverName", receiverName);
        params.put("content", content);

        String partUrl="api/pub_message";

        TSHttpClient.post(partUrl, params, handler);
    }

    //删除消息
    public static void deleteMessage(int uid, int friendId,
                                     AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("friendId", friendId);

        String partUrl="api/delete_message";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取消息列表
    public static void getMessageList(int uid, int pageIndex,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/message_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //更新头像
    public static void updatePortrait(int uid, File portrait,
                                      AsyncHttpResponseHandler handler) throws
            FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("portrait", portrait);

        String partUrl="api/update_portrait";

        TSHttpClient.post(partUrl, params, handler);
    }

    //获取通知消息
    public static void getNotices(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());

        String partUrl="api/user_notice";

        TSHttpClient.get(partUrl, params, handler);
    }

    /**清空通知消息
    *@param type 1:@我的消息 2:未读消息 3:评论个数 4:新粉丝个数
     */
    public static void clearNotice(int uid, int type,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("type", type);

        String partUrl="api/clear_notice";

        TSHttpClient.post(partUrl, params, handler);
    }

    public static void signIn(String url, AsyncHttpResponseHandler handler) {
        TSHttpClient.getDirect(url, handler);
    }

    //获取软件的动态列表
    public static void getSoftActiveList(int softId, int page,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("project", softId);
        params.put("pageIndex", page);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/software_active_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //检查App更新信息
    public static void checkUpdate(AsyncHttpResponseHandler handler) {
        TSHttpClient.get("MobileAppVersion.xml", handler);
    }

    //查找用户
    public static void findUser(String username,
                                AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("name", username);

        String partUrl="api/find_user";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取活动列表
    public static void getEventList(int pageIndex, int uid,
                                    AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("pageIndex", pageIndex);
        params.put("uid", uid);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/event_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取某活动已出席的人员列表
    public static void getEventApplies(int eventId, int pageIndex,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("pageIndex", pageIndex);
        params.put("eventId", eventId);
        params.put("pageSize", AppContext.PAGE_SIZE);

        String partUrl="api/event_attend_users";

        TSHttpClient.get(partUrl, params, handler);
    }

    //举报
    public static void report(Report report, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("objId", report.getObjId());
        params.put("url", report.getUrl());
        params.put("objType", report.getObjType());
        params.put("reason", report.getReason());

        if (report.getOtherReason() != null
                && !StringUtils.isEmpty(report.getOtherReason())) {
            params.put("otherReason", report.getOtherReason());
        }

        String partUrl="api/communityManage/report";

        TSHttpClient.post(partUrl, params, handler);
    }

    //摇一摇，指定请求类型
    public static void shake(int type, AsyncHttpResponseHandler handler) {

        String url = "api/rock_rock";
        if (type > 0) {
            url = url + "/?type=" + type;
        }
        TSHttpClient.get(url, handler);

    }

    //摇一摇，随机数据
    public static void shake(AsyncHttpResponseHandler handler) {
        shake(-1, handler);
    }

    //活动报名
    public static void eventApply(EventApplyData data,
                                  AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("event", data.getEventId());
        params.put("user", data.getUserId());
        params.put("name", data.getName());
        params.put("gender", data.getGender());
        params.put("mobile", data.getPhone());
        params.put("company", data.getCompany());
        params.put("job", data.getJob());

        if (!StringUtils.isEmpty(data.getRemark())) {
            params.put("misc_info", data.getRemark());
        }

        String partUrl="api/event_apply";

        TSHttpClient.post(partUrl, params, handler);
    }

    //上报bug
    private static void reportBug(String data, String report,
                                  AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("app", "1");
        params.put("report", report);
        params.put("msg", data);

        String partUrl="api/user_report_to_admin";

        TSHttpClient.post(partUrl, params, handler);
    }


    //team动态
    public static void teamDynamic(Team team, int page,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", team.getId());
        params.put("pageIndex", page);
        params.put("pageSize", 20);
        params.put("type", "all");

        String partUrl="api/team_active_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取team列表
    public static void teamList(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", AppContext.getInstance().getLoginUid());

        String partUrl="api/team_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取team成员列表
    public static void getTeamMemberList(int teamId,
                                         AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);

        String partUrl="api/team_member_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取team成员个人信息
    public static void getTeamUserInfo(String teamId, String uid,
                                       int pageIndex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("uid", uid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 20);

        String partUrl="api/team_user_information";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取我的任务中进行中，未完成、已完成等状态的数量
    public static void getMyIssueState(String teamId, String uid,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("uId", uid);

        String partUrl="api/team_user_issue_information";

        TSHttpClient.get(partUrl, params,
                handler);
    }

    //获取指定用户的team动态
    public static void getUserDynamic(int teamId, String uid, int pageIndex,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 20);
        params.put("type", "git");
        params.put("uid", uid);

        String partUrl="api/team_active_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //team动态详情
    public static void getDynamicDetail(int activeId, int teamId, int uid,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("uid", uid);
        params.put("activeId", activeId);

        String partUrl="api/team_active_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取指定用户的任务
    public static void getMyIssue(String teamId, String uid, int pageIndex,
                                  String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("uid", uid);
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 20);
        params.put("state", type);
        params.put("projectId", "-1");

        String partUrl="api/team_issue_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取指定周团队周报
    public static void getDiaryFromWhichWeek(int teamId, int year, int week,
                                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("year", year);
        params.put("week", week);

        String partUrl="api/team_diary_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    //删除一个便签
    public static void deleteNoteBook(int id, int uid,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("id", id);

        String partUrl="api/delete_team_notebook";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取用户的便签
    public static void getNoteBook(int uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);

        String partUrl="api/team_notebook_list";

        TSHttpClient.get(partUrl, params, handler);
    }

    // 获取指定周报的详细信息
    public static void getDiaryDetail(int teamId, int diaryId,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("diaryId", diaryId);

        String partUrl="api/team_diary_detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取周报评论列表
    public static void getDiaryComment(int teamId, int diaryId,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("teamId", teamId);
        params.put("diaryId", diaryId);
        params.put("type", "diary");
        params.put("pageIndex", 0);
        params.put("pageSize", "20");

        String partUrl="api/team_diary_comment_list_by_type";

        TSHttpClient.get(partUrl, params, handler);
    }

    //发送周报评论
    public static void pubDiaryComment(int uid, int teamId, int diaryId,
                                   String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teamId", teamId);
        params.put("type", "118");
        params.put("diaryId", diaryId);
        params.put("content", content);

        String partUrl="api/pub_team_diary_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    //客户端扫描二维码登录
    public static void scanQRCodeLogin(String url,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        String uuid = url.substring(url.lastIndexOf("=") + 1);
        params.put("uuid", uuid);

        TSHttpClient.getDirect(url, handler);
    }

    /*
    使用第三方登录
    *@param catalog 类别
    *@param openIdInfo 第三方info
     */
    public static void open_login(String catalog, String openIdInfo, AsyncHttpResponseHandler
            handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("openid_info", openIdInfo);

        String partUrl="api/openid_login";

        TSHttpClient.post(partUrl, params, handler);
    }

    /*
    第三方账户绑定
    *@param catalog 类别(QQ、wechat)
    *@param openIdInfo 第三方info
    *@param userName 用户名
    *@param pwd 密码
     */
    public static void bind_openid(String catalog, String openIdInfo, String userName, String
            pwd, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("openid_info", openIdInfo);
        params.put("username", userName);
        params.put("pwd", pwd);

        String partUrl="api/openid_bind";

        TSHttpClient.post(partUrl, params, handler);
    }

    //第三方账号注册
    public static void register_openid(String catalog, String openIdInfo, AsyncHttpResponseHandler
            handler) {
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("openid_info", openIdInfo);

        String partUrl="api/openid_reg";

        TSHttpClient.post(partUrl, params, handler);
    }

    //意见反馈
    public static void feedback(String content, File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();

        if (file != null && file.exists()) {
            try {
                params.put("file", file);
            } catch (FileNotFoundException e) {
            }
        }
        params.put("uid", AppContext.getInstance().getLoginUid());
        params.put("receiver", "2609904");
        params.put("content", content);

        String partUrl="api/pub_message";


        TSHttpClient.post(partUrl, params, handler);
    }

    //咨询Banner
    public static final int CATALOG_BANNER_NEWS=1;

    //博客Banner
    public static final int CATALOG_BANNER_BLOG=2;

    //活动Banner
    public static final int CATALOG_BANNER_EVENT=2;

    //获取Banner列表
    public static void getBannerList(String pageToken, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();

        params.put("pageToken", pageToken);

        String partUrl="api/banner";

        TSHttpClient.get(partUrl, params, handler);

    }

    /**
     * 请求新闻列表
     * @param pageToken 请求上下页数据令牌
     */
    public static void getNewsList(String pageToken, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }

        String partUrl="api/news";

        TSHttpClient.get(partUrl, params, handler);
    }

    //获取新闻明细 id是新闻的id
    public static void getNewsDetail(long id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams("id", id);

        String partUrl="api/news/detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    public static final String CATALOG_NEWS_DETAIL = "news";
    public static final String CATALOG_TRANSLATE_DETAIL = "translation";
    public static final String CATALOG_SOFTWARE_DETAIL = "software";

    /*
    请求博客列表
    *@param catalog 博客类别
    *@param pageToken 请求上下页数据令牌
     */

    public static void getBlogList(int catalog, String pageToken, AsyncHttpResponseHandler handler) {

        if (catalog <= 0)
            catalog = 1;

        RequestParams params = new RequestParams();

        params.put("catalog", catalog);

        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }

        String partUrl="api/blog";

        TSHttpClient.get(partUrl, params, handler);

    }

    //请求博客详情
    public static void getBlogDetail(long id, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();

        params.put("id", id);

        String partUrl="api/blog/detail";

        TSHttpClient.get(partUrl, params, handler);

    }

    //请求用户自己的博客列表
    public static void getUserBlogList(int catalog, String pageToken, int userId, AsyncHttpResponseHandler handler) {

        if (catalog <= 0)
            catalog = 1;

        RequestParams params = new RequestParams();

        params.put("catalog", catalog);

        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }

        if (userId <= 0) return;

        params.put("userId", userId);

        String partUrl="api/user_blog";

        TSHttpClient.get(partUrl, params, handler);
    }



    //请求活动列表
    public static void getEventList(String pageToken, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();

        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }

        String partUrl="api/event";

        TSHttpClient.get(partUrl, params, handler);
    }

    //请求活动详情
    public static void getEventDetail(long id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);

        String partUrl="api/event/detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    //更改收藏状态
    public static void getFavReverse(long id, int type, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();

        params.put("id", id);

        params.put("type", type);

        String partUrl="api/fav_reverse";

        TSHttpClient.get(partUrl, params, handler);
    }

    //更改关注状态(关注/取消关注)
    public static void getUserRelationReverse(long id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);

        String partUrl="api/user_relation_reverse";

        TSHttpClient.get(partUrl, params, handler);
    }

    //请求问答列表
    public static void getQuestionList(int type, String pageToken, AsyncHttpResponseHandler handler) {
        if (type <= 0)
            type = 1;

        RequestParams params = new RequestParams();

        params.put("type", type);

        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }

        String partUrl="api/question";

        TSHttpClient.get(partUrl, params, handler);
    }

    //请求问答详情
    public static void getQuestionDetail(long id, AsyncHttpResponseHandler handler) {
        if (id <= 0) return;
        RequestParams params = new RequestParams();
        params.put("id", id);

        String partUrl="api/question/detail";

        TSHttpClient.get(partUrl, params, handler);
    }

    public static final int COMMENT_SOFT = 1; // 软件推荐-不支持
    public static final int COMMENT_QUESTION = 2; // 讨论区帖子
    public static final int COMMENT_BLOG = 3; // 博客
    public static final int COMMENT_TRANSLATION = 4; // 翻译文章
    public static final int COMMENT_EVENT = 5; // 活动类型
    public static final int COMMENT_NEWS = 6; // 资讯类型

    //请求评论详情
    public static void getComment(long id, long authorId, int type, TextHttpResponseHandler handler) {
        if (id <= 0) return;
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("authorId", authorId);
        params.put("type", type);

        String partUrl="api/comment";

        TSHttpClient.get(partUrl, params, handler);
    }

    /*
    请求评论列表
    *@param sourceId 目标ID，该sourceId为资讯、博客、问答等的Id
    *@param type 评论类型
    *@param parts 请求的数据节点 parts="refer,reply"
    *@param pageToken 请求上下文数据令牌
     */

    public static void getCommentList(long sourceId, int type, String parts, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        params.put("type", type);
        if (!TextUtils.isEmpty(parts)) {
            params.put("parts", parts);
        }
        if (!TextUtils.isEmpty(pageToken)) {
            params.put("pageToken", pageToken);
        }

        String partUrl="api/comment";

        TSHttpClient.get(partUrl, params, handler);
    }

    /*
    发表评论
    *@param sid 文章id
    *@param referId 引用的评论id
    *@param replyId 回复的评论id
    *@param oid 引用、回复的发布者的id
    *@param type 评论类型 1:软件推荐, 2:问答帖子, 3:博客, 4:翻译文章, 5:活动, 6:资讯
    *@param content 内容
     */
    public static void pubComment(long sid, long referId, long replyId, long oid,
                                      int type, String content, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sid);
        params.put("type", type);
        params.put("content", content);

        if (referId > 0)
            params.put("referId", referId);
        if (replyId > 0)
            params.put("replyId", replyId);
        if (oid > 0)
            params.put("reAuthorId", oid);

        String partUrl="api/pub_comment";

        TSHttpClient.post(partUrl, params, handler);
    }

    // 发表资讯评论
    public static void pubNewsComment(long sid, long commentId, long commentAuthorId, String comment, TextHttpResponseHandler handler) {

        if (commentId == 0 || commentId == sid) {
            commentId = 0;
            commentAuthorId = 0;
        }

        pubComment(sid, 0, commentId, commentAuthorId, 6, comment, handler);
    }

    //发布博客评论
    public static void pubBlogComment(long sid, long commentId, long commentAuthorId, String comment, TextHttpResponseHandler handler) {
        if (commentId == 0 || commentId == sid) {
            commentId = 0;
            commentAuthorId = 0;
        }

        pubComment(sid, 0, commentId, commentAuthorId, 3, comment, handler);
    }

    //发表问答评论
    public static void publishQuestionComment(long sid, long commentId, long commentAuthorId, String comment, TextHttpResponseHandler handler) {

        if (commentId == 0 || commentId == sid) {
            commentId = 0;
            commentAuthorId = 0;
        }
        pubComment(sid, 0, commentId, commentAuthorId, 2, comment, handler);
    }

    //发表翻译评论
    public static void pubTranslateComment(long sid, long commentId, long commentAuthorId, String comment, TextHttpResponseHandler handler) {

        if (commentId == 0 || commentId == sid) {
            commentId = 0;
            commentAuthorId = 0;
        }
        pubComment(sid, 0, commentId, commentAuthorId, 4, comment, handler);
    }



    /*
     问答的回答, 顶\踩
     *@param sid source id 问答的id
     *@param 回答的id
     *@param 操作类型 0:取消, 1:顶, 2:踩
     */
    public static void questionVote(long sid, long cid, int opt, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sid);
        params.put("commentId", cid);
        params.put("voteOpt", opt);

        String partUrl="api/question_vote";

        TSHttpClient.post(partUrl, params, handler);
    }


}
