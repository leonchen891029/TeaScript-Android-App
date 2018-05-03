package com.teacore.teascript.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.util.TDevice;
import com.umeng.socialize.media.UMImage;

/**分享界面Dialog
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-1
 */
public class ShareDialog extends BaseDialog implements View.OnClickListener{

    private Context context;
    private String title;
    private String content;
    private String link;

    //final UMShareAPI。。。待完善

    private ShareDialog(Context context,boolean flag,OnCancelListener listener){
        super(context,flag,listener);
        this.context=context;
    }

    private ShareDialog(Context context,int defStyle){
        super(context,defStyle);
        this.context=context;
        View shareView=View.inflate(context,R.layout.dialog_share_view,null);
        shareView.findViewById(R.id.share_wechat_circle_ll).setOnClickListener(this);
        shareView.findViewById(R.id.share_wechat_ll).setOnClickListener(this);
        shareView.findViewById(R.id.share_sina_weibo_ll).setOnClickListener(this);
        shareView.findViewById(R.id.share_qq_ll).setOnClickListener(this);
        shareView.findViewById(R.id.share_copy_link_ll).setOnClickListener(this);
        shareView.findViewById(R.id.share_more_option_ll).setOnClickListener(this);
        super.setContentView(shareView);
    }

    public ShareDialog(Context context){
        this(context,R.style.dialog_bottom);
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager wm = getWindow().getWindowManager();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = displayMetrics.widthPixels;
        getWindow().setAttributes(lp);
    }

    //设置需要分享的内容
    public void setShareInfo(String title, String content, String link) {
        this.title = title;
        this.content = content;
        this.link = link;
    }

    @Override
    public void onClick(View view){
        if (!checkCanShare()) {
            AppContext.showToast("内容加载中，请稍候...");
            return;
        }
        switch (view.getId()){
            case R.id.share_wechat_circle_ll:
                shareToWechatCircle();
                break;
            case R.id.share_wechat_ll:
                shareToWechat();
                break;
            case R.id.share_sina_weibo_ll:
                shareToSinaweibo();
                break;
            case R.id.share_qq_ll:
                shareToQQ();
                break;
            case R.id.share_copy_link_ll:
                TDevice.copyTextToClipboard(link);
                break;
            case R.id.share_more_option_ll:
                TDevice.showSystemShareOption((Activity) context,content,title);
                break;
            default:
                break;
        }
        dismiss();
    }

    //分享到微信朋友圈 有待完成。。。
    private void shareToWechatCircle(){
       // UMWXHandler umwxHandler=new UMWXHandler(context,Constants.WEICHAT_APPID);
        //设置微信朋友圈分享内容
        //.............
    }

    //分享到微信
    private void shareToWechat(){
        // UMWXHandler umwxHandler=new UMWXHandler(context,Constants.WEICHAT_APPID);
        //设置微信分享内容
        //WeiXinShareContent weiXinShareContent=new WeiXinShareContent();
       // ShareContent shareContent=new ShareContent();
       // weiXinShareContent.setImage();
    }

    //分享到新浪微博
    private void shareToSinaweibo(){

        /*
        SinaSsoHandler sinaSsoHandler=new SinaSsoHandler();
        sinaSsoHandler.
        */

    }

    //分享到qq
    private void shareToQQ(){
       // UMQQSsoHandler umqqSsoHandler=new UMQQSsoHandler();
       // umqqSsoHandler.set
    }

    private UMImage getShareImage(){
        return new UMImage(context,R.drawable.icon_share);
    }

    private boolean checkCanShare() {
        boolean canShare = true;
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(link)) {
            canShare = false;
        }
        return canShare;
    }

    /*
    public UMSocialService getController() {
        return mController;
    }
   */
}
