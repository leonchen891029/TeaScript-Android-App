package com.teacore.teascript.module.back;

import com.teacore.teascript.R;
import com.teacore.teascript.module.back.currencyfragment.ActiveListFragment;
import com.teacore.teascript.module.back.currencyfragment.BrowserFragment;
import com.teacore.teascript.module.back.currencyfragment.ChatMessageListFragment;
import com.teacore.teascript.module.back.currencyfragment.EventApplyListFragment;
import com.teacore.teascript.module.back.currencyfragment.PostTagListFragment;
import com.teacore.teascript.module.back.currencyfragment.SoftwareTeatimesListFragment;
import com.teacore.teascript.module.back.currencyfragment.UserCenterFragment;
import com.teacore.teascript.module.back.currencyfragment.VoiceTeatimeFragment;
import com.teacore.teascript.module.back.currencyfragment.CommentListFragment;
import com.teacore.teascript.module.quickoption.fragment.softwarefragment.OSSoftwareViewPagerFragment;
import com.teacore.teascript.module.general.fragment.generallistfragment.EventGeneralListFragment;
import com.teacore.teascript.module.general.fragment.UserBlogFragment;
import com.teacore.teascript.module.general.teatime.TeatimeLikeUsersFragment;
import com.teacore.teascript.module.main.SearchViewPagerFragment;
import com.teacore.teascript.module.myinformation.AboutTeaScriptFragment;
import com.teacore.teascript.module.myinformation.EventViewPagerFragment;
import com.teacore.teascript.module.myinformation.FeedbackFragment;
import com.teacore.teascript.module.myinformation.FriendsViewPagerFragment;
import com.teacore.teascript.module.myinformation.MyInformationFragment;
import com.teacore.teascript.module.myinformation.MyLoginInformationFragment;
import com.teacore.teascript.module.myinformation.NoticeViewPagerFragment;
import com.teacore.teascript.module.myinformation.SettingFragment;
import com.teacore.teascript.module.myinformation.SettingNotificationFragment;
import com.teacore.teascript.module.myinformation.UserFavoriteViewPagerFragment;
import com.teacore.teascript.module.teatime.TeatimeListFragment;
import com.teacore.teascript.team.fragment.NotebookEditFragment;
import com.teacore.teascript.team.fragment.NotebookFragment;
import com.teacore.teascript.team.fragment.TeamActiveListFragment;
import com.teacore.teascript.team.fragment.TeamDiaryDetailFragment;
import com.teacore.teascript.team.fragment.TeamDiscussListFragment;
import com.teacore.teascript.team.fragment.TeamIssueListFragment;
import com.teacore.teascript.team.fragment.TeamMemberInformationFragment;
import com.teacore.teascript.team.fragment.TeamProjectListFragment;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamDiaryViewPagerFragment;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamIssueViewPagerFragment;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamMyIssueViewPagerFragment;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamProjectViewPagerFragment;

/**SimpleBackActivity使用的Fragment类型枚举类
 *@author 陈晓帆
 *@version 1.0
 * Created 2017-3-8
 */

public enum BackFragmentEnum {

        SEARCH(0, R.string.actionbar_title_search, SearchViewPagerFragment.class),

        COMMENT(1, R.string.actionbar_title_comment, CommentListFragment.class),

        SOFTWARE_TEATIME(2, R.string.actionbar_title_softteatime, SoftwareTeatimesListFragment.class),

        USER_CENTER(3, R.string.actionbar_title_user_center, UserCenterFragment.class),

        MY_INFORMATION(4, R.string.actionbar_title_my_information, MyInformationFragment.class),

        MY_INFORMATION_DETAIL(5, R.string.actionbar_title_my_information, MyLoginInformationFragment.class),

        USER_FAVORITE(6, R.string.actionbar_title_user_favorite, UserFavoriteViewPagerFragment.class),

        USER_FRIENDS(7, R.string.actionbar_title_my_friends,FriendsViewPagerFragment.class),

        USER_MESSAGE(8, R.string.actionbar_title_message, NoticeViewPagerFragment.class),

        USER_BLOG(9, R.string.actionbar_title_user_blog, UserBlogFragment.class),

        USER_EVENT(10, R.string.actionbar_title_my_event, EventViewPagerFragment.class),

        FEED_BACK(11, R.string.actionbar_title_feedback, FeedbackFragment.class),

        SETTING(12, R.string.actionbar_title_setting, SettingFragment.class),

        SETTING_NOTIFICATION(13, R.string.actionbar_title_setting_notification, SettingNotificationFragment.class),

        ABOUT_TEASCRIPT(14, R.string.actionbar_title_about_teascript, AboutTeaScriptFragment.class),

        ACTIVE(15, R.string.actionbar_title_active, ActiveListFragment.class),

        OPENSOURCE_SOFTWARE(16, R.string.actionbar_title_ossoftware, OSSoftwareViewPagerFragment.class),

        QUESTION_POST(17, R.string.actionbar_title_question, PostTagListFragment.class),

        CHAT_MESSAGE_DETAIL(18, R.string.actionbar_title_chat_message, ChatMessageListFragment.class),

        VOICE_TEATIME(19, R.string.actionbar_title_voice_teatime, VoiceTeatimeFragment.class),

        EVENT_APPLY(20, R.string.actionbar_title_event_apply, EventApplyListFragment.class),

        SAME_CITY(21, R.string.actionbar_title_same_city, EventGeneralListFragment.class),

        NOTE(22, R.string.actionbar_title_note, NotebookFragment.class),

        NOTE_EDIT(23, R.string.actionbar_title_noteedit, NotebookEditFragment.class),

        BROWSER(24, R.string.app_name, BrowserFragment.class),

        TEAM_USER_INFO(25, R.string.actionbar_title_team_member_information, TeamMemberInformationFragment.class),

        TEAM_MY_ISSUE(26, R.string.actionbar_title_team_my_issue, TeamMyIssueViewPagerFragment.class),

        TEAM_PROJECT_MAIN(27, R.string.actionbar_title_team_project, TeamProjectViewPagerFragment.class),

        TEAM_PROJECT(28, R.string.actionbar_title_team_project, TeamProjectListFragment.class),

        TEAM_ISSUE_MAIN(29, R.string.actionbar_title_team_issue, TeamIssueViewPagerFragment.class),

        TEAM_ISSUE(30, R.string.actionbar_title_team_issue, TeamIssueListFragment.class),

        TEAM_DISCUSS(31, R.string.actionbar_title_team_discuss, TeamDiscussListFragment.class),

        TEAM_ACTIVE(32, R.string.actionbar_title_team_actvie, TeamActiveListFragment.class),

        TEAM_DIRAY(33, R.string.actionbar_title_team_diary, TeamDiaryViewPagerFragment.class),

        TEAM_DIRAY_DETAIL(34, R.string.actionbar_title_team_diary_detail, TeamDiaryDetailFragment.class),

        TEATIME_LIKE_USER(35, R.string.actionbar_title_teatime_like_user, TeatimeLikeUsersFragment.class),

        TEATIME_TOPIC(36, R.string.actionbar_title_teatime, TeatimeListFragment.class);

        private int title;
        private Class<?> clz;
        private int value;

        private BackFragmentEnum(int value, int title, Class<?> clz) {
            this.value = value;
            this.title = title;
            this.clz = clz;
        }

        public int getTitle() {
            return title;
        }

        public void setTitle(int title) {
            this.title = title;
        }

        public Class<?> getClz() {
            return clz;
        }

        public void setClz(Class<?> clz) {
            this.clz = clz;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public static BackFragmentEnum getFragmentByValue(int val) {
            for (BackFragmentEnum p : values()) {
                if (p.getValue() == val)
                    return p;
            }
            return null;
        }


}
