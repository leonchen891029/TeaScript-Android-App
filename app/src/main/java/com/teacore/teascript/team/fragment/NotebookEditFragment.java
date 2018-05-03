package com.teacore.teascript.team.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.Notebook;
import com.teacore.teascript.database.NotebookDatabase;
import com.teacore.teascript.module.back.BackActivity;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.util.AnimationsUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.dialog.DialogUtils;

/**
 * 编辑便签fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-2
 */

public class NotebookEditFragment extends BaseFragment implements View.OnTouchListener{

    private EditText mContentET;
    private RelativeLayout mMenuRL;
    private ImageView mGreenIV;
    private ImageView mBlueIV;
    private ImageView mPurpleIV;
    private ImageView mYellowIV;
    private ImageView mRedIV;

    private RelativeLayout mTitleRL;
    private TextView mDateTV;
    private ImageView mEditIV;
    private ImageView mThumbtackIV;

    private Notebook mEditData;
    private NotebookDatabase mNotebookDB;
    private boolean isNewNotebook;
    //从哪个界面来
    private int whereFrom=QUICK_DIALOG;

    public static final String NOTEBOOK_KEY="notebook_key";
    public static final String NOTEBOOK_WHEREFROM_KEY="notebook_wherefrom_key";
    public static final int QUICK_DIALOG = 0;
    public static final int NOTEBOOK_FRAGMENT = 1;
    public static final int NOTEBOOK_ITEM = 2;

    public static final int[] backGrounds = { 0xffe5fce8,// 绿色
            0xffccf2fd,// 蓝色
            0xfff7f5f6,// 紫色
            0xfffffdd7,// 黄色
            0xffffddde,// 红色
    };
    public static final int[] titleBackGrounds = { 0xffcef3d4,// 绿色
            0xffa9d5e2,// 蓝色
            0xffddd7d9,// 紫色
            0xffebe5a9,// 黄色
            0xffecc4c3,// 红色
    };

    public static final int[] thumbtackImgs = { R.drawable.green, R.drawable.blue, R.drawable.purple,R.drawable.yellow, R.drawable.red};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notebook_edit,
                container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mContentET=(EditText) view.findViewById(R.id.notebook_edit_et);
        mMenuRL=(RelativeLayout) view.findViewById(R.id.notebook_edit_menu);
        mGreenIV=(ImageView) view.findViewById(R.id.notebook_edit_green_ncv);
        mBlueIV=(ImageView) view.findViewById(R.id.notebook_edit_blue_ncv);
        mPurpleIV=(ImageView) view.findViewById(R.id.notebook_edit_purple_ncv);
        mYellowIV=(ImageView) view.findViewById(R.id.notebook_edit_yellow_ncv);
        mRedIV=(ImageView) view.findViewById(R.id.notebook_edit_red_ncv);
        mTitleRL=(RelativeLayout) view.findViewById(R.id.notebook_edit_title_rl);
        mDateTV=(TextView) view.findViewById(R.id.notebook_edit_date_tv);
        mEditIV=(ImageView) view.findViewById(R.id.notebook_edit_iv);
        mThumbtackIV=(ImageView) view.findViewById(R.id.notebook_edit_thumbtack_iv);

        mContentET.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mContentET.setSingleLine(false);
        mContentET.setHorizontallyScrolling(false);
        mContentET.setText(Html.fromHtml(mEditData.getContent()).toString());
        mContentET.setBackgroundColor(backGrounds[mEditData.getColor()]);



        mTitleRL.setBackgroundColor(titleBackGrounds[mEditData.getColor()]);

        mDateTV.setText(mEditData.getDate());

        mThumbtackIV.setImageResource(thumbtackImgs[mEditData.getColor()]);

        mMenuRL.setOnTouchListener(this);
        mGreenIV.setOnClickListener(this);
        mBlueIV.setOnClickListener(this);
        mPurpleIV.setOnClickListener(this);
        mYellowIV.setOnClickListener(this);
        mRedIV.setOnClickListener(this);
        mEditIV.setOnTouchListener(this);
    }

    @Override
    public void initData() {
        mNotebookDB = new NotebookDatabase(getActivity());

        if (mEditData == null) {
            mEditData = new Notebook();
            mEditData.setContent(AppContext.getNoteDraft());
            isNewNotebook = true;
        }

        if (StringUtils.isEmpty(mEditData.getDate())) {
            mEditData.setDate(TimeUtils.getSystemTime("yyyy/MM/dd"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notebook_edit_green_ncv:
                mEditData.setColor(0);
                break;
            case R.id.notebook_edit_blue_ncv:
                mEditData.setColor(1);
                break;
            case R.id.notebook_edit_purple_ncv:
                mEditData.setColor(2);
                break;
            case R.id.notebook_edit_yellow_ncv:
                mEditData.setColor(3);
                break;
            case R.id.notebook_edit_red_ncv:
                mEditData.setColor(4);
                break;
        }

        mThumbtackIV.setImageResource(thumbtackImgs[mEditData.getColor()]);
        mContentET.setBackgroundColor(backGrounds[mEditData.getColor()]);
        mTitleRL.setBackgroundColor(titleBackGrounds[mEditData.getColor()]);
        closeMenu();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (mMenuRL.getVisibility() == View.GONE) {
                openMenu();
            } else {
                closeMenu();
            }
        }
        return true;
    }

    /**
     * 切换便签颜色的菜单
     */
    private void openMenu() {
        AnimationsUtils.openAnimation(mMenuRL, mEditIV, 500);
    }

    /**
     * 切换便签颜色的菜单
     */
    private void closeMenu() { AnimationsUtils.openAnimation(mMenuRL, mEditIV, 500); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getActivity().getIntent().getBundleExtra(
                BackActivity.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            mEditData = (Notebook) bundle.getSerializable(NOTEBOOK_KEY);
            whereFrom = bundle.getInt(NOTEBOOK_WHEREFROM_KEY, QUICK_DIALOG);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notebook_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pub_menu_send:
                if (!StringUtils.isEmpty(mContentET.getText().toString())) {
                    save();
                    if (whereFrom == QUICK_DIALOG) {
                        UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.NOTE);
                    }
                }
                getActivity().finish();
                break;
        }
        return true;
    }

    /**
     * 保存已编辑内容到数据库
     */
    private void save() {
        if (mEditData.getId() == 0) {
            mEditData.setId(-1
                    * StringUtils.toInt(
                    TimeUtils.getSystemTime("dddHHmmss"), 0));
        }
        mEditData.setUnixTime(TimeUtils.getSystemTime("yyyy-MM-dd HH:mm:ss"));
        mEditData.setContent(mContentET.getText().toString());
        mNotebookDB.save(mEditData);
    }

    @Override
    public boolean onBackPressed() {
        if (isNewNotebook) {
            final String content = mContentET.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                DialogUtils.getConfirmDialog(getActivity(), "是否保存为草稿?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppContext.setNoteDraft("");
                        getActivity().finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppContext.setNoteDraft(content);
                        getActivity().finish();
                    }
                }).show();
                return true;
            }
        }
        return super.onBackPressed();
    }


}
