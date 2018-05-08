package me.ajax.nestscrollrefresh;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by aj on 2018/4/23
 */
//自定义Adapter
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<String> myData = new ArrayList<>();

    MyAdapter() {

        int size = 10;
        myData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            myData.add("TEXT " + (i + 1));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setStr(myData.get(position));
        //holder.setStr(position + "");
    }

    public void setDatas(List<String> list) {
        myData.clear();
        addDatas(list);
    }


    public void addDatas(List<String> list) {
        myData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    //自定义Holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView strTv;

        Random random = new Random();

        public MyViewHolder(View itemView) {
            super(itemView);
            strTv = (TextView) itemView.findViewById(R.id.text);

            //itemView.setBackgroundColor(getRandomColor());
        }

        int getRandomColor() {
            StringBuilder text = new StringBuilder("#FF");
            for (int i = 0; i < 6; i++) {
                text.append(random.nextInt(9));
            }
            return Color.parseColor(text.toString());
        }

        public void setStr(String str) {
            strTv.setText(str);
        }

    }
}

