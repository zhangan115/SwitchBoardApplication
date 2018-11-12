package com.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.sito.library.R;


/**
 * 扩展的SwipeRefreshLayout
 */
public class RecycleRefreshLoadLayout extends SwipeRefreshLayout {

    private Context context;//环境
    private View viewFooter;// 底部View
    private ExpendRecycleView expendRecycleView;// 内部包含的子控件
    private OnLoadListener mLoadListener;   // 加载更多数据的监听
    private boolean isLoading = false;// 是否正在加载
    private boolean isStopLoad = false;//停止加载更多
    private float downY; // 按下的y坐标
    private float lastY; // 最后抬起的y坐标
    private boolean isPullUp; // 是否上拉

    public interface OnLoadListener {
        void onLoadMore();
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int touchSlop = 100;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                lastY = ev.getY();
                isPullUp = (touchSlop <= downY - lastY);
                break;
            case MotionEvent.ACTION_UP:
                isPullUp = (touchSlop <= downY - lastY);
        }
        return super.dispatchTouchEvent(ev);
    }

    public RecycleRefreshLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (expendRecycleView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) instanceof ExpendRecycleView) {
                    expendRecycleView = (ExpendRecycleView) getChildAt(i);
                    break;
                }
            }
        }
        if (expendRecycleView != null) {
            expendRecycleView.addOnScrollListener(scrollListener);
        }
    }

    public void setViewFooter(View viewFooter) {
        this.viewFooter = viewFooter;
    }

    @SuppressLint("InflateParams")
    public void setLoading(boolean loading, boolean isNoMore) {
        this.isLoading = loading;
        if (viewFooter == null) {
            viewFooter = LayoutInflater.from(context).inflate(R.layout.view_load_more, null);
        }
        if (loading) {
            viewFooter.findViewById(R.id.ll_load_data).setVisibility(View.VISIBLE);
            viewFooter.findViewById(R.id.ll_no_more_data).setVisibility(View.GONE);
            expendRecycleView.addFootView(viewFooter);
            expendRecycleView.getFootView().get(0).setVisibility(VISIBLE);
        } else {
            // 正在加载
            if (isNoMore) {
                //没有更多数据
                viewFooter.findViewById(R.id.ll_load_data).setVisibility(View.GONE);
                viewFooter.findViewById(R.id.ll_no_more_data).setVisibility(View.VISIBLE);
                expendRecycleView.addFootView(viewFooter);
                if (expendRecycleView.getFootView().size() > 0) {
                    expendRecycleView.getFootView().get(0).setVisibility(VISIBLE);
                }
            }
            // 加载完成(没有更多数据，移除)
            if (!isNoMore && expendRecycleView != null) {
                expendRecycleView.removeFooterView(viewFooter);
            }
        }
        if (expendRecycleView != null) {
            expendRecycleView.post(new Runnable() {
                @Override
                public void run() {
                    expendRecycleView.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }

    public void loadFinish() {
        setLoading(false, false);
    }

    public void setNoMoreData(boolean isNoMoreData) {
        isStopLoad = isNoMoreData;
        setLoading(false, isNoMoreData);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isStopLoad) {
                return;
            }
            if (mLoadListener != null && !isLoading && isPullUp) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int lastVisibleItemPosition;
                if (layoutManager instanceof GridLayoutManager) {
                    lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                    lastVisibleItemPosition = findMax(into);
                } else {
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }
                if (layoutManager.getChildCount() > 0
                        && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {
                    setLoading(true, false);
                    mLoadListener.onLoadMore();
                }
            }
        }
    };

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
