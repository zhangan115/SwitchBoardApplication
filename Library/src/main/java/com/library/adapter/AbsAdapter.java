package com.library.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * listView 的adapter 数据绑定
 *
 * @param <T> 数据类型
 */
public abstract class AbsAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> datas;
    private int layoutId;// item布局资源的标识

    public AbsAdapter(Context context, List<T> datas, int layoutId) {
        super();
        this.context = context;
        this.datas = datas;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            vHolder = new ViewHolder(convertView);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        showData(vHolder, getItem(position), position);// 显示数据
        return convertView;
    }

    /**
     * 实现该方法，处理数据与item
     *
     * @param vHolder  view holder
     * @param data     数据
     * @param position 位置
     */
    public abstract void showData(ViewHolder vHolder, T data, int position);

    /**
     * viewHolder
     */
    public static class ViewHolder {
        private SparseArray<View> cacheViews;
        private View itemView;// item布局对象

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            cacheViews = new SparseArray<View>();
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
}
