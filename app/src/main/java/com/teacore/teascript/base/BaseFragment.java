package com.teacore.teascript.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.interfaces.BaseFragmentInterface;
import com.teacore.teascript.widget.dialog.DialogControl;

/**Fragment基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-5
 */
public class BaseFragment extends Fragment implements View.OnClickListener,BaseFragmentInterface {

       //Fragment的状态常量
       public static final int STATE_NONE=0;
       public static final int STATE_REFRESH=1;
       public static final int STATE_LOADMORE=2;
       public static final int STATE_NOMORE=3;
       //正在下拉但还没有到刷新的状态
       public static final int STATE_PRESSNONE=4;

       public static int mState=STATE_NONE;

       protected LayoutInflater mLayoutInflater;

       public AppContext getApplication(){
           return (AppContext) getActivity().getApplication();
       }

       @Override
       public void onCreate(Bundle savedInstanceState){
           super.onCreate(savedInstanceState);
       }

       @Override
       public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
           this.mLayoutInflater=inflater;
           View view=super.onCreateView(inflater,container,savedInstanceState);
           return view;
       }

       //实现BaseFragmentInterface的initView方法
       @Override
       public void initView(View view){
       }
       //实现BaseFragmentInterface的initData方法
       @Override
       public void initData(){
       }

       @Override
       public void onClick(View view){
       }

       @Override
       public void onResume(){
           super.onResume();
       }

       @Override
       public void onPause(){
           super.onPause();
       }

       @Override
       public void onDestroy(){
           super.onDestroy();
       }

       protected int getLayoutId(){
           return 0;
       }

       protected View inflateView(int resId){
           return this.mLayoutInflater.inflate(resId,null);
       }

       public boolean onBackPressed(){
           return false;
       }

       protected ProgressDialog showWaitDialog(String str){
           FragmentActivity activity=(FragmentActivity) getActivity();
           if(activity instanceof DialogControl){
               return ((DialogControl) activity).showWaitDialog(str);
           }
           return null;
       }

       protected ProgressDialog showWaitDialog(int resId){
           FragmentActivity activity=(FragmentActivity) getActivity();
           if(activity instanceof DialogControl){
               return ((DialogControl) activity).showWaitDialog(resId);
           }
           return null;
       }

       protected ProgressDialog showWaitDialog(){
            return showWaitDialog(R.string.loading);
    }

       protected void hideWaitDialog(){
         FragmentActivity activity=(FragmentActivity) getActivity();
         if(activity instanceof DialogControl){
            ((DialogControl) activity).hideWaitDialog();
         }
      }

}





























