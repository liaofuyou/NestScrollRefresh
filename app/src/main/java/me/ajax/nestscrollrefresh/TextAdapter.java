package me.ajax.nestscrollrefresh;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aj on 2018/4/23
 */
//自定义Adapter
public class TextAdapter extends RecyclerView.Adapter<TextAdapter.MyViewHolder> {

    private ArrayList<String> myData = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setStr(myData.get(position));
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

        public MyViewHolder(View itemView) {
            super(itemView);
            strTv = (TextView) itemView.findViewById(R.id.text);
        }

        public void setStr(String str) {
            strTv.setText(str);
        }

    }
}

