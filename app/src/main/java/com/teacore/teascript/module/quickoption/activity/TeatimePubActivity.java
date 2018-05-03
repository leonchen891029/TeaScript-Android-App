package com.teacore.teascript.module.quickoption.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.service.TeatimeTaskUtils;
import com.teacore.teascript.util.BitmapUtils;
import com.teacore.teascript.util.FileUtils;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.EmojiIcon;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;
import com.teacore.teascript.widget.emoji.EmojiKeyboardFragment;
import com.teacore.teascript.widget.emoji.OnEmojiClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.teacore.teascript.widget.dialog.DialogUtils.getConfirmDialog;

/**
 * 动弹发布的Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-21
 */

public class TeatimePubActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    private static final int MAX_TEXT_LENGTH=160;
    private static final int SELECT_FRIENDS_REQUEST_CODE=100;
    private static final String TEXT_SOFTWARE="#请输入软件名#";


    public static final String ACTION_TYPE = "action_type";
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    public static final int ACTION_TYPE_RECORD = 2; // 录音
    public static final int ACTION_TYPE_TOPIC = 3; // 话题
    public static final int ACTION_TYPE_REPOST = 4; // 转发

    public static final String REPOST_IMAGE_KEY = "repost_image";
    public static final String REPOST_TEXT_KEY = "Teatime_topic";

    private ImageButton mEmojiIB;
    private ImageButton mPictureIB;
    private ImageButton mMetionIB;
    private ImageButton mSoftwareIB;
    private TextView mClearTV;
    private View mImageRL;
    private ImageView mCleanIV;
    private ImageView mImgIV;
    private EditText mInputET;

    private MenuItem mSendMenu;
    private Teatime teatime=new Teatime();

    private final EmojiKeyboardFragment keyboardFragment=new EmojiKeyboardFragment();

    @Override
    protected int getLayoutId(){
        int mode= WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getWindow().setSoftInputMode(mode);

        return R.layout.activity_teatime_pub;
    }

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    public void initView(){
        respondExternal(getIntent());

        mInputET=(EditText) findViewById(R.id.content_et);
        mImageRL=findViewById(R.id.img_rl);
        mImgIV=(ImageView) findViewById(R.id.img_iv);
        mCleanIV=(ImageView) findViewById(R.id.clear_iv);
        mClearTV=(TextView) findViewById(R.id.clear_tv);
        mPictureIB=(ImageButton) findViewById(R.id.picture_ib);
        mMetionIB=(ImageButton) findViewById(R.id.mention_ib);
        mSoftwareIB=(ImageButton) findViewById(R.id.trend_software_ib);
        mEmojiIB=(ImageButton) findViewById(R.id.emoji_keyboard_ib);

        mPictureIB.setOnClickListener(this);
        mMetionIB.setOnClickListener(this);
        mSoftwareIB.setOnClickListener(this);
        mEmojiIB.setOnClickListener(this);
        mClearTV.setOnClickListener(this);
        mClearTV.setText(String.valueOf(MAX_TEXT_LENGTH));
        mCleanIV.setOnClickListener(this);

        mInputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mClearTV.setText((MAX_TEXT_LENGTH - charSequence.length()) + "");
                updateMenuState(mSendMenu);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ("@".equals(editable.toString())) {
                    toSelectFriends();
                }
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_keyboard_fragment,keyboardFragment)
                .commit();

        keyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(EmojiIcon v) {
                EmojiInputUtils.input2OSC(mInputET,v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                EmojiInputUtils.backspace(mInputET);
            }
        });

    }
    //从App外部分享进入的事件
    private void respondExternal(Intent intent){
        String action=intent.getAction();
        String type=intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                teatime.setBody(sharedText);
            } else if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                String path = getAbsoluteImagePath(imageUri);
                teatime.setImageFilePath(path);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent
                        .getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                String path = getAbsoluteImagePath(imageUris.get(0));
                teatime.setImageFilePath(path);
            }
        }

    }

    //处理从App内部跳转过来的事件
    @Override
    public void initData(){
        int action=getIntent().getIntExtra(ACTION_TYPE,-1);
        selectActive(action);

        if(TextUtils.isEmpty(teatime.getBody())){
            teatime.setBody(AppContext.getTeatimeDraft());
            AppContext.setTeatimeDraft(null);
            mInputET.setSelection(mInputET.getText().toString().length());
        }

        mInputET.setText(teatime.getBody());
        String imagePath=teatime.getImageFilePath();

        if(!TextUtils.isEmpty(imagePath)){
            if(imagePath.startsWith("http")){
                setImageFromUrl(imagePath);
            }else{
                setImageFromPath(imagePath);
            }
        }

    }

    @Override
    public void onClick(View view){

        switch(view.getId()){
            case R.id.picture_ib:
                toSelectPicture();
                break;
            case R.id.mention_ib:
                toSelectFriends();
                break;
            case R.id.trend_software_ib:
                insertTrendSoftware();
                break;
            case R.id.clear_tv:
                mInputET.setText(null);
                break;
            case R.id.clear_iv:
                mImgIV.setImageBitmap(null);
                mImageRL.setVisibility(View.GONE);
                teatime.setImageFilePath("");
                break;
            case R.id.emoji_keybord_ib:
                if(!keyboardFragment.isShow()){
                    keyboardFragment.showEmojiKeyBoard();
                    keyboardFragment.hideSoftKeyboard();
                }else{
                    keyboardFragment.hideEmojiKeyboard();
                    keyboardFragment.showSoftKeyboard(mInputET);
                }
                break;
        }
    }

    private void toSelectPicture(){
        DialogUtils.getSelectDialog(this, getResources().getStringArray(R.array.choose_picture), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectActive(i);
            }
        }).show();
    }

    private void toSelectFriends(){
        if(!AppContext.getInstance().isLogin()){
            UiUtils.showLoginActivity(this);
            return;
        }
        Intent intent=new Intent(this,SelectFriendsActivity.class);
        startActivityForResult(intent,SELECT_FRIENDS_REQUEST_CODE);
    }

    private void insertTrendSoftware(){
        int curTextLength=mInputET.getText().length();
        if(curTextLength>=MAX_TEXT_LENGTH)
            return;

        String software=TEXT_SOFTWARE;
        int start,end;
        if ((MAX_TEXT_LENGTH - curTextLength >= software.length())) {
          start=mInputET.getSelectionStart()+1;
            end=start+software.length()-2;
        }else {
            int num = MAX_TEXT_LENGTH - curTextLength;
            if (num < software.length()) {
                software = software.substring(0, num);
            }
            start = mInputET.getSelectionStart() + 1;
            end = start + software.length() - 1;
        }
        if (start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
            start = MAX_TEXT_LENGTH;
            end = MAX_TEXT_LENGTH;
        }
        mInputET.getText().insert(mInputET.getSelectionStart(), software);
        mInputET.setSelection(start, end);
    }

    //根据不同的type，选择不同的图片
    private void selectActive(int type){
        Bundle bundle;
        switch(type){
            case ACTION_TYPE_ALBUM:
                showToAlbum();
                break;
            case ACTION_TYPE_PHOTO:
                showToCamera();
                break;
            //两种case同样处理
            case ACTION_TYPE_TOPIC:
            case ACTION_TYPE_REPOST:
                bundle=getIntent().getExtras();
                if(bundle!=null){
                    String sharedText=bundle.getString(REPOST_TEXT_KEY);
                    String sharedImage=bundle.getString(REPOST_IMAGE_KEY);
                    teatime.setBody(sharedText);
                    teatime.setImageFilePath(sharedImage);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode==SELECT_FRIENDS_REQUEST_CODE){
            //选中的好友的名字
            String names[]=data.getStringArrayExtra("names");
            if(names!=null&&names.length>0){
                //拼成字符串
                String text="";
                for(String name:names){
                    text+="@"+name+" ";
                }
                //插入到文本中
                mInputET.getText().insert(mInputET.getSelectionStart(), text);
            }
            return;
        }
        if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
            if (data == null)
                return;
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                String path = ImageUtils.getPath(this,selectedImageUri);
                setImageFromPath(path);
            }
        } else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
            setImageFromPath(teatime.getImageFilePath());
        }
    }

    private void setContentText(String topic){
        setContentText(topic,topic.length());
    }

    private void setContentText(String topic,int selectIndex){
        if(mInputET!=null){
            mInputET.setText(topic);
            mInputET.setSelection(selectIndex);
        }
    }

    //根据url上传图片
    private void setImageFromUrl(final String url) {
        if (TextUtils.isEmpty(url)) return;
        mCleanIV.setVisibility(View.GONE);
        mImageRL.setVisibility(View.VISIBLE);

        Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                setImageFromBitmap(resource);
            }
        });

        mCleanIV.setVisibility(View.VISIBLE);

    }

    //根据文件路径上传动弹图片
    private void setImageFromPath(final String path) {
        if (TextUtils.isEmpty(path)) return;
        try {
            Bitmap bitmap = BitmapUtils.bitmapFromStream(
                    new FileInputStream(path), 512, 512);

            setImageFromBitmap(bitmap);
            // 本地图片在这里销毁
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //根据bitmap上传动弹图片
    private void setImageFromBitmap(final Bitmap bitmap) {
        if (bitmap == null) return;
        String temp = FileUtils.getSDRoot() + "/teascript/tempfile.png";
        FileUtils.bitmapToFile(bitmap, temp);
        teatime.setImageFilePath(temp);

        // 压缩小图片用于界面显示
        Bitmap minBitmap = ImageUtils.zoomBitmap(bitmap, 100, 100);
        // 销毁之前的图片
        // 这里销毁会导致动弹界面图片无法重新预览,KJ框架问题
        // bitmap.recycle();

        mImgIV.setImageBitmap(minBitmap);
        mImageRL.setVisibility(View.VISIBLE);
    }

    private final int RC_CAMERA_PERMISSION=123;
    private final int RC_ALBUM_PERMISSION=124;

    //进入相机
    @AfterPermissionGranted(RC_CAMERA_PERMISSION)
    private void showToCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toCamera();
        } else {
            EasyPermissions.requestPermissions(this, "", RC_CAMERA_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void toCamera() {
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/teascript/Camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            AppContext.showToast("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);

        teatime.setImageFilePath(savePath + fileName); // 该照片的绝对路径

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    //进入图库
    @AfterPermissionGranted(RC_ALBUM_PERMISSION)
    private void showToAlbum() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toAlbum();
        } else {
            EasyPermissions.requestPermissions(this, "", RC_ALBUM_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void toAlbum() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_pub_topic,menu);
        mSendMenu=menu.findItem(R.id.pub_topic_menu);
        updateMenuState(mSendMenu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pub_topic_menu:
                pubTeatime();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //发送动弹
    private void pubTeatime() {
        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(this);
            return;
        }
        String content = mInputET.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            mInputET.requestFocus();
            AppContext.showToast(R.string.tip_content_empty);
            return;
        }
        if (content.length() > MAX_TEXT_LENGTH) {
            AppContext.showToast(R.string.tip_content_too_long);
            return;
        }
        if (teatime == null) teatime = new Teatime();

        teatime.setAuthorId(AppContext.getInstance().getLoginUid());
        teatime.setBody(content);
        TeatimeTaskUtils.pubTeatime(this, teatime);
        finish();
    }

    //更新菜单图标
    private void updateMenuState(MenuItem menuItem){
        if(menuItem==null||mInputET==null){
            return;
        }
        if (mInputET.getText().length() == 0) {
            menuItem.setEnabled(false);
            menuItem.setIcon(R.drawable.actionbar_unsend_icon);
        } else {
            menuItem.setEnabled(true);
            menuItem.setIcon(R.drawable.actionbar_send_icon);
        }
    }

    private String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getBaseContext().getContentResolver().query(uri,proj,null,null,null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } else {
            // 如果游标为空说明获取的已经是绝对路径了
            return uri.getPath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        int tipres = R.string.pub_teatime_required_album_tip;
        if (perms.get(0).equals(Manifest.permission.CAMERA)) {
            tipres = R.string.pub_teatime_required_camera_tip;
        } else if (perms.get(0).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            tipres = R.string.pub_teatime_required_album_tip;
        }
        String tip = getString(tipres);
        // 权限被拒绝了
        DialogUtils.getConfirmDialog(this,
                "权限申请",
                tip,
                "去设置",
                "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                },
                null).show();

    }

    @Override
    public void onBackPressed(){
        final String TeatimeString=mInputET.getText().toString();
        if(!TextUtils.isEmpty(TeatimeString)){
            getConfirmDialog(this, "是否保存为草稿?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    AppContext.setTeatimeDraft(TeatimeString);
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        }else{
            super.onBackPressed();
        }
    }


}
