package com.library.adapter;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView 的Adapter
 * Created by zhangan on 2016/1/27.
 */
public abstract class RVAdapter<T> extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    //布局文件
    private final int layoutId;
    //手指离开屏幕但是RecycleView 依旧在滚动，不加载图片
    private boolean isScrolling;
    //数据
    private List<T> datas;
    //打气筒
    private LayoutInflater inflater;
    //点击监听事件
    private OnItemClickListener listener;
    //长按监听事件
    private OnItemLongClickListener longListener;

    /**
     * 点击事件的接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 长按事件的接口
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongListener(OnItemLongClickListener listener) {
        this.longListener = listener;
    }

    /**
     * @param view         RecycleView
     * @param datas        数据
     * @param itemLayoutId 布局id
     */
    public RVAdapter(RecyclerView view, List<T> datas, int itemLayoutId) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.inflater = LayoutInflater.from(view.getContext());
        this.datas = datas;
        layoutId = itemLayoutId;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(layoutId, parent, false));
    }


    /**
     * 绑定数据
     *
     * @param holder   ViewHolder
     * @param position 位置
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        showData(holder, datas.get(position), position);
        if (listener != null) {
            holder.itemView.setOnClickListener(getOnClickListener(position));
        }
        if (longListener != null) {
            holder.itemView.setOnLongClickListener(getOnLongClickListener(position));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 抽象方法
     *
     * @param vHolder  ViewHolder
     * @param data     数据
     * @param position 位置
     */
    public abstract void showData(ViewHolder vHolder, T data, int position);


    /**
     * 点击事件
     *
     * @param position 位置
     * @return OnClickListener
     */
    public View.OnClickListener getOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(@Nullable View v) {
                if (listener != null && v != null) {
                    listener.onItemClick(v, position);
                }
            }
        };
    }

    /**
     * 长按事件
     *
     * @param position 位置
     * @return OnLongClickListener
     */
    public View.OnLongClickListener getOnLongClickListener(final int position) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longListener != null && v != null) {
                    longListener.onItemLongClick(v, position);
                }
                return false;
            }
        };
    }

    /**
     * ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> cacheViews;
        private View itemView;// item布局对象

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cacheViews = new SparseArray<>();
        }

        public View getView(int id) {
            View view = cacheViews.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                if (view != null) {
                    // 将已经查找到的控件放到控件缓存中去，以被直接获取，不需要再次调用findViewById中查找
                    cacheViews.put(id, view);
                }
            }
            return view;
        }
    }

    private boolean canScrollDown(RecyclerView recyclerView) {
        return ViewCompat.canScrollVertically(recyclerView, 1);
    }
}
