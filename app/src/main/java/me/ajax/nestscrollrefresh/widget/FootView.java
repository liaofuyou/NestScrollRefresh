package me.ajax.nestscrollrefresh.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by aj on 2018/5/4
 */

public class FootView extends HeadView {


    public FootView(Context context) {
        super(context);
    }

    public FootView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FootView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRefreshState(RefreshState state) {
        if (this.state == state) return;

        arrowView.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        this.state = state;
        if (state == RefreshState.START) {

            textView.setText("上拉加载更多");
        } else if (state == RefreshState.PULL) {

            textView.setText("上拉加载更多");
        } else if (state == RefreshState.REFRESH) {

            textView.setText("正在刷新");
            progressBar.setVisibility(VISIBLE);
        } else if (state == RefreshState.COMPLETE) {

            textView.setText("已更新");
        }
    }
}
