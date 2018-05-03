package com.teacore.teascript.module.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseDetailFragment;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.module.detail.fragment.BlogDetailFragment;
import com.teacore.teascript.module.back.currencyfragment.CommentListFragment;
import com.teacore.teascript.module.detail.fragment.EventDetailFragment;
import com.teacore.teascript.module.detail.fragment.NewsDetailFragment;
import com.teacore.teascript.module.detail.fragment.PostDetailFragment;
import com.teacore.teascript.module.detail.fragment.SoftwareDetailFragment;
import com.teacore.teascript.module.detail.fragment.TeatimeDetailFragment;
import com.teacore.teascript.module.detail.fragment.ToolbarFragment;
import com.teacore.teascript.module.detail.fragment.ToolbarFragment.OnActionClickListener;
import com.teacore.teascript.module.detail.fragment.ToolbarFragment.ToolAction;
import com.teacore.teascript.module.general.detail.activity.BlogDetailActivity;
import com.teacore.teascript.team.fragment.TeamDiaryDetailFragment;
import com.teacore.teascript.team.fragment.TeamDiscussDetailFragment;
import com.teacore.teascript.team.fragment.TeamIssueDetailFragment;
import com.teacore.teascript.team.fragment.TeamTeatimeDetailFragment;
import com.teacore.teascript.widget.emoji.EmojiFragment;
import com.teacore.teascript.widget.emoji.OnSendClickListener;


/**详情activity(包括:资讯、博客、软件、问答、动弹)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-16.
 */

public class DetailActivity extends BaseActivity implements OnSendClickListener{

    public static final int DISPLAY_NEWS = 0;
    public static final int DISPLAY_BLOG = 1;
    public static final int DISPLAY_SOFTWARE = 2;
    public static final int DISPLAY_POST = 3;
    public static final int DISPLAY_TEATIME = 4;
    public static final int DISPLAY_EVENT = 5;
    public static final int DISPLAY_TEAM_ISSUE_DETAIL = 6;
    public static final int DISPLAY_TEAM_DISCUSS_DETAIL = 7;
    public static final int DISPLAY_TEAM_TEATIME_DETAIL = 8;
    public static final int DISPLAY_TEAM_DIARY = 9;
    public static final int DISPLAY_COMMENT = 10;

    public static final String BUNDLE_KEY_DISPLAY_TYPE="bundle_key_display_type";

    private OnSendClickListener currentFragment;


    public EmojiFragment emojiFragment=new EmojiFragment();
    public ToolbarFragment toolbarFragment=new ToolbarFragment();

    @Override
    protected int getLayoutId(){
        return R.layout.activity_detail;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.actionbar_title_detail;
    }

    @Override
    protected void init(Bundle savedIntanceState){

        int displayType=getIntent().getIntExtra(BUNDLE_KEY_DISPLAY_TYPE,0);

        BaseFragment fragment=null;

        int actionBarTitle=0;

        switch (displayType) {

            case DISPLAY_NEWS:
                actionBarTitle = R.string.actionbar_title_news;
                fragment = new NewsDetailFragment();
                break;

            case DISPLAY_BLOG:
                actionBarTitle = R.string.actionbar_title_blog;
                fragment = new BlogDetailFragment();
                BlogDetailActivity.show(this, getIntent().getIntExtra("id", 0));
                finish();
                break;

            case DISPLAY_SOFTWARE:
                actionBarTitle = R.string.actionbar_title_software;
                fragment = new SoftwareDetailFragment();
                break;

            case DISPLAY_POST:
                actionBarTitle = R.string.actionbar_title_question;
                fragment = new PostDetailFragment();
                break;

            case DISPLAY_TEATIME:
                actionBarTitle = R.string.actionbar_title_teatime;
                fragment = new TeatimeDetailFragment();
                break;

            case DISPLAY_EVENT:
                actionBarTitle = R.string.actionbar_title_event_detail;
                fragment = new EventDetailFragment();
                break;

            case DISPLAY_TEAM_ISSUE_DETAIL:
                actionBarTitle = R.string.team_issue_detail;
                fragment = new TeamIssueDetailFragment();
                break;

            case DISPLAY_TEAM_DISCUSS_DETAIL:
                actionBarTitle = R.string.actionbar_title_question;
                fragment = new TeamDiscussDetailFragment();
                break;

            case DISPLAY_TEAM_TEATIME_DETAIL:
                actionBarTitle = R.string.actionbar_dynamic_detail;
                fragment = new TeamTeatimeDetailFragment();
                break;

            case DISPLAY_TEAM_DIARY:
                actionBarTitle = R.string.team_diary_detail;
                fragment = new TeamDiaryDetailFragment();
                break;

            //评论不需要DetailFragment
            case DISPLAY_COMMENT:
                actionBarTitle = R.string.actionbar_title_comment;
                fragment = new CommentListFragment();

            default:
                break;
        }

        setActionBarTitle(actionBarTitle);

        //切换到相应的Fragment
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();

        trans.replace(R.id.container, fragment);

        trans.commitAllowingStateLoss();

        if (fragment != null && fragment instanceof OnSendClickListener) {
            currentFragment = (OnSendClickListener) fragment;
        } else {
            currentFragment = new OnSendClickListener() {
                @Override
                public void onClickSendButton(Editable str) {
                }

                @Override
                public void onClickFlagButton() {
                }
            };
        }

    }

    @Override
    public void onClick(View view){}

    @Override
    public void initView(){
        if (currentFragment instanceof TeatimeDetailFragment
                || currentFragment instanceof TeamTeatimeDetailFragment
                || currentFragment instanceof TeamDiaryDetailFragment
                || currentFragment instanceof TeamIssueDetailFragment
                || currentFragment instanceof TeamDiscussDetailFragment
                || currentFragment instanceof CommentListFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.emoji_keyboard, emojiFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.emoji_keyboard, toolbarFragment).commit();
        }

        toolbarFragment.setOnActionClickListener(new OnActionClickListener() {
            @Override
            public void onActionClick(ToolAction action) {
                switch (action) {
                    case ACTION_CHANGE:
                    case ACTION_WRITE_COMMENT:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.anim_footer_menu_slide_in,
                                        R.anim.anim_footer_menu_slide_out)
                                .replace(R.id.emoji_keyboard, emojiFragment)
                                .commit();
                        break;
                    case ACTION_FAVORITE:
                        ((BaseDetailFragment) currentFragment)
                                .favourOrRemove();
                        break;
                    case ACTION_REPORT:
                        ((BaseDetailFragment) currentFragment).onReportMenuClick();
                        break;
                    case ACTION_SHARE:
                        ((BaseDetailFragment) currentFragment).handleShare();
                        break;
                    case ACTION_VIEW_COMMENT:
                        ((BaseDetailFragment) currentFragment)
                                .onClickShowComment();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void initData() {
    }

    @Override
    public void onClickSendButton(Editable str) {
        currentFragment.onClickSendButton(str);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (emojiFragment.isShowEmojiKeyBoard()) {
                    emojiFragment.hideAllKeyBoard();
                    return true;
                }
                if (emojiFragment.getEditText().getTag() != null) {
                    emojiFragment.getEditText().setTag(null);
                    emojiFragment.getEditText().setHint("说点什么吧");
                    return true;
                }
            } catch (NullPointerException e) {
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClickFlagButton() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_footer_menu_slide_in,
                        R.anim.anim_footer_menu_slide_out)
                .replace(R.id.emoji_keyboard, toolbarFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
