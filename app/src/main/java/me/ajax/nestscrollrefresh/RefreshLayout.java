package me.ajax.nestscrollrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by AJ on 2018/5/3
 */
public class RefreshLayout extends LinearLayout implements NestedScrollingParent2 {

    private HeadView headView;
    private TextView footView;
    private RecyclerView recyclerView;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private Scroller mScroller;
    private OnLoadListener onLoadListener;

    public RefreshLayout(Context context) {
        super(context);
    }


    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mScroller = new Scroller(getContext());
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        int p = dp(8);

        headView = new HeadView(getContext());
        addView(headView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        footView = new TextView(getContext());
        footView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        footView.setGravity(Gravity.CENTER);
        footView.setPadding(p, p, p, p);
        footView.setText("上拉加载更多");
        addView(footView);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof RecyclerView) {
                recyclerView = (RecyclerView) getChildAt(i);
            }
        }
    }


    int mAllOffsetDy;

    private boolean targetScrollToTop() {
        return !recyclerView.canScrollVertically(-1);
    }

    private boolean targetScrollToBottom() {
        return !recyclerView.canScrollVertically(1);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        l("onNestedFling");
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        l("onNestedPreFling" + getScrollY());
        return getScrollY() != 0;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        headView.layout(0, -headView.getMeasuredHeight(), r, 0);
        recyclerView.layout(l, t, r, b);
        footView.layout(0, b, r, b + footView.getMeasuredHeight());
    }

    void refreshFootViewText(int scrollY) {
        footView.setText("上拉加载更多");
    }

    public void refreshComplete() {
        mAllOffsetDy = 0;
        //footView.setRefreshState(RefreshState.COMPLETE);
        headView.setRefreshState(RefreshState.COMPLETE);
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 800);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                headView.setRefreshState(RefreshState.START);
            }
        }, 800);
        invalidate();
    }


    public void loadMoreComplete() {
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    private int dp(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5F);
    }

    static void l(Object... list) {
        StringBuilder text = new StringBuilder();
        for (Object o : list) {
            text.append("   ").append(o.toString());
        }
        Log.e("######", text.toString());
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return target instanceof RecyclerView;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        mAllOffsetDy = 0;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, int[] consumed, int type) {

        boolean pullDown = dy < 0;

        //向下滑动
        if (pullDown) {

            //在顶部的时候
            if (targetScrollToTop()) {


                double dragIndex = Math.exp(-mAllOffsetDy / 200);
                if (dragIndex < 0) dragIndex = 0;
                dy = (int) (dy * dragIndex);
                mAllOffsetDy += -dy;
                consumed[1] = dy;

                //在刷新中的时候，就不要下拉啦！
                if (headView.state == RefreshState.REFRESH
                        && -getScrollY() >= headView.getMeasuredHeight()) {
                    return;
                }

                scrollBy(0, dy);
                headView.onUIPositionChange(getScrollY());
                refreshFootViewText(getScrollY());
            }

            //在底部部的时候
            if (targetScrollToBottom() && getScrollY() > 0) {

                dy = Math.max(-getScrollY(), dy);

                //scrollBy(0, dy);
                //consumed[1] = dy;

                //headView.onUIPositionChange(getScrollY());
                //refreshFootViewText(getScrollY());

            }
        }
        //向上滑动
        else {

            //在顶部的时候
            if (getScrollY() < 0) {
                l("向上滑动 在顶部的时候 ", dy, -getScrollY());
                dy = Math.min(-getScrollY(), dy);

                mAllOffsetDy += -dy;
                scrollBy(0, dy);
                consumed[1] = dy;

                if (headView.state != RefreshState.REFRESH) {
                    headView.onUIPositionChange(getScrollY());
                    refreshFootViewText(getScrollY());
                }
            }

            //在底部部的时候
            if (targetScrollToBottom()) {
                //scrollBy(0, dy);
                //consumed[1] = dy;
                //headView.onUIPositionChange(getScrollY());
                //refreshFootViewText(getScrollY());
            }
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {

        mNestedScrollingParentHelper.onStopNestedScroll(target);

        int scrollY = getScrollY();

        if (scrollY < 0) {
            if (headView.state == RefreshState.REFRESH) {
                return;
            }

            if (Math.abs(scrollY) < dp(60)) {
                headView.setRefreshState(RefreshState.START);
                mScroller.startScroll(0, scrollY, 0, -scrollY, 800);

                mAllOffsetDy = 0;
            } else {
                headView.setRefreshState(RefreshState.REFRESH);
                if (onLoadListener != null) {
                    onLoadListener.refresh();
                }
                int dy = -(scrollY + headView.getMeasuredHeight());
                mScroller.startScroll(0, scrollY, 0, dy, 400);

                mAllOffsetDy = -(scrollY + dy);
            }
            invalidate();
        }
        if (scrollY > 0) {
            /*
            footView.setText("正在刷新");
            footView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 800);
                    invalidate();
                }
            }, 1000);
            */
        }

    }

}
