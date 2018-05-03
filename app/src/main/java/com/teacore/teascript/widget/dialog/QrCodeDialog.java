package com.teacore.teascript.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.util.FileUtils;
import com.teacore.teascript.util.QrCodeUtils;


/**
 * 二维码对话框
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */

public class QrCodeDialog extends Dialog {

    private ImageView mCodeIV;
    private Bitmap mBitmap;
    private Context mContext;

    private QrCodeDialog(Context context,boolean flag,OnCancelListener listener){
        super(context,flag,listener);
        mContext=context;
    }

    private QrCodeDialog(Context context,int defStyle){
        super(context,defStyle);
        View contentView=getLayoutInflater().inflate(R.layout.dialog_qr_code,null);
        mCodeIV=(ImageView) contentView.findViewById(R.id.qr_code_iv);
        try{
            mBitmap= QrCodeUtils.create2DCode(String.format("http://my.teascript.com/u/%s", AppContext.getInstance().getLoginUid()));
            mCodeIV.setImageBitmap(mBitmap);
        }catch(WriterException e){
            e.printStackTrace();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCodeIV.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                dismiss();
                if (FileUtils.bitmapToFile(mBitmap,
                        FileUtils.getSavePath(mContext,"teascript") + "/myqrcode.png")) {
                    AppContext.showToast("二维码已保存到teascript文件夹下");
                } else {
                    AppContext.showToast("SD卡不可写，二维码保存失败");
                }
                return false;
            }
        });
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                QrCodeDialog.this.dismiss();
                return false;
            }
        });
        super.setContentView(contentView);

    }

    public QrCodeDialog(Context context) {
        this(context, R.style.quick_option_dialog);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(p);
    }


}
