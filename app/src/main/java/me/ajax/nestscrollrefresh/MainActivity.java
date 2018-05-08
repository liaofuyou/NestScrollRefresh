package me.ajax.nestscrollrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RefreshLayout refreshLayout;
    MyAdapter myAdapter;

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
                        myAdapter.setDatas(getDatas(0));
                        refreshLayout.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.addDatas(getDatas(myAdapter.getItemCount()));
                        refreshLayout.loadMoreComplete();
                    }
                }, 2000);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter = new MyAdapter());

        myAdapter.addDatas(getDatas(0));
    }

    List<String> getDatas(int baseIndex) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("TEXT " + (baseIndex + i + 1));
        }
        return list;
    }
}
