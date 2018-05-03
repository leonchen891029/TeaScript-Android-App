package com.teacore.teascript.widget.dialog;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

/**对话框工具
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-20
 */
public class DialogUtils {

    //获取一个普通的对话框
    public static AlertDialog.Builder getDialog(Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        return builder;
    }

    //获取一个耗时等待对话框
    public static ProgressDialog getProgressDialog(Context context,String message){
        ProgressDialog waitDialog=new ProgressDialog(context);

        if(!TextUtils.isEmpty(message)){
            waitDialog.setMessage(message);
        }

        return waitDialog;
    }

    //获取一个信息对话框
    public static AlertDialog.Builder getMessageDialog(Context context,String message,DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder=getDialog(context);

        builder.setMessage(message);
        builder.setPositiveButton("确定",onClickListener);

        return builder;
    }

    public static AlertDialog.Builder getMessageDialog(Context context,String message){
        return getMessageDialog(context,message,null);
    }

    //获取一个确认对话框
    public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定", onClickListener);
        builder.setNegativeButton("取消", null);
        return builder;
    }
    public static AlertDialog.Builder getConfirmDialog(Context context,String message
            ,DialogInterface.OnClickListener onOKClickListener,DialogInterface.OnClickListener onCancelClickListener){

        AlertDialog.Builder builder=getDialog(context);

        builder.setMessage(message);
        builder.setPositiveButton("确定", onOKClickListener);
        builder.setNegativeButton("取消", onCancelClickListener);

        return builder;
    }

    //获取一个确认对话框(自定义两个button的信息)
    public static AlertDialog.Builder getConfirmDialog(Context context,String message,String okString,String cancelString
            ,DialogInterface.OnClickListener onOKClickListener,DialogInterface.OnClickListener onCancelClickListener){

      return getConfirmDialog(context, "", message, okString, cancelString, onOKClickListener, onCancelClickListener);
    }

    //获取一个确认对话框(自定义两个button信息，标题栏)
    public static AlertDialog.Builder getConfirmDialog(Context context,
                                                       String title,
                                                       String message,
                                                       String okString,
                                                       String cancleString,
                                                       DialogInterface.OnClickListener onOkClickListener,
                                                       DialogInterface.OnClickListener onCancleClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(okString, onOkClickListener);
        builder.setNegativeButton(cancleString, onCancleClickListener);
        return builder;
    }

    //获取一个多选对话框
    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setItems(arrays, onClickListener);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays, DialogInterface.OnClickListener onClickListener) {
        return getSelectDialog(context, "", arrays, onClickListener);
    }

    //获取一个当选对话框
    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        return getSingleChoiceDialog(context, "", arrays, selectIndex, onClickListener);
    }


}
