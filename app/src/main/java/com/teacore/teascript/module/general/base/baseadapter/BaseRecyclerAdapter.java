package com.teacore.teascript.module.general.base.baseadapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teacore.teascript.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-8
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter{
    public static final int STATE_NO_MORE = 1;
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_INVALID_NETWORK = 3;
    public static final int STATE_HIDE = 5;
    public static final int STATE_REFRESHING = 6;
    public static final int STATE_LOAD_ERROR = 7;
    public static final int STATE_LOADING = 8;
    public static final int NEITHER = 0;
    public static final int ONLY_HEADER = 1;
    public static final int ONLY_FOOTER = 2;
    public static final int BOTH_HEADER_FOOTER = 3;
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_HEADER = -1;
    public static final int VIEW_TYPE_FOOTER = -2;
    public final int BEHAVIOR_MODE;

    protected int mState;
    protected List<T> mItemsList;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected View mHeaderView;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    private OnLoadingHeaderCallback onLoadingHeaderCallback;

    public BaseRecyclerAdapter(Context context, int mode) {
        mItemsList = new ArrayList<>();
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        BEHAVIOR_MODE = mode;
        mState = STATE_HIDE;
        initListener();
    }

    public static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }

    public static abstract class OnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            return onLongClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract boolean onLongClick(int position, long itemId);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, long itemId);
    }


    public interface OnItemLongClickListener {
        void onLongClick(int position, long itemId);
    }

    public interface OnLoadingHeaderCallback {
        RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent);

        void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position);
    }

    private void initListener() {
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position, long itemId) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, itemId);
            }
        };

        onLongClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, long itemId) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onLongClick(position, itemId);
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        switch(viewType){
            case VIEW_TYPE_HEADER:
                if(onLoadingHeaderCallback!=null){
                    return onLoadingHeaderCallback.onCreateHeaderHolder(parent);
                }
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(mInflater.inflate(R.layout.view_recycler_footer,parent,false));
            default:
                final RecyclerView.ViewHolder holder=onCreateDefaultViewHolder(parent,viewType);
                if(holder!=null){
                    holder.itemView.setTag(holder);
                    holder.itemView.setOnClickListener(onClickListener);
                    holder.itemView.setOnLongClickListener(onLongClickListener);
                }
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
        switch(holder.getItemViewType()){
            case VIEW_TYPE_HEADER:
                if(onLoadingHeaderCallback!=null){
                    onLoadingHeaderCallback.onBindHeaderHolder(holder,position);
                }
                break;
            case VIEW_TYPE_FOOTER:
                FooterViewHolder footerViewHolder=(FooterViewHolder) holder;
                footerViewHolder.itemView.setVisibility(View.VISIBLE);
                switch(mState){
                    case STATE_INVALID_NETWORK:
                        footerViewHolder.footerTV.setText(mContext.getResources().getString(R.string.state_network_error));
                        footerViewHolder.footerPB.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_MORE:
                    case STATE_LOADING:
                        footerViewHolder.footerTV.setText(mContext.getResources().getString(R.string.state_loading));
                        footerViewHolder.footerPB.setVisibility(View.VISIBLE);
                        break;
                    case STATE_NO_MORE:
                        footerViewHolder.footerTV.setText(mContext.getResources().getString(R.string.state_no_more));
                        footerViewHolder.footerPB.setVisibility(View.GONE);
                        break;
                    case STATE_REFRESHING:
                        footerViewHolder.footerTV.setText(mContext.getResources().getString(R.string.state_refreshing));
                        footerViewHolder.footerPB.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_ERROR:
                        footerViewHolder.footerTV.setText(mContext.getResources().getString(R.string.state_load_error));
                        footerViewHolder.footerPB.setVisibility(View.GONE);
                        break;
                    case STATE_HIDE:
                        footerViewHolder.itemView.setVisibility(View.GONE);
                        break;
                }
                break;
            default:
                onBindDefaultViewHolder(holder, getItems().get(getIndex(position)), position);
                break;
        }
    }

    //当添加到RecyclerView时获取GridLayoutManager布局管理器，修正header和footer显示整行
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == VIEW_TYPE_HEADER || getItemViewType(position) == VIEW_TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    //当RecyclerView在windows活动时获取StraggeredGridLayoutManager布局管理器，修正Header和Footer显示整行
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            if (BEHAVIOR_MODE == ONLY_HEADER) {
                p.setFullSpan(holder.getLayoutPosition() == 0);
            } else if (BEHAVIOR_MODE == ONLY_FOOTER) {
                p.setFullSpan(holder.getLayoutPosition() == mItemsList.size() + 1);
            } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
                if (holder.getLayoutPosition() == 0 || holder.getLayoutPosition() == mItemsList.size() + 1) {
                    p.setFullSpan(true);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER))
            return VIEW_TYPE_HEADER;
        if (position + 1 == getItemCount() && (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER))
            return VIEW_TYPE_FOOTER;
        else return VIEW_TYPE_NORMAL;
    }

    protected int getIndex(int position) {
        return BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER ? position - 1 : position;
    }

    @Override
    public int getItemCount() {
        if (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == ONLY_HEADER) {
            return mItemsList.size() + 1;
        } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
            return mItemsList.size() + 2;
        } else return mItemsList.size();
    }

    public int getCount() {
        return mItemsList.size();
    }

    protected abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type);

    protected abstract void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, T item, int position);

    public final View getHeaderView() {
        return this.mHeaderView;
    }

    public final void setHeaderView(View view) {
        this.mHeaderView = view;
    }

    public final List<T> getItems() {
        return mItemsList;
    }


    public void addAll(List<T> items) {
        if (items != null) {
            this.mItemsList.addAll(items);
            notifyItemRangeInserted(this.mItemsList.size(), items.size());
        }
    }

    public final void addItem(T item) {
        if (item != null) {
            this.mItemsList.add(item);
            notifyItemChanged(mItemsList.size());
        }
    }


    public void addItem(int position, T item) {
        if (item != null) {
            this.mItemsList.add(getIndex(position), item);
            notifyItemInserted(position);
        }
    }

    public void replaceItem(int position, T item) {
        if (item != null) {
            this.mItemsList.set(getIndex(position), item);
            notifyItemChanged(position);
        }
    }

    public void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }


    public final void removeItem(T item) {
        if (this.mItemsList.contains(item)) {
            int position = mItemsList.indexOf(item);
            this.mItemsList.remove(item);
            notifyItemRemoved(position);
        }
    }

    public final void removeItem(int position) {
        if (this.getItemCount() > position) {
            this.mItemsList.remove(getIndex(position));
            notifyItemRemoved(position);
        }
    }

    public final T getItem(int position) {
        int p = getIndex(position);
        if (p < 0 || p >= mItemsList.size())
            return null;
        return mItemsList.get(getIndex(position));
    }

    public final void resetItem(List<T> items) {
        if (items != null) {
            clear();
            addAll(items);
        }
    }

    public final void clear() {
        this.mItemsList.clear();
        setState(STATE_HIDE, false);
        notifyDataSetChanged();
    }

    public void setState(int mState, boolean isUpdate) {
        this.mState = mState;
        if (isUpdate)
            updateItem(getItemCount() - 1);
    }

    public int getState() {
        return mState;
    }

    //添加项点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //添加项点长击事件
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public final void setOnLoadingHeaderCallBack(OnLoadingHeaderCallback listener) {
        onLoadingHeaderCallback = listener;
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar footerPB;
        public TextView footerTV;

        public FooterViewHolder(View view) {
            super(view);
            footerPB = (ProgressBar) view.findViewById(R.id.footer_pb);
            footerTV = (TextView) view.findViewById(R.id.footer_tv);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
