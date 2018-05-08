package me.ajax.nestscrollrefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by aj on 2018/5/4
 */

public class HeadView extends LinearLayout {

    TextView textView;
    ImageView arrowView;
    ProgressBar progressBar;
    RefreshState state;

    float density = 1F;

    public HeadView(Context context) {
        super(context);
        init();
    }

    public HeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        density = getResources().getDisplayMetrics().density;

        View.inflate(getContext(), R.layout.header_view, this);
        textView = findViewById(R.id.text_view);
        arrowView = findViewById(R.id.arrow_view);
        progressBar = findViewById(R.id.progress_bar);

        setRefreshState(RefreshState.START);
    }

    public void onUIPositionChange(int scrollY) {

        //刷新和完成，状态不随着位置变化
        if (state == RefreshState.REFRESH || state == RefreshState.COMPLETE) return;


        if (Math.abs(scrollY) > getMeasuredHeight()) {
            if (state == RefreshState.START) {
                arrowView.animate().rotation(180).start();
            }
            setRefreshState(RefreshState.PULL);
        } else {
            if (state == RefreshState.PULL) {
                arrowView.animate().rotation(0).start();
            }
            setRefreshState(RefreshState.START);
        }
    }

    public void setRefreshState(RefreshState state) {
        if (this.state == state) return;

        arrowView.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        this.state = state;
        if (state == RefreshState.START) {

            textView.setText("下拉刷新");
            arrowView.setVisibility(VISIBLE);
        } else if (state == RefreshState.PULL) {

            textView.setText("释放刷新");
            arrowView.setVisibility(VISIBLE);
        } else if (state == RefreshState.REFRESH) {

            textView.setText("正在刷新");
            progressBar.setVisibility(VISIBLE);
            arrowView.setRotation(180);
        } else if (state == RefreshState.COMPLETE) {

            textView.setText("已更新");
            arrowView.setRotation(0);
        }
    }

    private int dp(float dp) {
        return (int) (density * dp + 0.5F);
    }
}
