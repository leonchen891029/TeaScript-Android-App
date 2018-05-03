package com.teacore.teascript.team.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.team.bean.TeamMemberList;
import com.teacore.teascript.util.FileUtils;
import com.teacore.teascript.util.ImageUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.EmojiIcon;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;
import com.teacore.teascript.widget.emoji.EmojiKeyboardFragment;
import com.teacore.teascript.widget.emoji.OnEmojiClickListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 团队新动态
 *@author 陈晓帆
 *@version 1.0
 * Created 2017-4-11
 */

public class TeamNewActiveActivity extends BaseActivity {

    private static final int MAX_TEXT_LENGTH=160;
    private static final String TEXT_SOFTWARE="#请输入软件名#";

    private Team mTeam;

    private MenuItem mMenuSend;
    private ImageButton mEmojiIB;
    private ImageButton mPictureIB;
    private ImageButton mMentionIB;
    private ImageButton mTrendSoftwareIB;
    private TextView mClearTV;
    private View mImageRL;
    private ImageView mImageView;
    private ImageView mClearIV;
    private EditText mInputET;
    private final EmojiKeyboardFragment keyboardFragment=new EmojiKeyboardFragment();

    private String theLarge,theThumbnail;
    private File imgFile;

    private final Handler handler=new Handler(){
      @Override
      public void handleMessage(Message msg){
          if(msg.what==1&&msg.obj!=null){
              //显示图片
              mImageView.setImageBitmap((Bitmap) msg.obj);
              mImageRL.setVisibility(View.VISIBLE);
          }
      }
    };

    @Override
    protected int getLayoutId(){
        return R.layout.activity_team_new_active;
    }

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    public void initView(){
        mEmojiIB=(ImageButton) findViewById(R.id.emoji_keyboard_ib);
        mPictureIB=(ImageButton) findViewById(R.id.picture_ib);
        mMentionIB=(ImageButton) findViewById(R.id.mention_ib);
        mTrendSoftwareIB=(ImageButton) findViewById(R.id.trend_software_ib);
        mClearTV=(TextView) findViewById(R.id.clear_tv);
        mImageRL=findViewById(R.id.img_rl);
        mImageView=(ImageView) findViewById(R.id.img_iv);
        mClearIV=(ImageView) findViewById(R.id.clear_iv);
        mInputET=(EditText) findViewById(R.id.content_et);

        mPictureIB.setOnClickListener(this);
        mMentionIB.setOnClickListener(this);
        mTrendSoftwareIB.setOnClickListener(this);
        mEmojiIB.setOnClickListener(this);
        mClearTV.setOnClickListener(this);
        mClearIV.setOnClickListener(this);

        setActionBarTitle(R.string.team_new_active);
        mClearTV.setText(String.valueOf(MAX_TEXT_LENGTH));
        mInputET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSendMenu();
                mClearTV.setText((MAX_TEXT_LENGTH - s.length()) + "");
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.emoji_keyboard_fragment,keyboardFragment);

        keyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(EmojiIcon v) {
                EmojiInputUtils.input2OSC(mInputET, v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                EmojiInputUtils.backspace(mInputET);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        keyboardFragment.hideEmojiKeyboard();
    }

    private void updateSendMenu() {
        if (mInputET.getText().length() == 0) {
            mMenuSend.setEnabled(false);
            mMenuSend.setIcon(R.drawable.actionbar_unsend_icon);
        } else {
            mMenuSend.setEnabled(true);
            mMenuSend.setIcon(R.drawable.actionbar_send_icon);
        }
    }

    @Override
    public void initData() {
        mTeam = (Team) getIntent().getExtras().getSerializable(
                TeamMainActivity.BUNDLE_KEY_TEAM);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pub_new_team_active, menu);
        mMenuSend = menu.findItem(R.id.pub_menu_send);
        updateSendMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pub_menu_send:
                handleSubmit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSubmit() {
        String content = mInputET.getText().toString();
        if (StringUtils.isEmpty(content) || mTeam == null)
            return;
        TeaScriptTeamApi.pubTeamNewActive(mTeam.getId(), content, imgFile,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        Result result = XmlUtils.toBean(ResultData.class, arg2)
                                .getResult();
                        if (result != null) {
                            if (result.OK()) {
                                AppContext.showToast(result.getMessage());
                                finish();
                            } else {
                                AppContext.showToast(result.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        AppContext.showToast("发表失败，请检查下你的网络");
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showWaitDialog("提交中...");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideWaitDialog();
                    }
                });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.emoji_keyboard_ib:
                if (keyboardFragment.isShow()) {
                    keyboardFragment.hideEmojiKeyboard();
                    keyboardFragment.showSoftKeyboard(mInputET);
                } else {
                    keyboardFragment.showEmojiKeyBoard();
                    keyboardFragment.hideSoftKeyboard();
                }
                break;
            case R.id.picture_ib:
                handleSelectPicture();
                break;
            case R.id.mention_ib:
                tryToShowMetionUser();
                break;
            case R.id.trend_software_ib:
                insertTrendSoftware();
                break;
            case R.id.clear_iv:
                mImageView.setImageBitmap(null);
                mImageRL.setVisibility(View.GONE);
                imgFile = null;
                break;
            case R.id.clear_tv:
                handleClearWords();
                break;
            default:
                break;
        }
    }

    private void handleSelectPicture() {
        DialogUtils.getSelectDialog(this, getResources().getStringArray(R.array.choose_picture),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToSelectPicture(i);
                    }
                }).show();
    }

    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;

    private void goToSelectPicture(int position) {
        switch (position) {
            case ACTION_TYPE_ALBUM:
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
            case ACTION_TYPE_PHOTO:
                // 判断是否挂载了SD卡
                String savePath = "";
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/oschina/Camera/";
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                }

                // 没有挂载SD卡，无法保存文件
                if (StringUtils.isEmpty(savePath)) {
                    AppContext.showToast("无法保存照片，请检查SD卡是否挂载");
                    return;
                }

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(new Date());
                String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
                File out = new File(savePath, fileName);
                Uri uri = Uri.fromFile(out);

                theLarge = savePath + fileName;// 该照片的绝对路径

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent,
                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                break;
            default:
                break;
        }
    }


    private void handleClearWords(){
        if(TextUtils.isEmpty(mInputET.getText().toString())){
            return;
        }
        DialogUtils.getConfirmDialog(this, "是否清空内容？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    //在光标所在位置插入TEXT_SOFTWARE
    private void insertTrendSoftware(){
        int curTextLength=mInputET.getText().length();
        if(curTextLength>=MAX_TEXT_LENGTH)
            return;
        String software=TEXT_SOFTWARE;
        int start,end;
        if ((MAX_TEXT_LENGTH - curTextLength) >= software.length()) {
            start = mInputET.getSelectionStart() + 1;
            end = start + software.length() - 2;
        } else {
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

        //设置点击以后选中文字的范围
        mInputET.setSelection(start, end);
    }

    @Override
    public void onBackPressed() {
        if (mInputET.getText().length() != 0) {
            showConfirmExit();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmExit() {
        DialogUtils.getConfirmDialog(this, "是否放弃这次操作?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK)
            return;
        new Thread() {
            private String selectedImagePath;

            @Override
            public void run() {
                Bitmap bitmap = null;
                //请求码是SDCARD IMAGE
                if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                    //如果获取的Intent为null
                    if (imageReturnIntent == null)
                        return;
                    //intent中获取Image的uri
                    Uri selectedImageUri = imageReturnIntent.getData();
                    //如果uri不为null，获取imagePath
                    if (selectedImageUri != null) {
                        selectedImagePath = ImageUtils.getPath(
                                TeamNewActiveActivity.this, selectedImageUri);
                    }
                    //如果ImagePath不为null
                    if (selectedImagePath != null) {
                        theLarge = selectedImagePath;
                    } else {
                        bitmap = ImageUtils.loadPicasaImageFromGalley(
                                 TeamNewActiveActivity.this,selectedImageUri);
                    }
                    //获取imgName，获取缩略图
                    if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
                        String imgName = FileUtils.getFileName(theLarge);
                        bitmap = ImageUtils.loadImgThumbnail(
                                TeamNewActiveActivity.this, imgName,
                                MediaStore.Images.Thumbnails.MICRO_KIND);
                    }
                    if (bitmap == null && !StringUtils.isEmpty(theLarge))
                        bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
                } else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                    // 拍摄图片
                    if (!StringUtils.isEmpty(theLarge)) {
                        bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
                    }
                }

                if (bitmap != null) {
                    // 存放照片的文件夹
                    String savePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/OSChina/Camera/";
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }

                    String largeFileName = FileUtils.getFileName(theLarge);
                    String largeFilePath = savePath + largeFileName;
                    // 判断是否已存在缩略图
                    if (largeFileName.startsWith("thumb_")
                            && new File(largeFilePath).exists()) {
                        theThumbnail = largeFilePath;
                        imgFile = new File(theThumbnail);
                    } else {
                        // 生成上传的800宽度图片
                        String thumbFileName = "thumb_" + largeFileName;
                        theThumbnail = savePath + thumbFileName;
                        if (new File(theThumbnail).exists()) {
                            imgFile = new File(theThumbnail);
                        } else {
                            try {
                                // 压缩上传的图片
                                ImageUtils.createImageThumbnail(
                                        TeamNewActiveActivity.this, theLarge,
                                        theThumbnail, 800, 80);
                                imgFile = new File(theThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }
            }

            ;
        }.start();
    }

    private List<TeamMember> mTeamMemberList;

    private AlertDialog metionUserDialog;

    private void tryToShowMetionUser() {

        //如果team成员的列表为空
        if (mTeamMemberList == null || mTeamMemberList.isEmpty()) {
            TeaScriptApi.getTeamMemberList(mTeam.getId(),
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1,
                                              byte[] arg2) {
                            TeamMemberList memberList = XmlUtils.toBean(
                                    TeamMemberList.class, arg2);
                            if (memberList != null) {
                                mTeamMemberList = memberList.getList();
                                showMetionUser();
                            } else {
                                AppContext.showToast("获取团队成员失败");
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable arg3) {
                            AppContext.showToast("获取团队成员失败");
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            showWaitDialog("正在获取团队成员...");
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            hideWaitDialog();
                        }

                    });
        } else {
            showMetionUser();
        }
    }

    private void showMetionUser() {

        if (mTeamMemberList == null || mTeamMemberList.isEmpty())
            return;

        if (metionUserDialog == null) {

            final String[] toUsers = new String[mTeamMemberList
                    .size() + 1];

            toUsers[0] = "全体成员(all)";

            for (int i = 1; i < toUsers.length; i++) {
                toUsers[i] = mTeamMemberList.get(i - 1).getName();
            }

            metionUserDialog = DialogUtils.getSingleChoiceDialog(this, "艾特团队成员", toUsers, -1, new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mInputET.getText().insert(mInputET.getSelectionStart(),
                                    "@" + toUsers[i] + " ");
                            mInputET.setSelection(mInputET.length());
                            metionUserDialog.dismiss();

                        }
                    }).show();
        }

        metionUserDialog.show();
    }






}
