
# TeaScript Android [客户端](https://github.com/leonchen891029/TeaScript-Android-App)

## TeaScript Android 客户端简介
TeaScript是一门全新的动态脚本语言，该App旨在构建一个介绍该门语言的社区
![enter image description here](https://github.com/leonchen891029/TeaScriptImage/raw/master/work/launcher.png)

-------------------------------------------------------------------------------------------------------------

## 开发环境 
Android Studio 

## 项目简述

-------------------------------------------------------------------------------------------------------------

<font size=5>**1.底部导航**</font>

<font size=4>主界面底部TAB导航采用FragmentTabHost点击底部按钮切换Fragment。中间的快捷操作按钮使用的是自定义Dialog </font>

![fragment_tab_host](https://github.com/leonchen891029/TeaScriptImage/raw/master/work/fragment_tab_host.jpg)

-------------------------------------------------------------------------------------------------------------
<font size=5>**2.一级界面**</font>

<font size=4>包括综合，TeaScript,快捷操作按钮，Teatime，用户五个模块，其中综合,TeaScript,Teatime采用ViewPagerFragment根据不同页面显示不同信息</font>

-------------------------------------------------------------------------------------------------------------
<font size=4>**(1)综合界面**</font>

<font size=4>分为四个模块，资讯，博客，问答，活动</font>
<font size=4>资讯页面:提供TeaScript语言的相关资讯</font>
<font size=4>博客页面:提供最热和最新的用户博客</font>
<font size=4>问答页面:提供相关的问题解答</font>
<font size=4>活动页面:提供相关的活动信息</font>

<font size=4>其中资讯和活动中有HeaderView进行轮播展示，问答界面上方有一个GridView用来细分问答的种类</font>

<div align="left">
<img src="https://github.com/leonchen891029/TeaScriptImage/raw/master/work/news_list1.png"  >
<img src="https://github.com/leonchen891029/TeaScriptImage/raw/master/work/blog_list.png"  >
<img src="https://github.com/leonchen891029/TeaScriptImage/raw/master/work/question_list.png" >
<img src="https://github.com/leonchen891029/TeaScriptImage/raw/master/work/event_list.png"  ></div>

 ------------------------------------------------------------------------------------------------------------
<font size=4>**(2)TeaScript**</font>

<font size=4>提供语言指南，IDE等相关内容的下载</font>

![enter image description here](https://github.com/leonchen891029/TeaScriptImage/raw/master/work/teascript.png)

-------------------------------------------------------------------------------------------------------------

<font size=4>**(3)快速操作按钮**</font>

<font size=4>提供发布文字，语音，二维码扫描，找人等服务的快捷按钮</font>

![enter image description here](https://github.com/leonchen891029/TeaScriptImage/raw/master/work/quick_option.png)

-------------------------------------------------------------------------------------------------------------

<font size=4>**(4)Teatime**</font>

<font size=4>提供给用户一个可以进行自由闲聊的模块</font>

![enter image description here](https://github.com/leonchen891029/TeaScriptImage/raw/master/work/teatime.png)

-------------------------------------------------------------------------------------------------------------

<font size=4>**(5)用户信息**</font>

<font size=4>用户个人信息模块</font>

![enter image description here](https://github.com/leonchen891029/TeaScriptImage/raw/master/work/myinformation.png)

-------------------------------------------------------------------------------------------------------------

<font size=5>**3.详情界面**</font>

通过一个DetailActivity加载不同的的DetailFragment实现，而Fragment中通过WebView直接loadData()加载一段html数据并显示

<div align="center">
<img src="https://github.com/leonchen891029/TeaScriptImage/raw/master/work/news_detail1.png"  >
<img src="https://github.com/leonchen891029/TeaScriptImage/raw/master/work/blog_detail.png"  ></div>

-------------------------------------------------------------------------------------------------------------

## 项目文件介绍

---------------------------------------------------------
### adapter包(适配器包)
ActiveAdapter.java 动态适配器 <br>
ChatMessageAdapter.java 聊天信息适配器 <br>
CommentAdapter.java 评论适配器 <br>
EventAdapter.java  活动适配器<br>
EventApplyAdapter.java 活动报名者适配器<br>
FindUserAdapter.java 找人适配器<br>
FriendAdapter.java 好友适配器<br>
MessageAdapter.java 留言适配器<br>
PostAdapter.java 帖子适配器<br>
SearchFriendAdapter.java 搜索好友适配器 <br>
SearchResultAdapter.java 搜索结果适配器 <br>
SelectFriendAdapter.java 选择好友适配器 <br>
SoftwareAdapter.java 软件介绍适配器<br>
SoftwareCatalogAdapter.java 软件分类适配器<br>
TeatimeAdapter.java Teatime适配器<br>
TeatimeLikeAdapter.java 被点赞的Teatime适配器<br>
UserFavoriteAdapter.java 用户收藏适配器<br>
ViewPagerFragmentAdapter.java ViewPagerFragment适配器<br>
ViewPagerInfo.java ViewPager信息类 <br>

---------------------------------------------------------
### app包(全局类包)
AppConfig.java 应用程序配置类<br>
AppContext.java 应用程序类<br>
AppManager.java activity管理类<br>
AppStart.java 应用程序启动界面<br>
AppWelcome.java 第一次启动应用时的app介绍界面<br>

---------------------------------------------------------
### base包(基类包)
BaseApplication.java Application类的基类<br>
BaseActivity.java Activity类的基类<br>
BaseListAdapter.java 列表Adapter的基类<br>
BaseFragment.java Fragment类的基类<br>
BaseDetailFragment.java 详情Fragment的基类<br>
BaseListFragment.java 列表Fragment的基类<br>
BaseHeaderListFragment.java 带有HeaderView的列表Fragment基类<br>
BaseViewPagerFragment.java 具有导航条的Fragment基类<br>

---------------------------------------------------------
### bean包(数据bean包)
Base.java Entity的基类<br>
Entity.java 实体类的基类<br>
EntityList.java 实体列表类需要实现的接口<br>
Active.java 动态实体类<br>
ActiveList.java 动态列表实体类<br>
Android.java Android版本信息实体类<br>
Banner.java Banner实体类<br>
Blog.java 博客实体类<br>
BlogDetail.java 博客实体类的包装类<br>
BlogCommentList.java 博客评论列表实体类<br>
ChatMessage.java 聊天记录实体类<br>
ChatMessageList.java 聊天记录列表实体类<br>
Comment.java 评论实体类<br>
CommentList.java 评论列表实体类<br>
Constants.java 常量bean类<br>
Event.java 活动实体类<br>
EventList.java 活动列表实体类<br>
EventApply.java 活动申请者实体类<br>
EventAppliesList.java 活动申请者列表实体类<br>
EventApplyData.java 活动报名实体类<br>
EventRemark.java 活动备注实体类<br>
Favorite.java 收藏实体类<br>
FavoriteList.java 收藏列表实体类<br>
FindUsersList.java 找寻用户的列表实体类<br>
Friend.java 好友实体类<br>
FriendsList.java 好友列表实体类<br>
LoginUser.java 登录用户实体类<br>
MessageDetail.java 聊天详情实体类<br>
MyInformation.java 我的资料实体类<br>
News.java 新闻实体类<br>
NewsDetail.java 新闻实体类的包装类<br>
NewsList.java 新闻列表实体类 <br>
Notebook.java 便签实体类 <br>
Notice.java 通知实体类 <br>
NoticeDetail.java 通知实体类的包装类<br>
Post.java 帖子实体类<br>
PostDetail.java 帖子实体类的包装类<br>
Report.java 举报实体类<br>
ResultData.java 网络数据结果实体类<br>
Result.java 结果实体类<br>
SearchResult.java 搜索结果实体类<br>
SearchResultList.java 搜索结果列表实体类<br>
ShakeObject.java 摇一摇返回的bean类<br>
Software.java 软件实体类<br>
SoftwareCatalogList.java 软件分类列表实体类<br>
SoftwareDetail.java 软件实体类的包装类<br>
SoftwareIntro.java 软件简介实体类<br>
SoftwareIntroList.java 软件简介列表实体类<br>
Teatime.java Teatime实体类<br>
TeatimeDetail.java Teatime实体类的包装类<br>
TeatimeLike.java Teatime点赞实体类<br>
TeatimeLikeUser.java 点赞Teatime用户的实体类<br>
Update.java 更新实体类<br>
UpdateDetail.java 更新实体类的包装类<br>
User.java 用户实体类<br>
UserInformation.java 用户信息实体类<br>

---------------------------------------------------------
### broadcast包(广播类包)
NoticeReceiver.java 开启消息服务类的广播类

---------------------------------------------------------
### cache包(缓存包)
CacheManager.java 缓存管理类<br>
DataCleanManager.java 数据清理类<br>
DiskLruCache.java 硬盘缓存类<br>
DiskLruCacheUtils.java 硬盘缓存工具类<br>

---------------------------------------------------------
### database(数据库包)
DatabaseHelper.java 数据库帮助类<br>
NotebookDatabase.java 便签数据库<br>

---------------------------------------------------------
### interfaces包(接口包)
BaseActivityInterface.java Activity需要实现的接口<br>
BaseFragmentInterface.java Fragment需要实现的接口<br>
OnDownloadResultListener.java 下载服务回调接口<br>
OnTabReselectListener.java tab重新选择的监听器<br>
OnWebViewImageListener.java WebView上面的图片监听器

---------------------------------------------------------
### module包(模块包)

#### 1.back包(通过BackActivity加载不同Fragment)
BackActivity.java 加载不同Fragment的Activity<br>
BackFragmentEnum.java 管理不同的Fragment的枚举类<br>

##### currencyfragment包(其他特定模块所使用的Fragment并未列入其中)
ActiveListFragment.java 动态ListFragment<br>
BrowerFragment.java 浏览器Fragment<br>
ChatMessagerListFragment.java 聊天记录ListFragment<br>
CommentListFragment.java 评论ListFragment<br>
EventApplyListFragment.java 活动报名者ListFragment<br>
PostTagListFragment.java 按标签分类的帖子ListFragment<br>
SoftwareTeatimesListFragment.java 软件TeatimeListFragment<br>
UserCenterFragment.java 用户中心Fragment<br>
VoiceTeatimeFragment.java 语音Teatime发布Fragment<br>

#### 2.detail包(详情包)
DetailActivity.java 加载不同DetailFragment的Activity
##### fragment包
BlogDetailFragment.java 博客详情Fragment<br>
EventDetailFragment.java 活动详情Fragment<br>
NewsDetailFragment.java 新闻详情Fragment<br>
PostDetailFragment.java 帖子详情Fragment<br>
SoftwareDetailFragment.java 软件详情Fragment<br>
TeatimeDetailFragment.java Teatime详情Fragment<br>
ToolBarFragment.java 导航栏Fragment<br>

#### 3.general包(综合界面包)
##### (1)activity包
PreviewImageActivity.java 图片预览Activity<br>

##### (2)adapter包
ViewHolder.java 综合界面通用的ViewHolder<br>
###### generaladapter包
BlogAdapter.java 综合界面下的博客适配器<br>
EventAdapter.java 综合界面下的活动适配器<br>
NewsAdapter.java 综合界面下的新闻适配器<br>
QuestionAdapter.java 综合界面下的问答适配器<br>
QuestionGridAdapter.java 综合界面下问答界面四个分类按钮适配器<br>
###### teatimeadapter包
TeatimeCommentAdapter.java teatime评论RecyclerAdapter<br>
TeatimeLikeUserAdapter.java teatime点赞用户的RecylerAdapter<br>

##### (3)app包
AppOperator.java App异步线程池类<br>

##### (4)base包
###### baseactivity包
BaseActivity.java 综合界面下使用的activity基类<br>
BaseBackActivity.java XXXDetailActivity基类<br>
###### baseadapter包
BaseListAdapter.java 综合界面下使用的BaseListAdapter<br>
BaseRecyclerAdapter.java RecylerView.Adapter基类<br>
###### basebean包
PageBean.java 页面bean类<br>
ResultBean.java 返回结果bean类<br>
SoftwareDetail.java 软件详情bean类<br>
###### basefragment包
BaseFragment.java 综合界面下的Fragment基类<br>
BaseListFragment.java 综合界面下的BaseListFragment基类<br>
BaseGeneralListFragment.java GeneralListFragment基类<br>
BaseRecyclerViewFragment.java 基于RecyclerView的ListFragment基类<br>

##### (5)bean包
About.java 相关推荐的bean类<br>
Blog.java  综合界面下的博客bean类<br>
BlogDetail.java 综合界面下的博客详情bean类<br>
Comment.java 综合界面下的评论bean类<br>
CommentQ.java 适用于问答模块的评论bean类<br>
Event.java 综合界面下的活动bean类<br>
EventDetail.java 综合界面下的活动详情bean类<br>
News.java 综合界面下的新闻bean类<br>
NewsDetail.java 综合界面下的新闻详情bean类<br>
Question.java 综合界面下的问答bean类<br>
QuestionDetail.java 综合界面下的问答详情bean类<br>
Software.java 综合界面下的软件bean类<br>
TranslationDetail.java 翻译文章的bean类<br>
UserRelation.java 用户关系的bean类<br>

##### (6)behavior包
KeyboardActionDelegation.java 供KeyboardInputDelegation.java使用的操作类<br>
KeyboardInputDelegation.java 底部键盘输入的操作类<br>
ScrollingAutoHideBehavior.java 滚动时隐藏的behavior<br>

##### (7)comment包
CommentQActivity.java 问答评论Activity<br>
CommentQView.java     问答模块使用的CommentsView<br>
CommentsActivity.java 评论Activity<br>
CommentsView.java 评论界面<br>
CommentUtils.java 评论帮助类<br>
OnCommentClickListener.java 评论点击监听器<br>
QuestionCommentDetailActivity.java 问答评论详情Activity<br>

##### (8)detail包
###### activity包
DetailActivity.java  详情Activity的基类<br>
BlogDetailActivity.java 博客详情Activity<br>
EventDetailActivity.java 活动详情Activity<br>
NewsDetailActivity.java 新闻详情Activity<br>
QuestionDetailActivity.java 问答详情Activity<br>
SoftwareDetailActivity.java 软件详情Activity<br>
TranslationDetailActivity.java 翻译详情Activity<br>
###### constract包(p层和v层的接口统一写在契约类中)
DetailContract.java 详情contract基类<br>
BlogDetailContract.java 博客详情contract<br>
EventDetailContract.java 活动详情contract<br>
NewsDetailContract.java 新闻详情contract<br>
QuestionDetailContract.java 问答详情contract<br>
SoftwareDetailContract.java 软件详情contract<br>
TeatimeDetailContract.java Teatime详情contract<br>
TranslationDetailContract.java 翻译详情contract<br>
###### fragment包
DetailFragment.java 详情Fragment的基类<br>
BlogDetailFragment.java 与BlogDetailActivity相关联的Fragment<br>
EventDetailFragment.java 与EventDetailActivity相关联的Fragment<br>
NewsDetailFragment.java 与NewsDetailActivity相关联的Fragment<br>
QuestionDetailFragment.java 与QuestionDetailActivity相关联的Fragment<br>
SoftwareDetailFragment.java 与SoftwareDetailActivity相关联的Fragment<br>
TranslationDetailFragment.java 与TranslationDetailActivity相关联的Fragment<br>

##### (9)fragment包
GeneralViewPagerFragment.java 综合主界面<br>
UserBlogFragment.java 用户博客界面<br>
###### generallistfragment包
BlogGeneralListFragment.java 综合界面下的博客ListFragment<br>
EventGeneralListFragment.java 综合界面下的活动ListFragment<br>
NewsGeneralListFragment.java 综合界面下的新闻ListFragment<br>
QuestionGeneralListFragment.java 综合界面下的问答ListFragment

##### (10)teatime包
TeatimeDetailActivity.java Teatime详情Activity<br>
TeatimeCommentFragment.java Teatime评论Fragment<br>
TeatimeDetailViewPagerFragment.java TeatimeDetailActivity界面下赞与评论的Fragment<br>
TeatimLikeUsersFragment.java Teatime点赞用户Fragment<br>

##### (11)widget包
AboutRecommendView.java 相关推荐的view<br>
EventApplyDialog.java 综合下的活动申请对话框<br>
FlowLayout.java 自动换行的布局类<br>
RecyclerRefreshLayout.java 与RecyclerView相结合的SwipeRefreshLayout<br>
TWebView.java 自定义WebView<br>

#### 5.login包(登录包)
BindOpenIdActivity.java 第三方登录账号绑定操作Activity<br>
BindRegisterActivity.java 绑定注册Activity<br>
LoginActivity.java 登录Activity<br>

#### 6.main包(app主界面包)
MainActivity.java 主界面Activity<br>
MainActivityTabEnum.java 主界面FragmentTabHost枚举类<br>
SearchListFragment.java 搜索结果列表Fragment<br>
SearchViewPagerFragment.java 搜索的ViewPagerFragment<br>

#### 7.myinformation包(用户界面包)
AboutTeaScriptFragment.java 关于TeaScript的Fragment<br>
EventListFragment.java 用户活动Fragment<br>
EventViewPagerFragment.java 用户活动的ViewPagerFragment<br>
FeedbackFragment.java 意见反馈Fragment<br>
FriendsListFragment.java 粉丝列表Fragment<br>
FriendsViewPagerFragment.java 关注|粉丝ViewPagerFragment<br>
MessageListFragment.java 留言(私信)列表Fragment<br>
MyInformationFragment.java 用户信息主界面Fragment<br> 
MyLoginInformationFragment.java 用户登录信息主界面Fragment<br>
NoticeViewPagerFragment.java 消息中心ViewPagerFragment界面<br>
SettingFragment.java 系统设置Fragment<br>
SettingNotificationFragment.java 消息提醒设置Fragment<br>
TeatimeLikeListFragment.java 被赞过的Teatime的列表Fragment<br>
UserFavoriteListFragment.java 用户收藏列表Fragment<br>
UserFavoriteViewPagerFragment.java 用户收藏ViewPagerFragment<br>

#### 8.quickoption包(快速操作对话框)
##### (1)activity包
SelectFriendsActivity.java 发布Teatime时选择好友的Activity<br>
TeatimePubActivity.java 发布Teatime的Activity<br>
FindUserActivity.java 找人Activity<br>
ShakeActivity.java 摇一摇Activity<br>
##### (2)fragment包
ExploreFragment.java 发现的主界面Fragment
SameCityFragment.java 同城活动ListFragment
###### softwarefragment包 
OSSoftwareViewPagerFragment.java 开源软件ViewPagerFragment
SoftwareCatalogListFragment.java 开源软件分类ListFragment
SoftwareListFragment.java 开源软件介绍ListFragment

QuickOptionDialog.java 底部中间按钮弹出的快速操作对话框<br>

#### 9.teatime包
TeatimeListFragment.java Teatime列表Fragment<br>
TeatimeViewPagerFragment.java Teatime的ViewPagerFragment<br>

---------------------------------------------------------
### network包(网络包)
OperationResponseHandler.java 操作结果返回处理类<br>
TSHttpClient.java AsyncHttpClient的自定义封装类<br>
TSHttpHelper.java 获取User Agent(用户代理)信息，使服务器能够识别用户的操作系统等信息<br>

#### remote包
TeaScriptApi.java TeaScript网络操作封装类<br>
TeaScriptTeamApi.java TeaScript团队相关网络操作封装类<br>

---------------------------------------------------------
### service包(服务包)
DownloadService.java 下载服务类<br>
NoticeService.java 消息服务类<br>
NoticeUtils.java 消息服务帮助类<br>
PublicCommentTask.java 发布评论的任务类<br>
TaskService.java 任务服务类<br>
TeatimeTaskUtils.java 发布Teatime的任务工具类<br>

---------------------------------------------------------
### team包(团队包)
#### 1.activity包
TeamMainActivity.java 团队主界面Activity<br>
TeamNewActiveActivity.java 团队新动态Activity<br>
TeamNewIssueActivity.java 团队新任务Activity<br>

#### 2.adapter包
NotebookAdapter.java 便签适配器<br>
TeamActiveAdapter.java 团队动态适配器<br>
TeamDiaryAdapter.java 团队周报适配器<br>
TeamDiaryPagerAdapter.java 团队周报ViewPagerFragment所使用的PagerAdapter<br>
TeamDiscussAdapter.java 团队讨论适配器<br>
TeamIssueAdapter.java 团队任务适配器<br>
TeamIssueCatalogAdapter.java 团队任务分组适配器<br>
TeamMemberAdapter.java 团队成员GridView适配器<br>
TeamProjectAdapter.java 团队项目适配器<br>
TeamProjectAdapterPW.java 供TeamProjectPopWindow使用的团队项目适配器<br>
TeamProjectMemberAdapter.java 团队项目成员适配器<br>
TeamReplyAdapter.java 团队回复适配器<br>

#### 3.bean包
Author.java 作者实体类<br>
DiaryDayData.java 周报数据实体类<br>
MyIssueState.java 我的任务状态实体类<br>
Team.java 团队实体类<br>
TeamActive.java 团队动态实体类<br>
TeamActiveDetail.java 团队动态实体类的包装类<br>
TeamActiveList.java 团队动态列表实体类<br>
TeamDiary.java 团队周报实体类<br>
TeamDiatyDetail.java 团队周报实体类的包装类<br>
TeamDiaryList.java 团队周报列表实体类<br>
TeamDiscuss.java 团队讨论实体类<br>
TeamDiscussDetail.java 团队讨论实体类的包装类<br>
TeamGit.java 团队Git实体类<br>
TeamIssue.java 团队任务实体类<br>
TeamIssueDetail.java 团队任务实体类的包装类<br>
TeamIssueList.java 团队任务列表实体类<br>
TeamIssueCatalog.java 团队任务分组实体类<br>
TeamIssueCatalogList.java 分组列表实体类<br>
TeamList.java 团队信息列表实体类<br>
TeamMember.java 团队成员实体类<br>
TeamMemberList.java 团队成员列表实体类<br>
TeamProject.java 团队项目实体类<br>
TeamProjectList.java 团队项目列表实体类<br>
TeamReply.java 团队回复实体类<br>
TeamReplyDetail.java 团队回复实体类的包装类<br>
TeamReplyList.java 团队回复列表实体类<br>

#### 4.fragment包
##### viewpagerfragment包
TeamDiaryViewPagerFragment.java 团队周报ViewPagerFragment<br>
TeamIssueViewPagerFragment.java 团队任务ViewPagerFragment<br>
TeamMainViewPagerFragment.java 团队主界面ViewPagerFragment<br>
TeamMyIssueViewPagerFragment.java 我的团队任务ViewPagerFragment<br>
TeamProjectViewPagerFragment.java 团队项目ViewPagerFragment<br>

NotebookEditFragment.java 便签编辑Fragment<br>
NotebookFragment.java 便签Fragment<br>
TeamActiveListFragment.java 团队动态Fragment<br>
TeamBoardFragment.java 团队面板Fragment<br>
TeamDiaryDetailFragment.java 团队周报详情Fragment<br>
TeamDiscussDetailFragment.java 团队讨论详情Fragment<br>
TeamDiscussListFragment.java 团队讨论ListFragment<br>
TeamIssueDetailFragment.java 团队任务详情Fragment<br>
TeamIssueListFragment.java 团队任务ListFragment<br>
TeamMemberFragment.java 团队成员Fragment<br>
TeamMemberInformationFragment.java 团队成员信息Fragment<br>
TeamMyIssueFragment.java 我的团队任务ListFragment<br>
TeamProjectActveListFragment.java 团队项目动态ListFragment<br>
TeamProjectListFragment.java 团队项目ListFragment<br>
TeamProjectIssueCatalogListFragment.java 团队项目任务分组ListFragment<br>
TeamProjectMemberListFragment.java 团队项目成员ListFragment<br>
TeamTeatimeDetailFragment.java 团队Teatime详情Fragment<br>

#### 5.widget包
TeamProjectPopupWindow.java 团队项目选择PopupWindow<br>

---------------------------------------------------------
### util包(工具包)
AnimationsUtils.java 动画类工具类<br>
BitmapUtils.java 位图工具类<br>
ChatImageDisplay.java 聊天图片显示工具类<br>
ChatImageRequest.java 聊天图片请求工具类<br>
CyptoUtils.java 加密解密工具类<br>
FileUtils.java 文件操作工具类<br>
FontSizeUtils.java webview使用的字体大小工具类<br>
GlideImageLoader.java glide图片加载工具类<br>
HTMLUtils.java html工具类<br>
ImageUtils.java 图片操作工具类<br>
MethodsCompat.java 版本兼容工具类<br>
OpenIdCatalog.java 第三方平台类型工具类<br>
PlatformUtils.java 应用平台工具类<br>
QrCodeUtils.java 二维码工具类<br>
StringAndTimeUtils.java 字符串和时间操作工具类<br>
SynchronizeController.java 文件云同步解决工具类<br>
TDevice.java 设备相关信息工具类<br>
ThemeSwitchUtils.java 主题切换工具类<br>
TimeZoneUtils.java 时区工具类<br>
TLog.java 自定义log工具类<br>
UiUtils.java UI工具类<br>
UpdateManager.java 更新管理工具类<br>
UrlUtils.java url工具类<br>
ViewUtils.java view工具类<br>
XmlUtils.java xml工具类<br>

---------------------------------------------------------
### widget包(自定义控件包)
#### 1.dialog包(对话框包)
BaseDialog.java 对话框类的基类<br>
DialogAdapter.java 对话框适配器<br>
DialogController.java 对话框的控制接口<br>
DialogTitleView.java 自定义对话框的标题view<br>
DialogUtils.java 对话框工具<br>
EventApplyDialog.java 活动申请对话框<br>
QrCodeDialog.java 二维码对话框<br>
ReportDialog.java 举报对话框<br>
ShareDialog.java 分享对话框<br>
ShareMenuDialog.java 分享菜单对话框<br>

#### 2.emoji包(自定义表情控件)
EmojiAdapter.java 表情适配器<br>
EmojiConfig.java 表情设置类<br>
EmojiDisplayRules.java 表情显示规则<br>
EmojiFragment.java 表情Fragment<br>
EmojiIcon.java 表情实体类<br>
EmojiInputUtils.java 表情操作帮助工具<br>
EmojiKeyboardFragment.java 表情选择Fragment<br>
EmojiPagerFragment.java 表情显示Fragment<br>
EmojiPager.java 表情ViewPager类<br>
EmojiPagerAdapter.java EmojiPager的适配器类<br>
OnEmojiClickListener.java 表情点击监听器<br>
OnSendClickListener.java 表情发送监听器<br>
SoftKeyboardStateObservable.java 软键盘状态监听器<br>

#### 3.indicator包(指示器包)
CirclePagerIndicator.java 自定义圆形指示器<br>
LinePagerIndicator.java 自定义线性指示器<br>
PagerIndicator.java 指示器接口<br>

#### 4.recordbutton包(录音自定义控件)
RecordButton.java 录音按钮<br>
RecordButtonUtils.java 录音操作帮助类<br>

AvatarView.java 自定义头像控件<br>
BadgeView.java 自定义BadgeView<br>
CircleImageView.java 自定义圆形ImageView<br>
CustomScrollView.java 自定义ScrollView<br>
DiaryPagerView.java 自定义周报控件<br>
EmailSpan.java 自定义邮件span类<br>
EmptyLayout.java 显示错误加载的布局类<br>
EventBannerView.java 活动的BannerView<br>
EventHeaderView.java 活动的HeaderView<br>
FloorView.java 评论的底部展示控件<br>
IndexView.java 自定义索引控件<br>
KJDragGridView.java 可以进行拖拽的GridView<br>
MyURLSpan.java 自定义URLSpan<br>
NewsBannerView.java 新闻的BannerView<br>
NewsHeaderView.java 新闻的HeaderView<br>
NoLinkURLSpan.java 无下划线的URLSpan类<br>
NotebookCircleView.java Notebook圆形ImageView<br>
PagerSlidingTabStrip.java 专门用于ViewPager的滑动选项卡<br>
SmallCardSpan.java 自定义ReplacementSpan(实现小卡片)<br>
SmoothScroller.java 自定义Scroller<br>
SwitchLayout.java 左右切换屏幕的控件<br>
TeatimeTextView.java 用于Teatime的自定义TextView控件<br>
TouchImageView.java 支持缩放等功能的ImageView<br>
TSFragmentTabHost.java 自定义FragmentTabHost<br>
TSLinkMovementMethod.java 自定义LinkMovementMethod<br>
TSRefreshLayout.java 自定义RefreshLayout<br>
TSSwitchButton.java 自定义SwitchButton<br>
TSToast.java 自定义Toast<br>


## 依赖包介绍

1.网络请求库 android-async-http<br>
2.android图片加载框架 glide<br>
3.java与json转换库 google-gson<br>
4.android动画集库 nineoldandroids<br>

5.友盟分享 open_sdk<br>
  qq分享  SocialSDK_QQ_Full<br>
  sina分享 SocialSDK_Sina_Full<br>
  
  微信分享<br>
  SocialSDK_WeiXin_Full<br>
  wechat-sdk-android-with-mta<br>
           
  友盟分享控件<br>
  umeng_shareboard_widget<br>
  umeng_social_shareboard<br>
  umeng_social_shareview<br>
                
  友盟api<br>
  umeng_social_api<br>
  umeng_social_net<br>
  
  友盟工具 umeng_social_tool<br>
  微博sdk weiboSDKCore<br>
  
6.中文字符与拼音转换库 pinyin4j<br>
7.xml转换库 xstream<br>
8.二维码识别库 zxing<br>










































