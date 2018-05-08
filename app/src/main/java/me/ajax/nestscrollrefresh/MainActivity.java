package me.ajax.nestscrollrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.ajax.nestscrollrefresh.widget.OnLoadListener;
import me.ajax.nestscrollrefresh.widget.RefreshLayout;

public class MainActivity extends AppCompatActivity {

    RefreshLayout refreshLayout;
    TextAdapter textAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnLoadListener(new OnLoadListener() {
            @Override
            public void refresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textAdapter.setDatas(getDatas(0));
                        refreshLayout.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void loadMore() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textAdapter.addDatas(getDatas(textAdapter.getItemCount()));
                        refreshLayout.loadMoreComplete();
                    }
                }, 1000);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(textAdapter = new TextAdapter());

        textAdapter.setDatas(getDatas(0));
    }

    List<String> getDatas(int baseIndex) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("" + (baseIndex + i + 1));
        }
        return list;
    }
}
