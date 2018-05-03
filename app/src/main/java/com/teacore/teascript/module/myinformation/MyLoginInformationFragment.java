package com.teacore.teascript.module.myinformation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.MyInformation;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.FileUtils;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 登录信息Fragment界面(MyInformationFragment点击UserAvatar)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-8
 */

public class MyLoginInformationFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{

    public static final int ACTION_TYPE_ALBUM=0;
    public static final int ACTION_TYPE_PHOTO=1;

    private AvatarView mUserAV;
    private TextView mNameTV;
    private TextView mJoinTimeTV;
    private TextView mLocationTV;
    private TextView mPlatformTV;
    private TextView mFocusTV;
    private Button mLogoutBtn;
    private EmptyLayout mEmptyLayout;

    private User mUser;
    private boolean isChangeAvatar=false;
    private final static int CROP=200;

    private static final  String AVATAR_SAVEPATH= Environment
            .getExternalStorageDirectory().getAbsolutePath()+"/teascript/AVATAR/";

    private RequestManager mImageLoader;
    private Uri originalUri;
    private File avatarFile;
    private Bitmap avatarBitmap;
    private String avatarPath;

    private final AsyncHttpResponseHandler mHandler=new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            MyInformation myInformation= XmlUtils.toBean(MyInformation.class,new ByteArrayInputStream(bytes));
            if(myInformation!=null && myInformation.getUser()!=null){
                mUser=myInformation.getUser();
                fillUI();
            }else{
                onFailure(i,headers,bytes,null);
            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mImageLoader= Glide.with(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_login_information,container,false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view){
        mUserAV=(AvatarView) view.findViewById(R.id.user_av);
        mNameTV=(TextView) view.findViewById(R.id.name_tv);
        mJoinTimeTV=(TextView) view.findViewById(R.id.join_time_tv);
        mLocationTV=(TextView) view.findViewById(R.id.location_tv);
        mPlatformTV=(TextView) view.findViewById(R.id.development_platform_tv);
        mFocusTV=(TextView) view.findViewById(R.id.academic_focus_tv);
        mLogoutBtn=(Button) view.findViewById(R.id.logout_btn);
        mEmptyLayout=(EmptyLayout) view.findViewById(R.id.empty_layout);

        mUserAV.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestData();
            }
        });

    }

    @Override
    public void initData(){
        requestData();
    }

    public void requestData(){
        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        TeaScriptApi.getMyInformation(AppContext.getInstance().getLoginUid(),
                mHandler);
    }

    public void fillUI(){
        GlideImageLoader.loadImage(mImageLoader,mUserAV,mUser.getPortrait(),R.drawable.widget_dface);
        mJoinTimeTV.setText(TimeUtils.friendly_time(mUser.getJoinTime()));
        mLocationTV.setText(mUser.getFrom());
        mPlatformTV.setText(mUser.getDevPlatform());
        mFocusTV.setText(mUser.getExpertise());
    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.user_av:
                showAvatarOperation();
                break;
            case R.id.logout_btn:
                AppContext.getInstance().logout();
                AppContext.showToast(R.string.tip_logout_success);
                getActivity().finish();
                break;
            default:
                break;
        }

    }

    public void showAvatarOperation(){

        if(mUser==null){
            AppContext.showToast("");
            return;
        }

        DialogUtils.getSelectDialog(getActivity(), "选择操作", getResources().getStringArray(R.array.avatar_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    handleSelectPicture();
                }else{
                    UiUtils.showUserAvatar(getActivity(),mUser.getPortrait());
                }
            }
        }).show();

    }

    private void handleSelectPicture() {
        DialogUtils.getSelectDialog(getActivity(), "选择图片", getResources().getStringArray(R.array.choose_picture), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToSelectPicture(i);
            }
        }).show();
    }

    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_ALBUM:
                startImagePick();
                break;
            case ACTION_TYPE_PHOTO:
                startTakePhotoByPermissions();
                break;
            default:
                break;
        }
    }

    //选择图片裁剪
    private void startImagePick() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }

    private void startTakePhoto() {

        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/teascript/Camera/";
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(savePath)) {
            AppContext.showToast("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "ts_" + timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        originalUri = uri;

        String theLarge = savePath + fileName;

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(AVATAR_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            AppContext.showToast("无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNotStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(getActivity(), uri);
        }

        String ext = FileUtils.getFileFormat(thePath);
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "osc_crop_" + timeStamp + "." + ext;

        // 裁剪头像的绝对路径
        avatarPath = AVATAR_SAVEPATH + cropFileName;
        avatarFile = new File(avatarPath);

        return Uri.fromFile(avatarFile);
    }

    //拍照后裁剪
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    //上传新照片
    private void uploadNewPhoto() {

        showWaitDialog("正在上传头像...");

        // 获取头像缩略图
        if (!StringUtils.isEmpty(avatarPath) && avatarFile.exists()) {
            avatarBitmap = ImageUtils.loadImgThumbnail(avatarPath, 200, 200);
        } else {
            AppContext.showToast("图像不存在，上传失败");
        }

        if (avatarBitmap != null) {

            try {
                TeaScriptApi.updatePortrait(AppContext.getInstance().getLoginUid(), avatarFile, new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int arg0, Header[] arg1,
                                                  byte[] arg2) {
                                Result result = XmlUtils.toBean(ResultData.class, new ByteArrayInputStream(arg2)).getResult();
                                if (result.OK()) {
                                    AppContext.showToast("更换成功");
                                    // 显示新头像
                                    mUserAV.setImageBitmap(avatarBitmap);
                                    isChangeAvatar = true;
                                } else {
                                    AppContext.showToast(result.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(int arg0, Header[] arg1,
                                                  byte[] arg2, Throwable arg3) {
                                AppContext.showToast("更换头像失败");
                            }

                            @Override
                            public void onFinish() {
                                hideWaitDialog();
                            }
                        });
            } catch (FileNotFoundException e) {
                AppContext.showToast("图像不存在，上传失败");
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(originalUri);// 拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                startActionCrop(imageReturnIntent.getData());// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                uploadNewPhoto();
                break;
        }
    }

    private static final int CAMERA_PERM = 1;

    @AfterPermissionGranted(CAMERA_PERM)
    private void startTakePhotoByPermissions() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this.getContext(), perms)) {
            try {
                startTakePhoto();
            } catch (Exception e) {
                Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.permissions_request_camera),
                    CAMERA_PERM, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        try {
            startTakePhoto();
        } catch (Exception e) {
            Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
