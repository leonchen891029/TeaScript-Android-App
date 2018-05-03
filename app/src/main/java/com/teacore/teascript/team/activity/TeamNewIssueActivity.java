package com.teacore.teascript.team.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamGit;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.team.bean.TeamIssueCatalogList;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.team.bean.TeamMemberList;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.team.bean.TeamProjectList;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 团队新任务Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-18
 */

public class TeamNewIssueActivity extends BaseActivity{

    private Team mTeam;
    private TeamProject mTeamProject;
    private TeamIssueCatalog mTeamCatalog;
    private MenuItem mSendMenu;

    private EditText mTitleET;
    private TextView mProjectTV;
    private TextView mCatalogTV;
    private TextView mAssignTV;
    private TextView mTimeTV;
    private View mPushRL;
    private TextView mPushTV;
    private CheckBox mPushCB;
    private View mPushLine;
    private AlertDialog mProjectDialog;
    private AlertDialog mCatalogDialog;
    private AlertDialog mToUserDialog;

    private int mYear,mMonth,mDay;

    private String mIssueTimeStr;

    //团队成员
    private List<TeamMember> mToUsers;
    //指派的成员在团队中的ID
    private int mToUserIndex=0;

    private List<TeamProject> mProjects;
    private int mProjectIndex=0;

    private List<TeamIssueCatalog> mCatalogs;
    private int mCatalogIndex=0;


    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    protected int getLayoutId(){
        return R.layout.activity_team_new_issue;
    }

    @Override
    public void initView(){

        mTitleET=(EditText) findViewById(R.id.issue_title_et);
        mProjectTV=(TextView) findViewById(R.id.issue_project_tv);
        mCatalogTV=(TextView) findViewById(R.id.issue_catalog_tv);
        mAssignTV=(TextView) findViewById(R.id.issue_assign_tv);
        mTimeTV=(TextView) findViewById(R.id.issue_time_tv);
        mPushRL=findViewById(R.id.issue_push_rl);
        mPushTV=(TextView) findViewById(R.id.issue_push_tv);
        mPushCB=(CheckBox) findViewById(R.id.issue_push_cb);
        mPushLine=findViewById(R.id.push_line);

        findViewById(R.id.issue_project_rl).setOnClickListener(this);
        findViewById(R.id.issue_catalog_rl).setOnClickListener(this);
        findViewById(R.id.issue_assign_rl).setOnClickListener(this);
        findViewById(R.id.issue_time_rl).setOnClickListener(this);

        mTitleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateMenuState();
            }
        });

        TypefaceUtils.setTypeface((TextView) findViewById(R.id.issue_project_fa_tv));
        TypefaceUtils.setTypeface((TextView) findViewById(R.id.issue_catalog_fa_tv));
        TypefaceUtils.setTypeface((TextView) findViewById(R.id.issue_assign_fa_tv));
        TypefaceUtils.setTypeface((TextView) findViewById(R.id.issue_time_fa_tv));
    }

    private void updateMenuState(){
        if(mTitleET.getText().length()==0){
            mSendMenu.setEnabled(false);
            mSendMenu.setIcon(R.drawable.actionbar_unsend_icon);
        }else{
            mSendMenu.setEnabled(true);
            mSendMenu.setIcon(R.drawable.actionbar_send_icon);
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.issue_project_rl:
            case R.id.issue_catalog_rl:
            case R.id.issue_assign_rl:
                showSelected(view.getId());
                break;
            case R.id.issue_time_rl:
                showIssueDeadlineTime();
                break;
            default:
                break;
        }
    }

    private final int show_project=R.id.issue_project_rl;
    private final int show_catalog=R.id.issue_catalog_rl;
    private final int show_touser=R.id.issue_assign_rl;



    private void showSelected(int showType){
        switch(showType){
            case show_project:
                tryToShowProjectDialog();
                break;
            case show_catalog:
                tryToShowCatalogDialog();
                break;
            case show_touser:
                tryToShowToUserDialog();
                break;
            default:
                break;
        }
    }

    private void showIssueDeadlineTime(){

        final DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            }
        },mYear,mMonth,mDay);

        DialogInterface.OnClickListener mListener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mYear=datePickerDialog.getDatePicker().getYear();
                        mMonth=datePickerDialog.getDatePicker().getMonth();
                        mDay=datePickerDialog.getDatePicker().getDayOfMonth();
                        mIssueTimeStr=mYear+"-"+(mMonth+1)+"-"+mDay;
                        mTimeTV.setText(mIssueTimeStr);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        mIssueTimeStr="";
                        mTimeTV.setText(mIssueTimeStr);
                        break;
                    default:
                        break;
                }
            }
        };

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确认",mListener);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",mListener);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"清除",mListener);

        datePickerDialog.show();
    }

    private void tryToShowProjectDialog(){
        if(mProjects!=null){
           showTeamProjectSelected(mProjects);
        }else{
            TeaScriptTeamApi.getTeamProjectList(mTeam.getId(),new MyInfoHandler(show_project));
        }
    }

    private void tryToShowCatalogDialog(){
        TeaScriptTeamApi.getTeamProjectIssueList(AppContext.getInstance().getLoginUid(),
                mTeam.getId(),mTeamProject.getGit().getId(),mTeamProject.getSource(),new MyInfoHandler(show_catalog));
    }

    private void tryToShowToUserDialog(){
        TeaScriptTeamApi.getTeamProjectMemberList(mTeam.getId(),mTeamProject,new MyInfoHandler(show_touser));
    }

    public class MyInfoHandler extends AsyncHttpResponseHandler{

        private int showType=show_project;

        public MyInfoHandler(int showType){
            this.showType=showType;
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onStart() {
            super.onStart();
            showWaitDialog("获取中...");
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            showFaile();
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            switch (showType) {
                // 显示项目选择对话框
                case show_project:
                    TeamProjectList plist = XmlUtils.toBean(TeamProjectList.class,
                            arg2);
                    if (plist == null) {
                        showFaile();
                        return;
                    }
                    showTeamProjectSelected(plist.getList());
                    break;
                // 显示任务列表选择对话框
                case show_catalog:
                    TeamIssueCatalogList clist = XmlUtils.toBean(
                            TeamIssueCatalogList.class, arg2);
                    if (clist == null) {
                        showFaile();
                        return;
                    }
                    showTeamCatalogSelected(clist.getList());
                    break;
                // 显示指派用户对话框
                case show_touser:
                    TeamMemberList tpmList = XmlUtils.toBean(TeamMemberList.class,
                            arg2);
                    if (tpmList == null) {
                        showFaile();
                        return;
                    }
                    showIssueToUser(tpmList.getList());
                    break;
                default:
                    break;
            }
        }

        private void showFaile() {
            AppContext.showToast("获取失败");
        }

    }

    @Override
    public void initData(){
        Bundle args=getIntent().getExtras();
        if(args!=null){
            mTeam=(Team) args.getSerializable("team");
            mTeamProject=(TeamProject) args.getSerializable("project");
            mTeamCatalog=(TeamIssueCatalog) args.getSerializable("catalog");
        }

        if(mTeamProject!=null&&mTeamProject.getGit().getId()!=0&&mTeamProject.getGit().getId()!=-1){
            mProjectTV.setText(mTeamProject.getGit().getName());
            mProjectTV.setTag(mTeamProject);
        }else{
            TeamProject project=new TeamProject();
            TeamGit git=new TeamGit();
            project.setSource("");
            git.setId(-1);
            git.setName("不指定项目");
            project.setGit(git);
            mTeamProject=project;
        }

        //是否要显示发布布局
        isShowPush();

        if(mTeamCatalog!=null){
            mCatalogTV.setText(mTeamCatalog.getTitle());
            mCatalogTV.setTag(mTeamCatalog);
        }

        Calendar calendar=Calendar.getInstance();
        this.mYear=calendar.get(Calendar.YEAR);
        this.mMonth=calendar.get(Calendar.MONTH);
        this.mDay=calendar.get(Calendar.DATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_team_new_issue,menu);
        mSendMenu=menu.findItem(R.id.team_new_issue_pub);
        updateMenuState();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.team_new_issue_pub:
                pubNewIssue();
                break;
            default:
                break;
        }
        return false;
    }

    private AsyncHttpResponseHandler mHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Result result= XmlUtils.toBean(ResultData.class,bytes).getResult();
            if(result.OK()){
                AppContext.showToast(result.getMessage());
            }else{
                AppContext.showToast(result.getMessage());
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }

        @Override
        public void onFinish(){
            hideWaitDialog();
        }

        @Override
        public void onStart(){
            showWaitDialog("发布中...");
        }
    };

    private void pubNewIssue(){

        String titleStr=mTitleET.getText().toString();
        if(TextUtils.isEmpty(titleStr)){
            AppContext.showToast("请填写任务标题");
            return;
        }

        RequestParams params=new RequestParams();
        params.put("teamid",mTeam.getId());
        params.put("uid",AppContext.getInstance().getLoginUid());
        params.put("title",titleStr);

        if(mTeamProject.getGit().getId()>0){
            params.put("project",mTeamProject.getGit().getId());
            params.put("source",mTeamProject.getSource());
            if(mPushCB.isChecked()&&mTeamProject.isGitpush()){
                params.put("gitpush", TeamIssue.TEAM_ISSUE_GITPUSHED);
            }
        }

        if(!TextUtils.isEmpty(mIssueTimeStr)){
            params.put("deadline_time",mIssueTimeStr);
        }

        if(mTeamCatalog!=null){
            params.put("catalogid",mTeamCatalog.getId());
        }

        if(mToUserIndex!=0 && mToUsers!=null && !mToUsers.isEmpty()){
            params.put("to_user",mToUsers.get(mToUserIndex).getId());
        }

        TeaScriptTeamApi.pubTeamNewIssue(params,mHandler);
    }

    private void showTeamProjectSelected(List<TeamProject> projects){

        if(mProjects==null){
            TeamProject project=new TeamProject();
            TeamGit git=new TeamGit();
            git.setId(-1);
            git.setName("不指定项目");
            project.setGit(git);
            projects.add(0,project);
            mProjects=projects;
        }

        final String[] arrays=new String[projects.size()];

        for(int i=0;i<projects.size();i++){

            arrays[i]=projects.get(i).getGit().getName();

            if(mTeamProject!=null){
                if(mTeamProject.getGit().getName().equals(projects.get(i).getGit().getName())
                        &&mTeamProject.getGit().getId()==projects.get(i).getGit().getId()){
                    mProjectIndex=i;
                }
            }
        }

        mProjectDialog= DialogUtils.getSingleChoiceDialog(this, "指定项目", arrays, mProjectIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==mProjectIndex){
                    mProjectDialog.dismiss();
                    return;
                }
                mProjectIndex = which;
                mProjectTV.setText(arrays[which]);
                mTeamProject = mProjects.get(which);
                isShowPush();
                clearCatalogAndToUser();
                mProjectDialog.dismiss();
            }
        }).show();

    }

    private void showTeamCatalogSelected(final List<TeamIssueCatalog> catalogs){

        mCatalogs=catalogs;

        final String[] arrays=new String[catalogs.size()];

        for(int i=0;i<catalogs.size();i++){
            arrays[i]=catalogs.get(i).getTitle();
            if(mTeamCatalog!=null){
                if(mTeamCatalog.getTitle().equals(catalogs.get(i).getTitle())){
                    mCatalogIndex=i;
                }
            }
        }

        mCatalogDialog=DialogUtils.getSingleChoiceDialog(this, "指定任务列表", arrays, mCatalogIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCatalogIndex=which;
                mTeamCatalog=catalogs.get(which);
                mCatalogTV.setText(arrays[which]);
                mCatalogDialog.dismiss();
            }
        }).show();

    }

    private void showIssueToUser(List<TeamMember> toUsers){
        TeamMember member=new TeamMember();
        member.setId(-1);
        member.setName("未指派");
        toUsers.add(0,member);
        mToUsers=toUsers;
        final String[] arrays=new String[toUsers.size()];
        for(int i=0;i<toUsers.size();i++){
            arrays[i]=toUsers.get(i).getName();
        }

        mToUserDialog=DialogUtils.getSingleChoiceDialog(this, "指派成员", arrays, mToUserIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mToUserIndex=which;
                mAssignTV.setText(arrays[which]);
                mToUserDialog.dismiss();
            }
        }).show();

    }

    //重新选定项目之后清空任务列表和指派成员
    private void clearCatalogAndToUser(){
        //清除任务列表
        mCatalogIndex=0;
        mCatalogs=null;
        mCatalogTV.setText("未指定列表");
        //清除指派列表
        mToUserIndex=0;
        mToUsers=null;
        mAssignTV.setText("未指派");
    }

    private void isShowPush(){
        if(mTeamProject==null){
            return;
        }
        if(mTeamProject.getGit().getId()==-1||!mTeamProject.isGitpush()){
            mPushRL.setVisibility(View.GONE);
            mPushLine.setVisibility(View.GONE);
        }else{
            mPushRL.setVisibility(View.VISIBLE);
            mPushLine.setVisibility(View.VISIBLE);
            if(mTeamProject.getSource().equals(TeamProject.GITHUB)){
                mPushTV.setText("同步到GitHub");
            }else{
                mPushTV.setText("同步到Git@TeaScript");
            }
        }
    }




















}
