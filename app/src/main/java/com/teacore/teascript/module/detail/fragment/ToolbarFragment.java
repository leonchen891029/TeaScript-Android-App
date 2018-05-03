package com.teacore.teascript.module.detail.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;

/**Toolbar Fragment,导航栏Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-21
 */

public class ToolbarFragment extends BaseFragment{

    public interface OnActionClickListener{
        void onActionClick(ToolAction action);
    }

    public enum ToolAction{
        ACTION_CHANGE,ACTION_WRITE_COMMENT,ACTION_VIEW_COMMENT,ACTION_FAVORITE,ACTION_SHARE,ACTION_REPORT
    }

    private OnActionClickListener mActionListener;

    private View mActionWriteComment,mActionViewComment,mActionFavorite,mActionReport,mActionShare;

    private View favoriteIV;
    private boolean mFavorite;

    private TextView commentCountTV;
    private int mCommentCount;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        if (rootView!=null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent!=null) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_toolbar, container, false);
            initView(rootView);
        }
        return rootView;
    }



    public View getRootView(){
        return rootView;
    }

    @Override
    public void initView(View view){
        view.findViewById(R.id.text_btn).setOnClickListener(this);

        mActionViewComment=view.findViewById(R.id.view_comment_layout);
        mActionWriteComment=view.findViewById(R.id.write_comment_layout);
        mActionFavorite=view.findViewById(R.id.favorite_layout);
        mActionShare=view.findViewById(R.id.share_layout);
        mActionReport=view.findViewById(R.id.report_layout);

        mActionViewComment.setOnClickListener(this);
        mActionWriteComment.setOnClickListener(this);
        mActionFavorite.setOnClickListener(this);
        mActionShare.setOnClickListener(this);
        mActionReport.setOnClickListener(this);

        favoriteIV=mActionFavorite.findViewById(R.id.favorite_iv);
        favoriteIV.setSelected(mFavorite);

        commentCountTV=(TextView) mActionViewComment.findViewById(R.id.comment_count_tv);
        commentCountTV.setText(String.valueOf(mCommentCount));

    }


    @Override
    public void onClick(View v) {

        final int id = v.getId();

        ToolAction action = null;

        if (id == R.id.text_btn) {
            action = ToolAction.ACTION_CHANGE;
        } else if (id == R.id.write_comment_layout) {
            action = ToolAction.ACTION_WRITE_COMMENT;
        } else if (id == R.id.view_comment_layout) {
            action = ToolAction.ACTION_VIEW_COMMENT;
        } else if (id == R.id.share_layout) {
            action = ToolAction.ACTION_SHARE;
        } else if (id == R.id.report_layout) {
            action = ToolAction.ACTION_REPORT;
        } else if (id == R.id.favorite_layout) {
            action = ToolAction.ACTION_FAVORITE;
        }

        if (action != null && mActionListener != null) {
            mActionListener.onActionClick(action);
        }

    }

    public void setOnActionClickListener(OnActionClickListener listener){
        mActionListener=listener;
    }

    public void setCommentCount(int count) {
        mCommentCount = count;
        if (commentCountTV != null) {
            commentCountTV.setText(String.valueOf(mCommentCount));
            commentCountTV.setVisibility(mCommentCount > 0 ? View.VISIBLE
                    : View.GONE);
        }
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
        if (favoriteIV != null) {
            favoriteIV.setSelected(favorite);
        }
    }

    public void showReportButton() {
        mActionReport.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
    }



}
