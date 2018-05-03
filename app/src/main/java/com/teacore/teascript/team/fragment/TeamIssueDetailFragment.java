package com.teacore.teascript.team.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.team.bean.TeamIssueDetail;
import com.teacore.teascript.team.bean.TeamReply;
import com.teacore.teascript.team.bean.TeamReplyDetail;
import com.teacore.teascript.team.bean.TeamReplyList;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.util.ViewUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.OnSendClickListener;

import java.util.List;

import cz.msebera.android.httpclient.Header;


/**团队任务详情
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-3
 */

public class TeamIssueDetailFragment extends BaseFragment implements OnSendClickListener{

    private Team mTeam;
    private TeamIssue mTeamIssue;
    private TeamIssueCatalog mCatalog;

    private EmptyLayout mErrorLayout;
    private View mContent;
    private View mProjectLL;
    private TextView mProjectTV;
    private TextView mTitleFaTV;
    private TextView mTitleTV;
    private TextView mChildTV;
    private TextView mToUserTV;
    private TextView mDeadlineTV;
    private TextView mCooperateUserTV;
    private TextView mStateTV;
    private LinearLayout mLabelsLL;
    private TextView mAttachmentsTV;
    private TextView mRelationsTV;

    private LinearLayout mTitleLL;
    private LinearLayout mStateLL;
    private LinearLayout mChildLL;
    private LinearLayout mChildsLL;
    private LinearLayout mCommentsLL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView=inflater.inflate(R.layout.fragment_team_issue,container,false);

        Intent args=getActivity().getIntent();
        if(args!=null){
            mTeam=(Team) args.getSerializableExtra("team");
            mTeamIssue=(TeamIssue) args.getSerializableExtra("issue");
            mCatalog=(TeamIssueCatalog) args.getSerializableExtra("issue_catalog");
        }
        if(mCatalog!=null){
            ((BaseActivity) getActivity()).setActionBarTitle(mCatalog
                    .getTitle());
        }
        initView(rootView);
        initData();
        return rootView;
    }

    @Override
    public void initView(View view){
        mErrorLayout=(EmptyLayout) view.findViewById(R.id.error_layout);
        mContent=view.findViewById(R.id.content);
        mProjectLL=view.findViewById(R.id.issue_project_ll);
        mProjectTV=(TextView) view.findViewById(R.id.issue_project_tv);
        mTitleFaTV=(TextView) view.findViewById(R.id.issue_title_fa_tv);
        mTitleTV=(TextView) view.findViewById(R.id.issue_title_tv);
        mChildTV=(TextView) view.findViewById(R.id.issue_child_tv);
        mToUserTV=(TextView) view.findViewById(R.id.issue_assign_tv);
        mCooperateUserTV=(TextView) view.findViewById(R.id.issue_cooperate_user_tv);
        mDeadlineTV=(TextView) view.findViewById(R.id.issue_deadline_tv);
        mStateTV=(TextView) view.findViewById(R.id.issue_state_tv);
        mLabelsLL=(LinearLayout) view.findViewById(R.id.issue_labels_ll);
        mAttachmentsTV=(TextView) view.findViewById(R.id.issue_attachments_tv);
        mRelationsTV=(TextView) view.findViewById(R.id.issue_relations_tv);

        mTitleLL=(LinearLayout) view.findViewById(R.id.issue_title_ll);
        mStateLL=(LinearLayout) view.findViewById(R.id.issue_state_ll);
        mChildLL=(LinearLayout) view.findViewById(R.id.issue_child_ll);
        mChildsLL=(LinearLayout) view.findViewById(R.id.issue_childs_ll);
        mCommentsLL=(LinearLayout) view.findViewById(R.id.issue_comments_ll);

        //设置fontawesome字体
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_assign_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_cooperate_user_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_deadline_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_state_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_labels_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_child_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_relations_fa_tv));
        TypefaceUtils.setTypeface((TextView) view.findViewById(R.id.issue_attachments_fa_tv));

        mTitleLL.setOnClickListener(this);
        mStateLL.setOnClickListener(this);
        mChildLL.setOnClickListener(this);

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestDetail();
            }
        });
    }

    @Override
    public void initData(){
        super.initData();
        requestDetail();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DetailActivity) getActivity()).emojiFragment.hideFlagButton();
    }


    private final AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

            TeamIssueDetail teamIssueDetail = XmlUtils.toBean(
                    TeamIssueDetail.class, arg2);

            if (teamIssueDetail != null) {
                mContent.setVisibility(View.VISIBLE);
                mErrorLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
                fillUI(teamIssueDetail.getTeamIssue());
                requestIssueComments();
            } else {
                mContent.setVisibility(View.INVISIBLE);
                mErrorLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                mErrorLayout.setEmptyMessage("该任务可能已被删除");
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            mErrorLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onStart() {
            mErrorLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        }

    };

    private void requestDetail() {
        TeaScriptTeamApi.getTeamIssueDetail(mTeam.getId(), mTeamIssue.getId(),
                mDetailHandler);
    }

    private void fillUI(TeamIssue teamIssue){
        if(teamIssue==null)
            return;
        this.mTeamIssue=teamIssue;
        if(mTeamIssue.getProject()!=null && mTeamIssue.getProject().getGit()!=null){
            mProjectLL.setVisibility(View.VISIBLE);
            //项目是否已经Git
            String pushState=mTeamIssue.getGitpush()!=TeamIssue.TEAM_ISSUE_GITPUSHED?"-未同步"
                    :"-已同步";
            mProjectTV.setText(mTeamIssue.getProject().getGit().getName()+pushState);
        }else{
            mProjectLL.setVisibility(View.GONE);
        }

        mTitleTV.setText(mTeamIssue.getTitle());

        setIssueState();

        if(mTeamIssue.getToUser()!=null
                && !TextUtils.isEmpty(mTeamIssue.getToUser().getName())){
            mToUserTV.setText(mTeamIssue.getToUser().getName());
        }else{
            mToUserTV.setText("未指派");
        }

        if (!TextUtils.isEmpty(mTeamIssue.getDeadlineTime())) {
            mDeadlineTV.setText(mTeamIssue.getDeadlineTimeText());
        } else {
            mDeadlineTV.setText("未指定截止日期");
        }

        if (mTeamIssue.getAttachments().getTotalCount() != 0) {
            mAttachmentsTV.setText(mTeamIssue.getAttachments().getTotalCount()
                    + "");
        } else {
            mAttachmentsTV.setText("暂无附件");
        }

        if (mTeamIssue.getRelations().getTotalCount() != 0) {
            mRelationsTV.setText(mTeamIssue.getRelations().getTotalCount() + "");
        } else {
            mRelationsTV.setText("暂无关联");
        }

        if (mTeamIssue.getChildIssues().getTotalCount() != 0) {
            String childIssueState = mTeamIssue.getChildIssues()
                    .getTotalCount()
                    + "个子任务，"
                    + mTeamIssue.getChildIssues().getClosedCount() + "个已完成";
            mChildTV.setText(childIssueState);
        } else {
            mChildTV.setText("暂无子任务");
        }

        setChildIssues(mTeamIssue.getChildIssues().getIssuesList());

        setLabels(mTeamIssue);
        setIssueCollaborator();
    }

    private void setIssueState(){
        TypefaceUtils.setTypeface(mTitleFaTV,
                mTeamIssue.getIssueStateFaTextId());

        if (mTeamIssue.getState().equals("closed")
                || mTeamIssue.getState().equals("accepted")) {
            ViewUtils
                    .setTextViewLineFlag(mTitleTV, Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            ViewUtils.setTextViewLineFlag(mTitleTV, 0);
        }

        mStateTV.setText(mTeamIssue.getIssueStateText());
    }

    private void setChildIssues(List<TeamIssue> list) {
        if (list == null || list.isEmpty())
            return;

        for (TeamIssue teamIssue : list) {
            addChildIssue(teamIssue);
        }
    }

    private void addChildIssue(final TeamIssue teamIssue) {
        if (teamIssue == null)
            return;

        final View cell = LayoutInflater.from(getActivity()).inflate(
                R.layout.list_cell_team_issue_child, null, false);

        AvatarView avatarView = (AvatarView) cell.findViewById(R.id.avatar_av);

        avatarView.setAvatarUrl(teamIssue.getToUser().getPortrait());

        final TextView content = (TextView) cell.findViewById(R.id.content_tv);

        content.setText(teamIssue.getTitle());

        setChildIssueState(cell, teamIssue);

        cell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateChildIssueState(cell, teamIssue);
            }
        });

        mChildsLL.addView(cell);
    }

    private void setChildIssueState(View cell, TeamIssue childIssue) {
        TextView content = (TextView) cell.findViewById(R.id.content_tv);
        TextView state = (TextView) cell.findViewById(R.id.state_tv);

        if (childIssue.getState().equalsIgnoreCase("closed")) {
            ViewUtils.setTextViewLineFlag(content, Paint.STRIKE_THRU_TEXT_FLAG);
            TypefaceUtils.setTypeface(state, R.string.fa_check_circle_o);
        } else {
            ViewUtils.setTextViewLineFlag(content, 0);
            TypefaceUtils.setTypeface(state, R.string.fa_circle_o);
        }
    }

    private void updateChildIssueState(final View cell,
                                       final TeamIssue childIssue) {
        switchChildIssueState(childIssue);
        TeaScriptTeamApi.updateChildIssue(mTeam.getId(), "state", childIssue,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                        Result result = XmlUtils.toBean(ResultData.class, arg2)
                                .getResult();
                        if (result.OK()) {
                            setChildIssueState(cell, childIssue);
                        } else {
                            switchChildIssueState(childIssue);
                        }
                        AppContext.showToast(result.getMessage());
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        AppContext.showToast("更新失败");
                        switchChildIssueState(childIssue);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showWaitDialog("正在更新状态...");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideWaitDialog();
                    }
                });
    }

    private void switchChildIssueState(TeamIssue childIssue) {
        if (childIssue.getState().equals("opened")) {
            childIssue.setState("closed");
        } else {
            childIssue.setState("opened");
        }
    }

    private void setLabels(TeamIssue issue) {
        if (issue.getLabels() == null || issue.getLabels().isEmpty()) {
            mLabelsLL.setVisibility(View.GONE);
        } else {
            for (TeamIssue.Label label : issue.getLabels()) {
                TextView text = (TextView) LayoutInflater.from(getActivity())
                        .inflate(R.layout.list_cell_team_issue_label, null, false);
                text.setText(label.getName());
                String colorStr = label.getColor();
                if (colorStr.equalsIgnoreCase("#ffffff")) {
                    colorStr = "#000000";
                }
                int color = Color.parseColor(colorStr);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 0, 4, 0);

                GradientDrawable d = (GradientDrawable) text.getBackground();
                d.setStroke(1, color);
                text.setTextColor(color);
                mLabelsLL.addView(text, params);
            }
        }
    }

    private void setIssueCollaborator() {
        StringBuffer cooperateUserStr = new StringBuffer();
        if (mTeamIssue.getCollaborators().size() > 0) {
            for (int i = 0; i < mTeamIssue.getCollaborators().size(); i++) {
                if (i == mTeamIssue.getCollaborators().size() - 1) {
                    cooperateUserStr.append(mTeamIssue.getCollaborators()
                            .get(i).getName());
                } else {
                    cooperateUserStr.append(mTeamIssue.getCollaborators()
                            .get(i).getName()
                            + "，");
                }
            }
            mCooperateUserTV.setText(cooperateUserStr.toString());
        } else {
            mCooperateUserTV.setText("暂无协作者");
        }
    }


    private final AsyncHttpResponseHandler mChangeIssueHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

            Result result = XmlUtils.toBean(ResultData.class, arg2).getResult();
            if (result.OK()) {
                setIssueState();
            }
            AppContext.showToast(result.getMessage());
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {

            AppContext.showToast("更新失败");
        }

        @Override
        public void onStart() {
            showWaitDialog("正在修改...");
        }

        ;

        @Override
        public void onFinish() {
            hideWaitDialog();
        }
    };

    @Override
    public void onClick(View v) {
        //
        switch (v.getId()) {
            case R.id.issue_title_ll:
            case R.id.issue_state_ll:
                changeIssueState();
                break;
            case R.id.issue_assign_ll:
                // 暂时屏蔽修改任务指派
                // Bundle bundle = new Bundle();
                // bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, mTeam);
                // bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT,
                // mTeamIssue.getProject());
                // UIHelper.showSimpleBack(getActivity(),
                // SimpleBackPage.TEAM_PROJECT_MEMBER_SELECT, bundle);
                break;
            case R.id.issue_cooperate_user_ll:

                break;
            case R.id.issue_deadline_ll:

                break;
            case R.id.issue_child_ll:
                if (mChildsLL.getVisibility() == View.GONE) {
                    mChildsLL.setVisibility(View.VISIBLE);
                } else {
                    mChildsLL.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }

    private AlertDialog dialog;

    private void changeIssueState() {
        if (!mTeamIssue.getAuthority().isUpdateState()) {
            AppContext.showToast("抱歉，无更改权限");
            return;
        }

        final String[] items = getResources().getStringArray(
                R.array.team_issue_state);
        final String[] itemsEn = getResources().getStringArray(
                R.array.team_issue_state_en);
        int index = 0;
        for (int i = 0; i < itemsEn.length; i++) {
            if (itemsEn[i].equals(mTeamIssue.getState())) {
                index = i;
            }
        }

        final int selIndex = index;

        dialog = DialogUtils.getSingleChoiceDialog(getActivity(), "更改任务状态", items, selIndex, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == selIndex) {
                            dialog.dismiss();
                            return;
                        }
                        mTeamIssue.setState(itemsEn[i].toString());
                        TeaScriptTeamApi.changeIssueState(mTeam.getId(), mTeamIssue,
                                "state", mChangeIssueHandler);
                        dialog.dismiss();
                    }
                }).show();
    }

    //请求任务的评论
    private void requestIssueComments() {

        TeaScriptTeamApi.getTeamReplyList(mTeam.getId(), mTeamIssue.getId(),
                TeamReply.REPLY_TYPE_ISSUE, 0, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                        TeamReplyList list = XmlUtils.toBean(
                                TeamReplyList.class, arg2);

                        if (list != null && !list.getList().isEmpty()) {
                            fillComments(list.getList());
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {


                    }
                });
    }

    private void fillComments(List<TeamReply> list) {
        if (list == null || list.isEmpty())
            return;
        for (TeamReply teamReply : list) {
            addComment(teamReply);
        }
    }

    private void addComment(final TeamReply reply) {
        View cell = LayoutInflater.from(getActivity()).inflate(
                R.layout.list_cell_team_reply, null, false);
        AvatarView avatarView = (AvatarView) cell.findViewById(R.id.avatar_av);
        avatarView.setAvatarUrl(reply.getAuthor().getPortrait());
        TextView name = (TextView) cell.findViewById(R.id.name_tv);
        name.setText(reply.getAuthor().getName());
        TextView content = (TextView) cell.findViewById(R.id.content_tv);
        content.setText(HtmlUtils.delHTMLTag(reply.getContent()));
        TextView time = (TextView) cell.findViewById(R.id.time_tv);
        time.setText(TimeUtils.friendly_time(reply.getCreateTime()));
        mCommentsLL.addView(cell);
    }


    @Override
    public void onClickSendButton(Editable str) {
        if (mTeamIssue == null) {
            return;
        }
        showWaitDialog("提交评论中...");
        TeaScriptTeamApi.pubTeamIssueComment(mTeam.getId(), mTeamIssue.getId(),
                str.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        TeamReply reply = XmlUtils.toBean(TeamReplyDetail.class,
                                arg2).getTeamReply();
                        if (reply != null) {
                            addComment(reply);
                        } else {
                            AppContext.showToast("评论失败");
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        Result result = XmlUtils.toBean(ResultData.class, arg2)
                                .getResult();
                        AppContext.showToast(result.getMessage());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideWaitDialog();
                    }
                });
    }

    @Override
    public void onClickFlagButton() {
    }


}
