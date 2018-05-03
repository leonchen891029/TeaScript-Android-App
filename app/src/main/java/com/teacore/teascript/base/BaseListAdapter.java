package com.teacore.teascript.base;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.widget.MyURLSpan;
import com.teacore.teascript.widget.TSLinkMovementMethod;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

import java.util.ArrayList;
import java.util.List;

/**继承自BaseAdapter的Adapter基类
 * @author 陈晓帆
 * @version 1.0
 * @created 2017-1-1
 */
public class BaseListAdapter<T extends Entity> extends BaseAdapter{

       //各种状态常量
       public static final int  STATE_EMPTY_ITEM=0;
       public static final int  STATE_LOAD_MORE=1;
       public static final int  STATE_NO_MORE=2;
       public static final int  STATE_NO_DATA=3;
       public static final int  STATE_LESS_ONE_PAGE=4;
       public static final int  STATE_NETWORK_ERROR=5;
       public static final int  STATE_OTHER=6;

       //状态变量,默认情况下是少于一页
       protected int state=STATE_LESS_ONE_PAGE;

       protected int loadMoreText;
       protected int loadFinishText;
       protected int noDataText;

       protected int mScreenWidth;

       private Context mContext;

       private LayoutInflater mLayoutInflater;

       public BaseListAdapter(){
           loadMoreText= R.string.loading;
           loadFinishText=R.string.loading_no_more;
           noDataText=R.string.error_view_no_data;
       }

       public BaseListAdapter(Context context){
           mContext=context;
       }

       //获取LayoutInflater
       public LayoutInflater getLayoutInflater(Context context){
           if (mLayoutInflater==null){
               mLayoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           }
           return mLayoutInflater;

       }

       public void setScreenWidth(int screenWidth){
           mScreenWidth=screenWidth;
       }

       public void setState(int state){
           this.state=state;
       }

       public int getState(){
           return this.state;
       }

       //包含数据实例的列表
       protected ArrayList<T> mDatas=new ArrayList<T>();

       //重写方法getCount(),得到数据总数，根据状态来判断
       @Override
       public int getCount(){
           switch(getState()){
               case STATE_EMPTY_ITEM:
                   return getDataSizePlus();
               case STATE_NETWORK_ERROR:;
                   return getDataSizePlus();
               case STATE_LOAD_MORE:
                   return getDataSizePlus();
               case STATE_NO_DATA:
                   return 1;
               case STATE_NO_MORE:
                   return getDataSizePlus();
               case STATE_LESS_ONE_PAGE:
                   return getDataSize();
               default:
                   break;
           }
           return getDataSize();
       }

       public int getDataSize(){
           return  mDatas.size();
       }

       public int getDataSizePlus(){
           if( hasFooterView() ){
               return  getDataSize()+1;
           }
           return getDataSize();
       }

       //重写getItem()函数，注意这里返回的是一个泛型T
       @Override
       public T getItem(int position){
           if(mDatas.size()>position){
               return mDatas.get(position);
           }
           return null;
       }

       //重写getItemId()函数，返回position
       @Override
       public long getItemId(int position){
           return position;
       }

       //重置数据集
       public void setDatas(ArrayList<T> datas){
           mDatas=datas;
           notifyDataSetChanged();
       }

       //返回数据集
       public ArrayList<T> getDatas(){
           return mDatas==null?(mDatas=new ArrayList<T>()):mDatas;
       }

       //增加数据集
       public void addDatas(List<T> datas){
           if(mDatas !=null && datas !=null && !datas.isEmpty() ){
               mDatas.addAll(datas);
           }
           notifyDataSetChanged();
       }

       //添加单个数据
       public void addItem(T obj){
           if(mDatas!=null){
               mDatas.add(obj);
           }
           notifyDataSetChanged();
       }

       //在固定位置上面添加数据
       public void addItem(int pos,T obj){
           if (mDatas!=null){
               mDatas.add(pos, obj);
           }
           notifyDataSetChanged();
       }

       //删除单个数据
       public void removeItem(T obj){
           mDatas.remove(obj);
           notifyDataSetChanged();
       }

       //删除所有数据
       public void clear(){
           mDatas.clear();
           notifyDataSetChanged();
       }

       //设置加载更多的文本提示
       public void setLoadMoreText(int loadMoreText){
           this.loadMoreText=loadMoreText;
       }

       //设置加载结束的文本提示
       public void setLoadFinishText(int loadFinishText){
          this.loadFinishText=loadFinishText;
       }

       //设置没有数据的文本提示
       public void setNoDataText(int noDataText){
          this.noDataText=noDataText;
       }

       protected boolean footViewHasBg(){
             return true;
       }

       private LinearLayout mFooterView;

       @Override
       public View getView(int position,View convertView,ViewGroup parent){

           //如果此时是在倒数第一个item并且有footerview(代表已经到了footview)
           if( position==getCount()-1 && hasFooterView() ) {
               //除了没有数据和少于一页两种状态
               if (getState() == STATE_NO_MORE || getState() == STATE_LOAD_MORE || state == STATE_EMPTY_ITEM
                       || getState() == STATE_NETWORK_ERROR) {

                   //加载底部的FootView
                   this.mFooterView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell_footer_view, null);

                   if (!footViewHasBg()) {
                       mFooterView.setBackgroundResource(0);
                   }

                   ProgressBar progressBar = (ProgressBar) mFooterView.findViewById(R.id.footerview_pb);

                   TextView textView = (TextView) mFooterView.findViewById(R.id.footerview_tv);

                   switch (getState()) {
                       //加载更多状态
                       case STATE_LOAD_MORE:
                           setFooterViewLoading();
                           break;
                       //不能加载更多状态
                       case STATE_NO_MORE:
                           mFooterView.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.GONE);
                           textView.setVisibility(View.VISIBLE);
                           textView.setText(loadFinishText);
                           break;
                       //暂无内容状态
                       case STATE_EMPTY_ITEM:
                           mFooterView.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.GONE);
                           textView.setVisibility(View.VISIBLE);
                           textView.setText(noDataText);
                           break;
                       //网络错误状态
                       case STATE_NETWORK_ERROR:
                           mFooterView.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.GONE);
                           textView.setVisibility(View.VISIBLE);
                           //判断是否有可用的网络
                           if (TDevice.hasInternet()) {
                               textView.setText("加载出错了");
                           } else {
                               textView.setText("没有可用的网络");
                           }
                           break;
                       default:
                           mFooterView.setVisibility(View.GONE);
                           progressBar.setVisibility(View.GONE);
                           textView.setVisibility(View.GONE);
                           break;
                   }
                   return mFooterView;
               }
           }

           if(position<0){
               position=0;
           }

           return getRealView(position,convertView,parent);
       }

       //返回普通的item的view
       protected View getRealView(int position,View convertView,ViewGroup parent){
           return null;
       }

       protected boolean hasFooterView(){
           return true;
       }

       public View getFooterView(){
           return this.mFooterView;
       }

       //设置加载状态下的footview
       public void setFooterViewLoading(String loadMsg){
           ProgressBar progressBar=(ProgressBar) mFooterView.findViewById(R.id.footerview_pb);
           TextView textView=(TextView) mFooterView.findViewById(R.id.footerview_tv);
           mFooterView.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.VISIBLE);
           textView.setVisibility(View.VISIBLE);

           if(StringUtils.isEmpty(loadMsg)){
               textView.setText(loadMoreText);
           }else{
               textView.setText(loadMsg);
           }


       }

       public void setFooterViewLoading(){
           setFooterViewLoading("");
       }

       public void setFooterViewText(String msg){
           ProgressBar progressBar=(ProgressBar) mFooterView.findViewById(R.id.footerview_pb);
           TextView    textView=(TextView) mFooterView.findViewById(R.id.footerview_tv);
           mFooterView.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.VISIBLE);
           textView.setVisibility(View.VISIBLE);
           textView.setText(msg);
       }

       //设置TeatimeTextView的内容
       protected void setContent(TeatimeTextView contentView, String content){

           contentView.setMovementMethod(TSLinkMovementMethod.getMovementMethod());
           contentView.setFocusable(false);
           contentView.setDispatchToParent(true);
           contentView.setLongClickable(false);
           Spanned span= Html.fromHtml(TeatimeTextView.modifyPath(content));
           span= EmojiInputUtils.displayEmoji(contentView.getResources(),span.toString());
           contentView.setText(span);
           MyURLSpan.parseLinkText(contentView, span);
       }

       protected void setText(TextView textView,String text,boolean needGone){
           if(text==null|| TextUtils.isEmpty(text)){
               if(needGone){
                   textView.setVisibility(View.GONE);
               }
           }else{
            textView.setText(text);
           }
       }

       protected void setText(TextView textView,String text){
           setText(textView,text,false);
       }

       protected void setImageRes(ImageView imageRes,int resId){
           imageRes.setImageResource(resId);
       }

}




