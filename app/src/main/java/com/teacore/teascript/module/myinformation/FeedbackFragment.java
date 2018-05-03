package com.teacore.teascript.module.myinformation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * 意见反馈Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-10
 */

public class FeedbackFragment extends BaseFragment{

    private EditText mFeedbackET;
    private ImageView mImgAddIV;
    private ImageView mImgClearIV;
    private RadioButton mErrorRB;

    private String imgPath;
    private MenuItem mPubMenuItem;
    private RequestManager mImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mImageLoader= Glide.with(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);

        View view=inflater.inflate(R.layout.fragment_feedback,null);

        initView(view);
        initData();

        return view;
    }

    @Override
    public void initView(View view){
        mFeedbackET=(EditText) view.findViewById(R.id.feedback_et);
        mImgAddIV=(ImageView) view.findViewById(R.id.img_add_iv);
        mImgClearIV=(ImageView) view.findViewById(R.id.img_clear_iv);
        mErrorRB=(RadioButton) view.findViewById(R.id.app_error_rb);


        mFeedbackET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mFeedbackET.getText().length() == 0) {
                    setPubMenuState(false);
                } else {
                    setPubMenuState(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mImgAddIV.setOnClickListener(this);
        mImgClearIV.setOnClickListener(this);
        mErrorRB.setChecked(true);

    }

    @Override
    public void initData(){
        super.initData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_submit, menu);
        mPubMenuItem = menu.findItem(R.id.pub_menu_send);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pub_menu_send:
                final String data = mFeedbackET.getText().toString();
                final String header = String.format("［Android-主站-%s］%s",
                        mErrorRB.isChecked() ? getString(R.string.feedback_app_error) :
                                getString(R.string.feedback_function_suggest), data);
                //待完成......
                break;
        }
        return true;
    }

    //上传反馈信息
    public void upload(String content, File file) {
        final ProgressDialog dialog = DialogUtils.getProgressDialog(getActivity(), "上传中");

        TeaScriptApi.feedback(content, file, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                ResultData resultData = XmlUtils.toBean(ResultData.class, arg2);
                if (resultData != null && resultData.getResult().OK()) {
                    AppContext.showToast("已收到你的建议，谢谢");
                    getActivity().finish();
                } else {
                    onFailure(arg0, arg1, arg2, null);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                AppContext.showToast("网络异常，请稍后重试");
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                setPubMenuState(true);
            }

            @Override
            public void onStart() {
                super.onStart();
                setPubMenuState(false);
                dialog.show();
            }
        });
    }

    private void setPubMenuState(boolean canOption) {
        if (!canOption) {
            mPubMenuItem.setEnabled(false);
            mPubMenuItem.setIcon(R.drawable.actionbar_unsend_icon);
        } else {
            mPubMenuItem.setEnabled(true);
            mPubMenuItem.setIcon(R.drawable.actionbar_send_icon);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_add_iv:
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                } else {
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "选择图片"),
                            ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                }
                break;
            case R.id.img_clear_iv:
                mImgAddIV.setImageResource(R.drawable.image_add_selector);
                imgPath = null;
                mImgClearIV.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null &&
                requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgPath = ImageUtils.getPath(getActivity(), selectedImageUri);
                GlideImageLoader.loadImage(mImageLoader,mImgAddIV,imgPath,R.drawable.loading);
                mImgClearIV.setVisibility(View.VISIBLE);
            }
        }
    }

}
