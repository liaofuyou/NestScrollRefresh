package me.ajax.nestscrollrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
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
public class RefreshLayout extends LinearLayout implements NestedScrollingParent {

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //在此可以判断参数target是哪一个子view以及滚动的方向，然后决定是否要配合其进行嵌套滚动
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        l("onStartNestedScroll ", target instanceof RecyclerView);
        return target instanceof RecyclerView;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        mAllOffsetDy = 0;
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);

        int scrollY = getScrollY();

        if (scrollY < 0) {
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
            footView.setText("正在刷新");
            footView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 800);
                    invalidate();
                }
            }, 1000);
        }

    }

    int mAllOffsetDy;

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {

        boolean pullDown = dy < 0;

        //向下滑动
        if (pullDown) {

            //在顶部的时候
            if (targetScrollToTop()) {

                double dragIndex = Math.exp(-mAllOffsetDy / 200);
                if (dragIndex < 0) dragIndex = 0;
                dy = (int) (dy * dragIndex);
                mAllOffsetDy += -dy;
                l("========= mAllOffsetDy ", mAllOffsetDy);

                scrollBy(0, dy);
                consumed[1] = dy;

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
                //dy = Math.min(-getScrollY(), dy);

                mAllOffsetDy += -dy;
                scrollBy(0, dy);
                consumed[1] = dy;
/*
                if (headView.state != RefreshState.REFRESH) {
                    headView.onUIPositionChange(getScrollY());
                    refreshFootViewText(getScrollY());
                }*/
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

    private boolean targetScrollToTop() {
        return !recyclerView.canScrollVertically(-1);
    }

    private boolean targetScrollToBottom() {
        return !recyclerView.canScrollVertically(1);
    }

    //后于child滚动
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        l("消费 " + dyConsumed, "未消费 " + dyUnconsumed);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
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


    @Override
    public void scrollTo(int x, int y) {
        /*if (y < -dp(120)) {
            y = -dp(120);
        }
        if (y > dp(50)) {
            y = dp(50);
        }*/
        super.scrollTo(x, y);
    }

    void refreshFootViewText(int scrollY) {
        footView.setText("上拉加载更多");
    }

    public void refreshComplete() {
        mAllOffsetDy = 0;
        headView.setRefreshState(RefreshState.COMPLETE);
        mScroller.startScroll(0, getScrollY(), 0, headView.getMeasuredHeight(), 800);
        invalidate();
    }


    public void loadMoreComplete() {
        //footView.setRefreshState(RefreshState.COMPLETE);
        mAllOffsetDy = 0;
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 800);
        invalidate();
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

}
