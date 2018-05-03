package com.teacore.teascript.team.adapter;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.util.ViewUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**任务适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-7
 */
public class TeamIssueAdapter extends BaseListAdapter<TeamIssue> {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder=null;
        int type=getItemViewType(position);

        if(convertView ==null|| convertView.getTag()==null){
            convertView=getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_team_issue,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        TeamIssue item=mDatas.get(position);

        viewHolder.mTitleTV.setText(item.getTitle());

        String date= TimeUtils.friendly_time2(item.getCreateTime());
        String preDate="";
        if (position > 0) {
            preDate = TimeUtils.friendly_time2(mDatas.get(position - 1)
                    .getCreateTime());
        }
        if (preDate.equals(date)) {
            viewHolder.mMainTitleTV.setVisibility(View.GONE);
        } else {
            viewHolder.mMainTitleTV.setText(date);
            viewHolder.mMainTitleTV.setVisibility(View.VISIBLE);
        }

        setIssueState(viewHolder,item);
        setIssueSource(viewHolder,item);

        viewHolder.mAuthorTV.setText(item.getAuthor().getName());
        if(item.getToUser()==null || TextUtils.isEmpty(item.getToUser().getName())){
            viewHolder.mAssignTV.setText("未指派");
            viewHolder.mAssignerTV.setVisibility(View.GONE);
        }else{
            viewHolder.mAssignTV.setText("未指派");
            viewHolder.mAssignerTV.setVisibility(View.GONE);
            viewHolder.mAssignerTV.setText(item.getToUser().getName());
        }

        viewHolder.mTimeTV.setText(TimeUtils.friendly_time(item.getCreateTime()));
        viewHolder.mCommentCountTV.setText(item.getReplyCount() + "");

        if (item.getProject() != null && item.getProject().getGit() != null) {
            viewHolder.mProjectTV.setVisibility(View.VISIBLE);
            String gitState = item.getGitpush() == TeamIssue.TEAM_ISSUE_GITPUSHED ? ""
                    : " -未同步";
            setText(viewHolder.mProjectTV, item.getProject().getGit().getName() + gitState);
        } else {
            viewHolder.mProjectTV.setVisibility(View.GONE);
        }

        String deadlineTime = item.getDeadlineTime();
        if (!StringUtils.isEmpty(deadlineTime)) {
            viewHolder.mAcceptTimeTV.setVisibility(View.VISIBLE);
            setText(viewHolder.mAcceptTimeTV, getDeadlineTime(item), true);
        } else {
            viewHolder.mAcceptTimeTV.setVisibility(View.GONE);
        }

        if (item.getAttachments().getTotalCount() != 0) {
            viewHolder.mAttachmentsTV.setVisibility(View.VISIBLE);
            viewHolder.mAttachmentsTV.setText("附件" + item.getAttachments().getTotalCount()
                    + "");
        } else {
            viewHolder.mAttachmentsTV.setVisibility(View.GONE);
        }

        if (item.getChildIssues() != null
                && item.getChildIssues().getTotalCount() != 0) {
            viewHolder.mChildsTV.setVisibility(View.VISIBLE);
            setText(viewHolder.mChildsTV, "子任务("
                    + item.getChildIssues().getClosedCount() + "/"
                    + item.getChildIssues().getTotalCount() + ")");
        } else {
            viewHolder.mChildsTV.setVisibility(View.GONE);
        }

        if (item.getRelations().getTotalCount() != 0) {
            viewHolder.mRelationsTV.setVisibility(View.VISIBLE);
            viewHolder.mRelationsTV.setText("关联" + item.getRelations().getTotalCount()
                    + "");
        } else {
            viewHolder.mRelationsTV.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void setIssueState(ViewHolder viewHolder, TeamIssue teamIssue) {
        String state = teamIssue.getState();
        if (TextUtils.isEmpty(state))
            return;
        TypefaceUtils.setTypeface(viewHolder.mStateTV, teamIssue.getIssueStateFaTextId());

        if (teamIssue.getState().equals("closed")
                || teamIssue.getState().equals("accepted")) {
            ViewUtils.setTextViewLineFlag(viewHolder.mTitleTV, Paint.STRIKE_THRU_TEXT_FLAG
                    | Paint.ANTI_ALIAS_FLAG);
        } else {
            ViewUtils.setTextViewLineFlag(viewHolder.mTitleTV, 0 | Paint.ANTI_ALIAS_FLAG);
        }
    }

    private void setIssueSource(ViewHolder viewHolder, TeamIssue teamIssue) {
        String source = teamIssue.getSource();
        if (TextUtils.isEmpty(source))
            return;
        if (source.equalsIgnoreCase(TeamIssue.TEAM_ISSUE_SOURCE_GITOSC)) {
            // 来自gitosc
            TypefaceUtils.setTypeface(viewHolder.mSourceTV, R.string.fa_gitosc);
        } else if (source.equalsIgnoreCase(TeamIssue.TEAM_ISSUE_SOURCE_GITHUB)) {
            // 来自github
            TypefaceUtils.setTypeface(viewHolder.mSourceTV, R.string.fa_github);
        } else {
            // 来自teamosc
            TypefaceUtils.setTypeface(viewHolder.mSourceTV, R.string.fa_team);
        }
    }

    private String getDeadlineTime(TeamIssue teamIssue) {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = TimeUtils.toDate(teamIssue.getUpdateTime(), dataFormat);
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    static class ViewHolder {
        TextView mMainTitleTV;
        TextView mStateTV;
        TextView mTitleTV;
        TextView mSourceTV;
        TextView mProjectTV;
        TextView mAttachmentsTV;
        TextView mChildsTV;
        TextView mRelationsTV;
        TextView mAcceptTimeTV;
        TextView mAuthorTV;
        TextView mAssignTV;
        TextView mAssignerTV;
        TextView mTimeTV;
        TextView mCommentCountTV;


        public ViewHolder(View view) {
            mMainTitleTV=(TextView) view.findViewById(R.id.main_title_tv);
            mStateTV=(TextView) view.findViewById(R.id.issue_state_tv);
            mTitleTV=(TextView) view.findViewById(R.id.issue_title_tv);
            mSourceTV=(TextView) view.findViewById(R.id.issue_source_tv);
            mProjectTV=(TextView) view.findViewById(R.id.issue_project_tv);
            mAttachmentsTV=(TextView) view.findViewById(R.id.issue_attachments_tv);
            mChildsTV=(TextView) view.findViewById(R.id.issue_childs_tv);
            mRelationsTV=(TextView) view.findViewById(R.id.issue_relations_tv);
            mAcceptTimeTV=(TextView) view.findViewById(R.id.issue_accept_time_tv);
            mAuthorTV=(TextView) view.findViewById(R.id.issue_author_tv);
            mAssignTV=(TextView) view.findViewById(R.id.issue_assign_tv);
            mAssignerTV=(TextView) view.findViewById(R.id.issue_assign_man_tv);
            mTimeTV=(TextView) view.findViewById(R.id.issue_time_tv);
            mCommentCountTV=(TextView) view.findViewById(R.id.issue_comment_count_tv);
        }
    }

















}
