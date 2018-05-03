package com.teacore.teascript.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.util.TDevice;

/**Dialog的基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-1
 */
public class BaseDialog extends Dialog {

    public DialogInterface.OnClickListener listener;

    protected View dialog;
    protected DialogTitleView dialogHeader;
    protected FrameLayout dialogContent;
    protected View barDivider;
    protected View buttonDivider;
    protected Button positiveButton;
    protected Button negativeButton;

    private final int contentPadding;

    protected DialogInterface.OnClickListener dismissClick=new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public BaseDialog(Context context){
        this(context,R.style.dialog_common);
    }

    public BaseDialog(Context context, int defStyle){
        super(context,defStyle);

        //view getcontext得到的是activity
        contentPadding=(int) getContext().getResources().getDimension(R.dimen.global_dialog_padding);

        init(context);
    }

    protected BaseDialog(Context context, boolean flag, DialogInterface.OnCancelListener listener){
        super(context,flag,listener);
        contentPadding = (int) getContext().getResources().getDimension(R.dimen.global_dialog_padding);
        init(context);
    }

    //初始化创建dialogContent
    protected void init(final Context context){
        setCancelable(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog= LayoutInflater.from(context).inflate(R.layout.dialog_base,null);
        dialogHeader=(DialogTitleView) dialog.findViewById(R.id.dialog_header);
        dialogContent=(FrameLayout) dialog.findViewById(R.id.dialog_content);
        barDivider=dialog.findViewById(R.id.button_bar_divider);
        buttonDivider=dialog.findViewById(R.id.button_divider);
        positiveButton=(Button) dialog.findViewById(R.id.positive_btn);
        negativeButton=(Button) dialog.findViewById(R.id.negative_btn);

        super.setContentView(dialog);
    }

    public TextView getTitleTextView(){
        return dialogHeader.titleTv;
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        if(TDevice.isTablet()){

            int maxWidth=(int) TDevice.dpToPixels(360f);

            if(maxWidth<TDevice.getScreenWidth()){
                WindowManager.LayoutParams params=getWindow().getAttributes();
                params.width=maxWidth;
                getWindow().setAttributes(params);
            }

        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.dismiss();
    }

    //setContent用于在container中设置view
    public void setContent(View view) {
        setContent(view, contentPadding);
    }

    public void setContent(View view, int padding) {
        dialogContent.removeAllViews();
        dialogContent.setPadding(padding, padding, padding, padding);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        dialogContent.addView(view, lp);
    }

    //子类不要使用，因为子类都是使用setContent改变container中的内容
    @Override
    public void setContentView(int i) {
        setContent(null);
    }

    @Override
    public void setContentView(View view) {
        setContentView(null, null);
    }

    @Override
    public void setContentView(View view,
                               android.view.ViewGroup.LayoutParams layoutparams) {
        throw new Error("Dialog: User setContent (View view) instead!");
    }

    public void setItems(BaseAdapter adapter,
                         AdapterView.OnItemClickListener onItemClickListener) {
        ListView listview = new ListView(dialogContent.getContext());
        listview.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        listview.setDivider(null);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(onItemClickListener);
        setContent(listview, 0);
    }

    public void setItems(CharSequence[] items,
                         AdapterView.OnItemClickListener onItemClickListener) {
        ListView listview = new ListView(dialogContent.getContext());
        listview.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        listview.setAdapter(new DialogAdapter(items));
        listview.setDivider(null);
        listview.setOnItemClickListener(onItemClickListener);
        setContent(listview, 0);
    }

    public void setMessage(int resId) {
        setMessage(getContext().getResources().getString(resId));
    }

    public void setMessage(Spanned spanned) {

        ScrollView scrollView = new ScrollView(getContext());

        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        TextView tvMessage = new TextView(getContext(), null,
                R.style.dialog_pinterest_text);

        tvMessage.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        tvMessage.setPadding(contentPadding, contentPadding, contentPadding,
                contentPadding);
        tvMessage.setLineSpacing(0.0F, 1.3F);
        tvMessage.setText(spanned);
        tvMessage.setTextColor(getContext().getResources().getColor(
                R.color.black));

        ScrollView.LayoutParams lp = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT);
        scrollView.addView(tvMessage, lp);
        setContent(scrollView, 0);
    }

    public void setMessage(String message) {
        setMessage(Html.fromHtml(message));
    }

    //设置取消按钮
    public void setNegativeButton(int negative,
                                  DialogInterface.OnClickListener listener) {
        setNegativeButton(getContext().getString(negative), listener);
    }

    public void setNegativeButton(String text,
                                  final DialogInterface.OnClickListener listener) {

        if (!TextUtils.isEmpty(text)) {

            negativeButton.setText(text);
            negativeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(BaseDialog.this, 0);
                    else
                        dismissClick.onClick(BaseDialog.this, 0);
                }
            });

            negativeButton.setVisibility(View.VISIBLE);

            if (positiveButton.getVisibility() == View.VISIBLE)
                buttonDivider.setVisibility(View.VISIBLE);
        } else {
              negativeButton.setVisibility(View.GONE);
              buttonDivider.setVisibility(View.GONE);
        }

        if (positiveButton.getVisibility() == View.VISIBLE
                || positiveButton.getVisibility() == View.VISIBLE)
             barDivider.setVisibility(View.VISIBLE);
        else
            barDivider.setVisibility(View.GONE);
    }

    public void setPositiveButton(int positive,
                                  DialogInterface.OnClickListener listener) {
        setPositiveButton(getContext().getString(positive), listener);
    }

    public void setPositiveButton(String positive,
                                  final DialogInterface.OnClickListener listener) {
        if (!TextUtils.isEmpty(positive)) {
            positiveButton.setText(positive);
            positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(BaseDialog.this, 0);
                    else
                        dismissClick.onClick(BaseDialog.this, 0);
                }
            });

            positiveButton.setVisibility(View.VISIBLE);

            if (negativeButton.getVisibility() == View.VISIBLE)
                buttonDivider.setVisibility(View.VISIBLE);
        } else {
            positiveButton.setVisibility(View.GONE);
            buttonDivider.setVisibility(View.GONE);
        }
        if (positiveButton.getVisibility() == View.VISIBLE
                || negativeButton.getVisibility() == View.VISIBLE)
            barDivider.setVisibility(View.VISIBLE);
        else
            barDivider.setVisibility(View.GONE);
    }

    //设置dialogtitleview的文本内容
    public void setSubTitle(int i) {
        setSubTitle((getContext().getResources().getString(i)));
    }

    public void setSubTitle(CharSequence subtitle) {
        if (subtitle != null && subtitle.length() > 0) {
            dialogHeader.subTitleTv.setText(subtitle);
            dialogHeader.subTitleTv.setVisibility(View.VISIBLE);
        } else {
            dialogHeader.subTitleTv.setVisibility(View.GONE);
        }
    }

    //设置自己的title
    @Override
    public void setTitle(int title) {
        setTitle((getContext().getResources().getString(title)));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title != null && title.length() > 0) {
            dialogHeader.titleTv.setText(title);
            dialogHeader.setVisibility(View.VISIBLE);
        } else {
            dialogHeader.setVisibility(View.GONE);
        }
    }

}
